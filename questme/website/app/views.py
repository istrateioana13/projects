from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth import login, logout, authenticate, get_user_model
from django.contrib import messages
from django.contrib.auth.decorators import login_required
from .forms import UserRegisterForm, UserLoginForm
from .models import User, Quiz, Question, Answer, UserAnswer, UserMobile
from rest_framework import generics
from .serializers import QuizListSerializer, QuizDetailSerializer
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
import json
from collections import defaultdict
from django.http import HttpResponse
from django.template.loader import get_template
from xhtml2pdf import pisa
import fitz  # PyMuPDF
import requests
from django.template.loader import render_to_string
from django.core.mail import EmailMessage, get_connection
import random
from django.utils import timezone
from django.utils.dateparse import parse_datetime
from django.views.decorators.http import require_POST
from django.urls import reverse
from collections import defaultdict
from rest_framework.decorators import api_view
from rest_framework.response import Response


# Inregistrare si autentificare aplicatie mobila
@csrf_exempt
def login_mobile(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            email = data.get('email', '').strip().lower()
            password = data.get('password', '').strip()

            try:
                user = UserMobile.objects.get(email=email)
            except UserMobile.DoesNotExist:
                return JsonResponse({'error': 'This email is not registered.'}, status=404)

            if user.password != password:
                return JsonResponse({'error': 'Incorrect password.'}, status=401)

            if not user.is_verified:
                return JsonResponse({'error': 'Email not verified.'}, status=403)

            return JsonResponse({
                'id': user.id,
                'username': user.username,
                'email': user.email,
            })

        except Exception as e:
            return JsonResponse({'error': str(e)}, status=400)

    return JsonResponse({'error': 'Invalid request method'}, status=405)

@csrf_exempt
def register_user_mobile(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)

            username = data.get('username')
            email = data.get('email')
            password = data.get('password')
            confirmation_code = data.get('confirmation_code')

            if not all([username, email, password, confirmation_code]):
                return JsonResponse({'error': 'Missing fields'}, status=400)

            if UserMobile.objects.filter(email=email).exists():
                return JsonResponse({'error': 'This email is already registered.'}, status=400)
            
            if UserMobile.objects.filter(username=username).exists():
                return JsonResponse({'error': 'This username is already taken.'}, status=400)

            user = UserMobile.objects.create(
                username=username,
                email=email,
                password=password,  
                confirmation_code=confirmation_code,
                is_verified=False,
            )

            try:
                connection = get_connection()

                html_message = render_to_string('emails/email_verification.html', {
                    'username': username,
                    'verification_code': confirmation_code,
                })

                email_obj = EmailMessage(
                    subject='Your Confirmation Code',
                    body=html_message,
                    from_email='questme7@gmail.com',
                    to=[email],
                    connection=connection,
                )
                email_obj.content_subtype = 'html'
                email_obj.send()
            except Exception as e:
                print("Email send failed:", str(e))

            return JsonResponse({
                'message': 'User created and email sent',
                'user': {
                    'id': user.id,
                    'username': user.username,
                    'email': user.email
                }
            })

        except Exception as e:
            return JsonResponse({'error': str(e)}, status=400)

    return JsonResponse({'error': 'Invalid request method'}, status=405)



@csrf_exempt
def verify_code(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            email = data.get('email', '').strip().lower()
            code = data.get('code', '').strip()

            user = UserMobile.objects.filter(email=email).first()

            if not user:
                return JsonResponse({'error': 'User not found'}, status=404)

            if user.confirmation_code == code:
                user.is_verified = True
                user.save()
                return JsonResponse({'message': 'User verified'})
            else:
                return JsonResponse({'error': 'Invalid confirmation code'}, status=400)

        except Exception as e:
            return JsonResponse({'error': str(e)}, status=400)

    return JsonResponse({'error': 'Invalid request method'}, status=405)



# Inregistrare si autentificare aplicatie web

def register(request):
    if request.method == 'POST':
        form = UserRegisterForm(request.POST)
        if form.is_valid():
          
            email = form.cleaned_data['email']
            username = form.cleaned_data['username']
            password = form.cleaned_data['password1']

            code = f"{random.randint(100000, 999999)}"

            request.session['temp_user'] = {
                'email': email,
                'username': username,
                'password': password,
                'code': code,
                'code_created_at': timezone.now().isoformat(),
            }
 
            send_verification_email(username=username, email=email, code=code)

            return render(request, 'register.html', {
                'form': form,
                'show_popup': True  
            })
    else:
        form = UserRegisterForm()

    return render(request, 'register.html', {
        'form': form,
    })

def user_login(request):
    error_message = None

    if request.user.is_authenticated:
        return redirect('home')

    if request.method == 'POST':
        form = UserLoginForm(request.POST)
        if form.is_valid():
            email = form.cleaned_data['email']
            password = form.cleaned_data['password']
            user = authenticate(request, username=email, password=password)
            if user is not None:
                login(request, user)
                return redirect('home')
            else:
                error_message = "The email or password you entered is incorrect."
    else:
        form = UserLoginForm()

    return render(request, 'login.html', {'form': form, 'error_message': error_message, 'hide_menu': True})


def user_logout(request):
    logout(request)
    return redirect('login')


def profile(request):
    return render(request, 'profile.html', {'user': request.user})


# Salvare chestionar
def save_quiz(request):
    if request.method == 'POST': 
        title = request.POST.get("title")
        is_anonymous = request.POST.get("anonymous") == "on"
        
        if not title:
            return render(request, "quiz/create_quiz.html")  
        
        try:
            user_logged_in = request.user.is_authenticated  
            author = None if is_anonymous and user_logged_in else (request.user if user_logged_in else None)
            author_id_value = request.user.id if user_logged_in else None  

            quiz = Quiz.objects.create(
                author=author,
                author_id_value=author_id_value,
                title_quiz=title
            )

            if request.user.is_authenticated:
                try:
                    print(" Attempting to send quiz creation email...")
                    subject = 'Your Quiz Was Created Successfully!'
                    html_message = render_to_string('emails/quiz_confirmation.html', {
                    'username': request.user.username,
                    'quiz_title': quiz.title_quiz,
                    })
        
                    email = EmailMessage(
                    subject,
                    html_message,
                    'questme7@gmail.com',
                    [request.user.email],
                    )
                    email.content_subtype = 'html'  
                    email.send()
                    print(" Quiz creation email sent to:", request.user.email)

                except Exception as email_error:
                    print(f"Email send failed: {str(email_error)}")


            questions_data = {}  
            for key in request.POST:
                if key.startswith('questions[') and '][text]' in key:
                    index = key.split('[')[1].split(']')[0]
                    if index not in questions_data:
                        questions_data[index] = {
                            'text': request.POST.get(f'questions[{index}][text]', '').strip(),
                            'answers': request.POST.getlist(f'questions[{index}][answers][]'),
                            'correct_answers': request.POST.getlist(f'questions[{index}][correct_answers][]')
                        }

            for index in sorted(questions_data.keys(), key=lambda x: int(x)):
                data = questions_data[index]
                question_text = data['text']
                if not question_text:
                    continue 

                question = Question.objects.create(quiz=quiz, title_question=question_text)
                
                answer_texts = data['answers']
                correct_answers = data['correct_answers']
                
                for j, answer_text in enumerate(answer_texts):
                    answer_text = answer_text.strip()
                    if not answer_text:
                        continue  
                    
                    is_right = str(j) in correct_answers
                    Answer.objects.create(question=question, answer_text=answer_text, is_right=is_right)
            
            return redirect(f"{reverse('home')}?saved=1")
        
        except Exception as e:
            print(request, f"An error occurred: {str(e)}")
            return render(request, "quiz/create_quiz.html")
    
    return render(request, "quiz/create_quiz.html")

# Salvare raspunsuri
@csrf_exempt
def save_answers(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            answers = data.get('answers', [])
            user_email = data.get('user_email')

            if not answers or not user_email:
                return JsonResponse({'error': 'No answers or email provided'}, status=400)

            for answer in answers:
                UserAnswer.objects.update_or_create(
                    username=answer['username'],
                    quiz_id=answer['quiz_id'],
                    question_id=answer['question_id'],
                    answer_text=answer['answer_text'],
                    defaults={'is_right': answer['is_right']}
                )

            first_answer = answers[0]
            quiz_id = first_answer['quiz_id']
            username = first_answer['username']

            quiz = Quiz.objects.filter(id=quiz_id).first()

            if quiz:
                try:
                    connection = get_connection()

                    html_message_user = render_to_string('emails/quiz_completed.html', {
                        'username': username,
                        'quiz_title': quiz.title_quiz,
                    })

                    user_email_obj = EmailMessage(
                        subject='You Completed a Quiz!',
                        body=html_message_user,
                        from_email='questme7@gmail.com',
                        to=[user_email],
                        connection=connection,
                    )
                    user_email_obj.content_subtype = 'html'
                    user_email_obj.send()

                    if quiz.author and quiz.author.email:
                        html_message_creator = render_to_string('emails/quiz_completed_notify_creator.html', {
                            'quiz_title': quiz.title_quiz,
                            'completed_by': username,
                        })

                        creator_email_obj = EmailMessage(
                            subject='Your Quiz Was Just Completed!',
                            body=html_message_creator,
                            from_email='questme7@gmail.com',
                            to=[quiz.author.email],
                            connection=connection,
                        )
                        creator_email_obj.content_subtype = 'html'
                        creator_email_obj.send()
                        print("Creator notification email sent.")

                except Exception as email_error:
                    print(f"Email sending failed: {str(email_error)}")

            return JsonResponse({'message': 'Answers saved and email attempted'})

        except json.JSONDecodeError as e:
            return JsonResponse({'error': f'JSON decode error: {str(e)}'}, status=400)
        except Exception as e:
            return JsonResponse({'error': str(e)}, status=400)

    return JsonResponse({'error': 'Invalid method'}, status=405)



# Editare chestionar
def edit_quiz(request, quiz_id):
    quiz = get_object_or_404(Quiz, id=quiz_id)

    if request.method == 'POST':
        quiz.title_quiz = request.POST.get("title", "").strip()
        quiz.save()

        submitted_question_ids = []
        questions_data = {}

        for key in request.POST:
            if key.startswith('questions[') and '][text]' in key:
                index = key.split('[')[1].split(']')[0]
                qid = request.POST.get(f'questions[{index}][id]', None)
                questions_data[index] = {
                    'id': qid,
                    'text': request.POST.get(f'questions[{index}][text]', '').strip(),
                    'answers': request.POST.getlist(f'questions[{index}][answers][]'),
                    'correct_answers': request.POST.getlist(f'questions[{index}][correct_answers][]')
                }
                if qid:
                    submitted_question_ids.append(int(qid))

        existing_questions = quiz.questions.all()

        for q in existing_questions:
            if q.id not in submitted_question_ids:
                q.delete()

        for index in sorted(questions_data.keys(), key=int):
            data = questions_data[index]
            question_id = data['id']
            question_text = data['text']
            answer_texts = data['answers']
            correct_answers = data['correct_answers']

            if not question_text:
                continue

            if question_id:
                question = Question.objects.get(id=question_id, quiz=quiz)
                question.title_question = question_text
                question.answers.all().delete()
            else:
                question = Question.objects.create(quiz=quiz, title_question=question_text)

            for j, answer_text in enumerate(answer_texts):
                if answer_text.strip():
                    is_right = str(j) in correct_answers
                    Answer.objects.create(question=question, answer_text=answer_text.strip(), is_right=is_right)

        return redirect('quiz_detail', quiz_id=quiz.id)

    quiz_data = []
    for q in quiz.questions.all():
        quiz_data.append({
            'id': q.id,
            'text': q.title_question,
            'answers': [{'text': a.answer_text, 'is_right': a.is_right} for a in q.answers.all()]
        })

    return render(request, 'quiz/edit_quiz.html', {'quiz': quiz, 'quiz_data': quiz_data})

# Stergere quiz
def delete_quiz(request, quiz_id):
    quiz = get_object_or_404(Quiz, pk=quiz_id)

    if request.method == "POST":
        quiz.delete()
        messages.success(request, "Quiz has been deleted.")
        return redirect('all_quiz')
    
    return redirect('all_quiz')

def home(request):
    return render(request, 'home.html')

def create_quiz(request):
    return render(request, 'quiz/create_quiz.html')

def all_quiz(request):
    if request.user.is_authenticated:
        quizzes = Quiz.objects.filter(author_id_value=request.user.id).order_by('-id')
    else:
        quizzes = Quiz.objects.none()

    return render(request, 'quiz/all_quiz.html', {'quizzes': quizzes})


def filled_by(request, quiz_id):
    quiz = get_object_or_404(Quiz, id=quiz_id)
    user_answers = UserAnswer.objects.filter(quiz_id=quiz_id)

    user_question_answers = defaultdict(lambda: defaultdict(set))

    for ua in user_answers:
        user_question_answers[ua.username][ua.question_id].add(ua.answer_text)

    correct_answers = {
        question.id: set(
            answer.answer_text
            for answer in question.answers.filter(is_right=True)
        )
        for question in quiz.questions.all()
    }

    user_scores = {}

    for username, questions in user_question_answers.items():
        score = 0
        for q_id, given_answers in questions.items():
            expected_answers = correct_answers.get(q_id, set())
            if given_answers == expected_answers:
                score += 1
        user_scores[username] = score  

    sorted_users = sorted(user_scores.items(), key=lambda x: x[1], reverse=True)

    context = {
        'quiz': quiz,
        'users_with_scores': sorted_users,
    }
    return render(request, 'quiz/filled_by.html', context)


def quiz_detail(request, quiz_id):
    quiz = get_object_or_404(Quiz.objects.prefetch_related('questions__answers'), id=quiz_id)
    return render(request, 'quiz/quiz_detail.html', {'quiz': quiz})


class QuizListView(generics.ListAPIView):
    queryset = Quiz.objects.all()
    serializer_class = QuizListSerializer


class QuizDetailView(generics.RetrieveAPIView):
    queryset = Quiz.objects.all()
    serializer_class = QuizDetailSerializer   


def user_answers(request, quiz_id, username):
    quiz = get_object_or_404(Quiz, id=quiz_id)
    answers = UserAnswer.objects.filter(username=username, quiz_id=quiz_id)
    
    
    answer_details = []
    for answer in answers:
        question = Question.objects.get(
            quiz_id=answer.quiz_id,
            id=answer.question_id
        )
        answer_details.append((answer, question))
    
    context = {
        'quiz': quiz,
        'username': username,
        'answer_details': answer_details,
    }
    return render(request, 'quiz/user_answers.html', context)


# Export chestionar PDF
def quiz_pdf(request, quiz_id):
    quiz = Quiz.objects.prefetch_related('questions__answers').get(id=quiz_id)
    template_path = 'quiz/quiz_pdf_template.html'
    context = {'quiz': quiz}
    
    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = f'attachment; filename="{quiz.title_quiz}.pdf"'

    template = get_template(template_path)
    html = template.render(context)

    pisa_status = pisa.CreatePDF(html, dest=response)
    
    if pisa_status.err:
        return HttpResponse('PDF generation failed', status=500)
    return response

# Generare cu AI chestionar cu prompt
@csrf_exempt
@require_POST
def generate_quiz(request):
    data = json.loads(request.body)
    prompt = data.get('prompt')

    prompt_text = (
        f"Create a quiz about {prompt}. "
        "Always return JSON with this exact format:\n\n"
        "{\n"
        "  \"title\": \"Quiz Title without the word \"Quiz\",\n"
        "  \"questions\": [\n"
        "    {\n"
        "      \"question\": \"Your question text here\",\n"
        "      \"options\": [\"Answer 1\", \"Answer 2\", \"Answer 3\", and so on],\n"
        "      \"answer\": \"The correct answer text from the answers above.\"\n"
        "    }\n"
        "  ]\n"
        "}\n\n"
        "Only respond with valid JSON and use the same key names exactly as shown above. Do not add any explanations."
    )

    response = requests.post(
        "http://localhost:11434/api/generate",
        json={
            "model": "mistral",
            "prompt": prompt_text,
            "stream": False,
            "temperature": 0.2
        }
    )

    response_data = response.json()
    output = response_data.get("response", "")
    try:
        ai_data = json.loads(output)

        quiz_data = {
            'title': ai_data['title'],
            'questions': []
        }

        for q in ai_data['questions']:
            quiz_data['questions'].append({
                'question': q['question'],
                'options': q['options'],
                'answer': q['answer']
            })

    except (json.JSONDecodeError, KeyError) as e:
        return JsonResponse({'error': f'Could not parse JSON or missing keys: {str(e)}', 'raw_output': output}, status=500)

    return JsonResponse(quiz_data)

def generate_ai(request):
    show_quiz_form = False

    if request.method == "POST":
        show_quiz_form = True

    return render(request, 'generate/generate_ai.html', {'show_quiz_form': show_quiz_form})



# Generare cu AI chestionar cu PDF
@csrf_exempt
def generate_quiz_from_pdf(request):
    if request.method == 'POST' and request.FILES.get('pdf'):
        pdf_file = request.FILES['pdf']

        try:
            doc = fitz.open(stream=pdf_file.read(), filetype="pdf")
            full_text = ""
            for page in doc:
                full_text += page.get_text()
            doc.close()

        except Exception as e:
            return JsonResponse({'error': f'Failed to extract text from PDF: {str(e)}'}, status=500)

        prompt_text = (
            f"Based on the following lecture, create a quiz. Only use facts found in the lecture.\n\n"
            f"Lecture content:\n{full_text[:3000]}\n\n" 
            "Always return JSON with this exact format:\n"
            "{\n"
            "  \"title\": \"Quiz Title without the word \"Quiz\" \",\n"
            "  \"questions\": [\n"
            "    {\n"
            "      \"question\": \"Your question text here\",\n"
            "      \"options\": [\"Answer 1\", \"Answer 2\", \"Answer 3\", and so on],\n"
            "      \"answer\": \"The correct answers text from the answers above.\"\n"
            "    }\n"
            "  ]\n"
            "}\n"
            "Only respond with valid JSON. Do not add explanations."
        )

        response = requests.post(
            "http://localhost:11434/api/generate",
            json={
                "model": "mistral",
                "prompt": prompt_text,
                "stream": False,
                "temperature": 0.2
            }
        )

        response_data = response.json()
        output = response_data.get("response", "")

        try:
            ai_data = json.loads(output)
            quiz_data = {
                'title': ai_data['title'],
                'questions': ai_data['questions'],
                'raw_output': output 
            }
        except Exception as e:
            return JsonResponse({'error': f'Could not parse quiz: {str(e)}', 'raw_output': output}, status=500)

        return JsonResponse(quiz_data)

    return JsonResponse({'error': 'Invalid request'}, status=400)


def generate_pdf(request):
    show_quiz_form = False

    if request.method == "POST":
        show_quiz_form = True

    return render(request, 'generate/generate_pdf.html', {'show_quiz_form': show_quiz_form})



def quiz_answer_distribution(request, quiz_id):
    quiz = Quiz.objects.get(pk=quiz_id)
    data = []

    for question in quiz.questions.all():
        question_data = {
            "question": question.title_question,
            "answers": []
        }

        answers = Answer.objects.filter(question=question)
        answer_counts = defaultdict(int)

        user_answers = UserAnswer.objects.filter(quiz_id=quiz.id, question_id=question.id)

        for ua in user_answers:
            answer_counts[ua.answer_text] += 1

        for answer in answers:
            question_data["answers"].append({
                "text": answer.answer_text,
                "count": answer_counts.get(answer.answer_text, 0)
            })

        data.append(question_data)

    return JsonResponse(data, safe=False)


# Email de verificare
def send_verification_email(username, email, code):
    print("Rendering verification email template...")

    html_content = render_to_string('emails/email_verification.html', {
        'username': username,
        'verification_code': code,
    })
    connection = get_connection() 

    email_msg = EmailMessage(
        subject='Verify Your Email',
        body=html_content,
        from_email='questme7@gmail.com',  
        to=[email],
        connection=connection
    )
    email_msg.content_subtype = 'html'
    try:
        email_msg.send()
    except Exception as e:
        print(f"Error sending email to {email}: {e}")



# Verficare email
User = get_user_model()
@csrf_exempt
def verify_email(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        input_code = data.get('code')
        session_data = request.session.get('temp_user')

        if not session_data:
            return JsonResponse({'success': False, 'message': 'No registration data found'})

        if input_code != session_data['code']:
            return JsonResponse({'success': False, 'message': 'Invalid verification code'})

        created_at = parse_datetime(session_data['code_created_at'])
        if timezone.now() > created_at + timezone.timedelta(minutes=10):
            return JsonResponse({'success': False, 'message': 'Verification code expired'})

        user = User(
            username=session_data['username'],
            email=session_data['email'],
            is_verified=True
        )
        user.set_password(session_data['password'])
        user.save()

        login(request, user)
        del request.session['temp_user'] 

        return JsonResponse({'success': True, 'message': 'Email verified. User registered.'})

    return JsonResponse({'success': False, 'message': 'Invalid request'})


@api_view(['GET'])
def completed_quizzes(request):
    username = request.GET.get('username')
    if not username:
        return Response({'error': 'Username is required'}, status=400)

    quiz_ids = (
        UserAnswer.objects
        .filter(username=username)
        .values_list('quiz_id', flat=True)
        .distinct()
    )

    quiz_data = []
    for quiz_id in quiz_ids:
        answers = UserAnswer.objects.filter(username=username, quiz_id=quiz_id)
        correct = answers.filter(is_right=True).count()
        total = answers.count()
        quiz = Quiz.objects.filter(id=quiz_id).first()

        if quiz:
            quiz_data.append({
                'id': quiz.id,
                'title_quiz': quiz.title_quiz,
                'correct_answers': correct,
                'total_answers': total,
            })

    return Response(quiz_data)

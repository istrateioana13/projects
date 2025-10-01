from django.urls import path
from . import views
from .views import (
    register, user_login, user_logout, home,
    register_user_mobile, verify_code, login_mobile,
    create_quiz, save_quiz, all_quiz, delete_quiz, edit_quiz,
    quiz_detail, QuizListView, QuizDetailView, filled_by, user_answers, save_answers,
    quiz_pdf, generate_pdf, generate_quiz, generate_ai, generate_quiz_from_pdf,
    quiz_answer_distribution, verify_email, completed_quizzes,
)

urlpatterns = [
    # --- Autentificare web ---
    path('', user_login, name='login'),
    path('register/', register, name='register'),
    path('logout/', user_logout, name='logout'),
    path('verify-email/', verify_email, name='verify_email'),

    # --- Autentificare pentru aplicația mobilă ---
    path('register_mobile/', register_user_mobile, name='register_user_mobile'),
    path('verify_code/', verify_code, name='verify_code'),
    path('login_mobile/', login_mobile, name='login_mobile'),

    # --- Pagină principală ---
    path('home/', home, name='home'),

    # Creare și administrare quiz-uri 
    path('create_quiz/', create_quiz, name='create_quiz'),
    path('save_quiz/', save_quiz, name='save_quiz'),
    path('all_quizzes/', all_quiz, name='all_quiz'),
    path('quiz/<int:quiz_id>/', quiz_detail, name='quiz_detail'),
    path('quiz/<int:quiz_id>/delete/', delete_quiz, name='delete_quiz'),
    path('edit_quiz/<int:quiz_id>/', edit_quiz, name='edit_quiz'),

    # Completare quiz și rezultate 
    path('quizzes/', QuizListView.as_view(), name='quiz_list'),
    path('complete/quiz/<int:pk>/', QuizDetailView.as_view(), name='quiz-detail'),
    path('save_answers/', save_answers, name='save_answers'),
    path('filled_by/<int:quiz_id>', filled_by, name='filled_by'),
    path('user_answers/<int:quiz_id>/<str:username>', user_answers, name='user_answers'),
    path('completed-quizzes/', completed_quizzes, name='completed_quizzes'),


    # Generare quiz
    path('generate_quiz/', generate_quiz, name='generate_quiz'),
    path('generate_ai/', generate_ai, name='generate_ai'),
    path('generate_pdf/', generate_pdf, name='generate_pdf'),
    path('generate_quiz_from_pdf/', generate_quiz_from_pdf, name='generate_quiz_from_pdf'),

    # Export și statistici 
    path('quiz/<int:quiz_id>/export-pdf/', quiz_pdf, name='quiz_pdf'),
    path('quiz/<int:quiz_id>/answer-distribution/', quiz_answer_distribution, name='quiz_answer_distribution'),
]

from django.contrib import admin
from .models import User, Profile, Quiz, Question, Answer, UserAnswer, UserMobile


@admin.register(User)
class UserAdmin(admin.ModelAdmin):
    list_display = ['id', 'username', 'email', 'is_verified']


@admin.register(Profile)
class ProfileAdmin(admin.ModelAdmin):
    list_display = ['user', 'first_name']


@admin.register(Quiz)
class QuizAdmin(admin.ModelAdmin):
    list_display = ['id','title_quiz', 'author', 'author_id_value']


@admin.register(Question)
class QuestionAdmin(admin.ModelAdmin):
    list_display = ['title_question', 'quiz']


@admin.register(Answer)
class AnswerAdmin(admin.ModelAdmin):
    list_display = ['answer_text', 'question', 'is_right']

@admin.register(UserAnswer)
class UserAnswerAdmin(admin.ModelAdmin):
    list_display = ['username','quiz_id', 'answer_text']


@admin.register(UserMobile)
class UserAnswerAdmin(admin.ModelAdmin):
    list_display = ['username','email', 'is_verified']

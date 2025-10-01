from rest_framework import serializers
from .models import Quiz, Question, Answer

class QuizListSerializer(serializers.ModelSerializer):
    author = serializers.StringRelatedField()  
    created_at = serializers.DateTimeField(format="%Y-%m-%d %H:%M")

    class Meta:
        model = Quiz
        fields = ['id', 'title_quiz', 'author', 'created_at']

class AnswerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Answer
        fields = ['id', 'answer_text', 'is_right']

class QuestionSerializer(serializers.ModelSerializer):
    answers = AnswerSerializer(many=True, read_only=True)

    class Meta:
        model = Question
        fields = ['id', 'title_question', 'answers']

class QuizDetailSerializer(serializers.ModelSerializer):
    questions = QuestionSerializer(many=True, read_only=True)
    title = serializers.CharField(source='title_quiz')

    class Meta:
        model = Quiz
        fields = ['id', 'title', 'questions']
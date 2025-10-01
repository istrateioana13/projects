from django.db import models
from django.contrib.auth.models import AbstractUser
from django.db.models.signals import post_save
from django.utils.translation import gettext_lazy as _
from django.utils import timezone

# User Model
class User(AbstractUser):
    username = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    is_verified = models.BooleanField(default=False)

    verification_code = models.CharField(max_length=6, blank=True, null=True)
    code_created_at = models.DateTimeField(blank=True, null=True)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['username',]

    def __str__(self):
        return self.username

    def is_code_expired(self):
        if not self.code_created_at:
            return True
        return timezone.now() > self.code_created_at + timezone.timedelta(minutes=10)
    
#Profile model
class Profile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    first_name = models.CharField(max_length=100)

    def __str__(self):
        return self.first_name
        

def create_user_profile(sender, instance, created, **kwargs):
    if created:
        Profile.objects.create(user=instance)

def save_user_profile(sender, instance, **kwargs):
    instance.profile.save()        

post_save.connect(create_user_profile, sender=User)  
post_save.connect(save_user_profile, sender=User)  

# Quiz Model
class Quiz(models.Model):
    author = models.ForeignKey(User, on_delete=models.CASCADE, related_name="quizzes", null=True)
    author_id_value = models.IntegerField(null=True, blank=True)
    title_quiz = models.CharField(_("Titlul chestionarului"), max_length=100, default=_("Chestionar Nou"))
    created_at = models.DateTimeField(auto_now_add=True)


    @property
    def question_count(self):
        return self.questions.count()

    class Meta:
        verbose_name = _("Chestionar")
        verbose_name_plural = _("Chestionare")
        ordering = ["id"]

    def __str__(self):
        return self.title_quiz


# Question Model
class Question(models.Model):
    quiz = models.ForeignKey(Quiz, related_name="questions", on_delete=models.CASCADE)
    title_question = models.CharField(_("Întrebare"), max_length=255, db_index=True)

    class Meta:
        verbose_name = _("Întrebare")
        verbose_name_plural = _("Întrebări")
        ordering = ["id"]

    def __str__(self):
        return self.title_question


# Answer Model
class Answer(models.Model):
    question = models.ForeignKey(Question, related_name="answers", on_delete=models.CASCADE)
    answer_text = models.CharField(_("Răspuns"), max_length=255)
    is_right = models.BooleanField(default=False)

    class Meta:
        verbose_name = _("Răspuns")
        verbose_name_plural = _("Răspunsuri")
        ordering = ["id"]

    def __str__(self):
        return f"{self.answer_text} ({'Corect' if self.is_right else 'Greșit'})"
    

# UserAnswer Model    
class UserAnswer(models.Model):
    username = models.CharField(max_length=255)
    quiz_id = models.IntegerField()
    question_id = models.IntegerField()
    is_right = models.IntegerField()
    answer_text = models.CharField(max_length=1000, blank=True, null=True, default='')

    def __str__(self):
        return f'{self.username} - Quiz {self.quiz_id} - Q{self.question_id}'  
    
# UserMobile Model    
class UserMobile(models.Model):
    username = models.CharField(max_length=150, unique=True)
    email = models.EmailField(unique=True)
    password = models.CharField(max_length=128)
    confirmation_code = models.CharField(max_length=10)
    is_verified = models.BooleanField(default=False)

    def __str__(self):
        return self.username
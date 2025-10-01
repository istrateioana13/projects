from django import forms
from django.contrib.auth.forms import UserCreationForm, AuthenticationForm
from .models import User 

class UserRegisterForm(UserCreationForm):
    email = forms.EmailField()

    class Meta:
        model = User
        fields = ['username', 'email', 'password1', 'password2'] 

class UserLoginForm(forms.Form):  
    email = forms.EmailField(label="Email")
    password = forms.CharField(widget=forms.PasswordInput)

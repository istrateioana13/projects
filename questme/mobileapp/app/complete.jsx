import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView, ActivityIndicator } from 'react-native';
import { useRouter, useLocalSearchParams } from 'expo-router';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useSQLiteContext } from 'expo-sqlite';

const Complete = () => {
  const router = useRouter();
  const [quizData, setQuizData] = useState(null);
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const [loading, setLoading] = useState(true); 
  const { quizId } = useLocalSearchParams();

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const response = await axios.get(`http://127.0.0.1:8000/complete/quiz/${quizId}/`);
        const data = await response.data;
        setQuizData(data);  
        setLoading(false);   
      } catch (error) {
        console.error('Failed to fetch quiz:', error);
        setLoading(false); 
      }
    };
    fetchQuiz();
  }, []);

  const handleSelect = (questionId, answer) => {
    setSelectedAnswers((prev) => {
      const current = prev[questionId] || [];
  
      const alreadySelected = current.find(
        (a) => a.answer_text === answer.answer_text
      );
  
      let updated;
  
      if (alreadySelected) {
        
        updated = current.filter((a) => a.answer_text !== answer.answer_text);
      } else {
       
        updated = [...current, { answer_text: answer.answer_text, is_right: answer.is_right }];
      }
  
      return {
        ...prev,
        [questionId]: updated,
      };
    });
  };

  const handleSubmit = async () => {
    try {
      const storedUser = await AsyncStorage.getItem('loggedInUser');
  
      if (!storedUser) {
        alert('User not logged in!');
        return;
      }
  
      const user = JSON.parse(storedUser);
  
      const results = Object.entries(selectedAnswers).flatMap(([questionId, answers]) =>
        answers.map((answer) => ({
          username: user.username,
          quiz_id: parseInt(quizId),
          question_id: parseInt(questionId),
          is_right: answer.is_right ? 1 : 0,
          answer_text: answer.answer_text,
        }))
      );
  
      const payload = {
        answers: results,
        user_email: user.email,
      };
  
      await sendAnswersToDjango(payload);
      router.push('/home');
    } catch (error) {
      console.error('Error in handleSubmit:', error);
      alert('Something went wrong. Please try again.');
    }
  };
  
  const sendAnswersToDjango = async (payload) => {
    try {
      const response = await fetch('http://127.0.0.1:8000/save_answers/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      const data = await response.json();
    } catch (err) {
      console.error('Error sending to Django:', err);
    }
  };

  if (loading) {
    return <ActivityIndicator size="large" style={{ flex: 1, justifyContent: 'center' }} />;
  }

  if (!quizData) {
    return <Text style={styles.errorText}>Failed to load quiz data. Please try again.</Text>;
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.quizTitle}>{quizData.title}</Text>

      {quizData.questions.map((question) => (
        <View key={question.id} style={styles.questionCard}>
          <Text style={styles.questionText}>{question.title_question}</Text>

          {question.answers.map((answer) => {
           const isSelected = selectedAnswers[question.id]?.some(
            (a) => a.answer_text === answer.answer_text
          );          
            return (
              <TouchableOpacity
                key={answer.id}
                onPress={() => handleSelect(question.id, answer)}
                style={[
                  styles.answerButton,
                  isSelected && styles.selectedAnswer,
                ]}
              >
                <Text style={[styles.answerText, isSelected && styles.selectedAnswerText]}>
                  {answer.answer_text}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>
      ))}

      <TouchableOpacity style={styles.submitButton} onPress={handleSubmit}>
        <Text style={styles.submitText}>Send</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

export default Complete;

const styles = StyleSheet.create({
  container: {
    padding: 16,
    paddingBottom: 60,
  },
  quizTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 65,
    marginBottom: 24,
  },
  questionCard: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 12,
    marginBottom: 24,
    borderColor: '#ddd',
    borderWidth: 1,
  },
  questionText: {
    fontWeight: 'bold',
    fontSize: 18,
    marginBottom: 12,
  },
  answerButton: {
    padding: 12,
    borderRadius: 8,
    backgroundColor: '#f1f1f1',
    marginBottom: 8,
  },
  selectedAnswer: {
    backgroundColor: '#d1fae5',
    borderColor: '#10b981',
    borderWidth: 2,
  },
  answerText: {
    fontSize: 16,
  },
  selectedAnswerText: {
    fontWeight: 'bold',
    color: '#065f46',
  },
  submitButton: {
    backgroundColor: '#B71C1C',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
    marginTop: 24,
  },
  submitText: {
    color: 'white',
    fontSize: 18,
    fontWeight: '600',
  },
  errorText: {
    textAlign: 'center',
    fontSize: 18,
    color: 'red',
    marginTop: 50,
  },
});

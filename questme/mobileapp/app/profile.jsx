import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { router } from 'expo-router';

const Profile = () => {
  const [username, setUsername] = useState('');
  const [completedQuizzes, setCompletedQuizzes] = useState([]);

  useEffect(() => {
    const loadUsername = async () => {
      try {
        const storedUser = await AsyncStorage.getItem('loggedInUser');
        if (storedUser) {
          const user = JSON.parse(storedUser);
          setUsername(user.username);
        }
      } catch (error) {
        console.error('Error loading user:', error);
      }
    };

    loadUsername();
  }, []);

  useEffect(() => {
    if (username) {
      fetchCompletedQuizzes();
    }
  }, [username]);

  const fetchCompletedQuizzes = async () => {
    try {
      const res = await fetch(`http://127.0.0.1:8000/completed-quizzes/?username=${username}`);
      const data = await res.json();
      setCompletedQuizzes(data); 
    } catch (error) {
      console.error('Error fetching completed quizzes:', error);
    }
  };
  

  const renderQuizItem = ({ item }) => (
    <View style={styles.quizItem}>
      <Text style={styles.quizTitle}>{item.title_quiz}</Text>
      <Text style={styles.quizDetails}>
        Correct Answers: {item.correct_answers} / {item.total_answers}
      </Text>
    </View>
  );
  

  return (
    <View style={styles.container}>
      <Text style={styles.greeting}>Hello, {username}</Text>
      <Text style={styles.heading}>Completed Quizzes</Text>

      {completedQuizzes.length === 0 ? (
        <Text style={styles.noQuizzesText}>No quizzes completed yet.</Text>
      ) : (
        <FlatList
          data={completedQuizzes}
          keyExtractor={(item) => item.id.toString()}
          renderItem={renderQuizItem}
        />
      )}

      <TouchableOpacity
        style={styles.button}
        onPress={async () => {
          await AsyncStorage.removeItem('loggedInUser');
          router.replace('/welcome');
        }}
      >
        <Text style={styles.buttonText}>Log out</Text>
      </TouchableOpacity>
    </View>
  );
};

export default Profile;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    backgroundColor: '#fff',
  },
  greeting: {
    fontSize: 24,
    fontWeight: '600',
    marginBottom: 20,
    textAlign: 'center',
    marginTop: 60,
  },
  heading: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
    textAlign: 'center',
  },
  noQuizzesText: {
    textAlign: 'center',
    color: '#555',
  },
  quizItem: {
    padding: 15,
    marginBottom: 10,
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
  },
  quizTitle: {
    fontSize: 18,
    fontWeight: '600',
  },
  quizDetails: {
    fontSize: 14,
    color: '#555',
  },
  button: {
    backgroundColor: '#B71C1C',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 12,
    alignSelf: 'center',
    marginBottom: 20,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
  },
});

import { StatusBar } from "expo-status-bar";
import React, { useState,useEffect } from "react";
import { 
  View, 
  Text, 
  TextInput, 
  FlatList, 
  TouchableOpacity, 
  StyleSheet, 
  Image,
} from "react-native";
import logoImg from "@/assets/images/logo.png"
import Animated, {FadeInDown, FadeInUp } from 'react-native-reanimated';
import axios from 'axios';
import { useNavigation } from "expo-router";
import { useSQLiteContext } from "expo-sqlite";
import AsyncStorage from '@react-native-async-storage/async-storage';

const QuizCard = ({ id, title, author }) => {
  
  const navigation = useNavigation(); 

  return (
    <Animated.View entering={FadeInDown.delay(200).duration(1000).springify()} style={styles.card}>
      <Text style={styles.quizTitle}>{title}</Text>
      <Text style={styles.quizAuthor}>by {author}</Text>
      <TouchableOpacity style={styles.button} onPress={() => navigation.push('complete', { quizId: id })}>
        <Text style={styles.buttonText}>View</Text>
      </TouchableOpacity>
    </Animated.View>
  );
};

const Home = () => {
 
  const [search, setSearch] = useState('');
  const navigationHome = useNavigation();
  const navigationProfile = useNavigation();
  const [completedQuizIds, setCompletedQuizIds] = useState([]);
  const [quizzes, setQuizzes] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const userData = await AsyncStorage.getItem('loggedInUser');
        if (!userData) return;
  
        const user = JSON.parse(userData);
  
        const [quizRes, completedRes] = await Promise.all([
          axios.get('http://127.0.0.1:8000/quizzes/'),
          axios.get(`http://127.0.0.1:8000/completed-quizzes/?username=${user.username}`)
        ]);
  
        const sortedQuizzes = quizRes.data.sort((a, b) => b.id - a.id);
        setQuizzes(sortedQuizzes);
        setCompletedQuizIds(completedRes.data.map(q => q.id));

      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
  
    fetchData();
  }, []);
  
  

  const filteredQuizzes = quizzes
  .filter((quiz) =>
    (quiz.title_quiz || "").toLowerCase().includes(search.toLowerCase())
  )
  .filter((quiz) => !completedQuizIds.includes(quiz.id));  






  return (
      <View style={styles.container}>
        <StatusBar style="dark" />
        
        {/* Header */}
        <View style={styles.header}>
          <View style={styles.logoContainer}>
            <TouchableOpacity >
            <Image 
              source={logoImg}
              style={styles.logoImage}
              resizeMode="contain"
            />
            </TouchableOpacity>

            <TouchableOpacity onPress={()=>navigationHome.push('home')}>
            <Text style={styles.logoText}>QuestMe</Text>
            </TouchableOpacity>
            
          </View>
          <Text style={styles.profile} onPress={()=>navigationProfile.push('profile')}>Profile</Text>
        </View>

        {/* Search Bar */}
        <Animated.View  entering={FadeInUp.delay(400).duration(1000).springify() }>
        <TextInput
          style={styles.searchBar}
          placeholder="Search quizzes..."
          placeholderTextColor="#888"
          value={search}
          onChangeText={setSearch}
        />
        </Animated.View>

        <FlatList
        data={filteredQuizzes}
        keyExtractor={(item) => item.id.toString()}
        numColumns={2}
        renderItem={({ item }) => (
          <QuizCard id={item.id} title={item.title_quiz} author={item.author} />
        )}
        columnWrapperStyle={styles.row}
        showsVerticalScrollIndicator={false}
      />
      
      </View>
  );
};

const styles = StyleSheet.create({
 
  container: { 
    flex: 1, 
    paddingHorizontal: 16,
    paddingTop: 20,
    backgroundColor: '#fff',

  },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 10,
    marginTop: 30,
  },
  logoContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  logoImage: {
    width: 62,
    height: 62,
    marginRight: 12,
  },
  logoText: {
    fontSize: 24, 
    fontWeight: "700",
    color: 'black',
  },
  profile: { 
    fontSize: 16, 
    color: "#666",
    fontWeight: '500',
  },
  searchBar: {
    height: 48,
    backgroundColor: '#f5f5f5',
    borderRadius: 12,
    paddingHorizontal: 16,
    fontSize: 16,
    marginBottom: 24,
  },
  row: { 
    justifyContent: "space-between",
    marginBottom: 16,
  },
  card: {
    flex: 1,
    backgroundColor: "#ffffff",
    padding: 16,
    marginHorizontal: 8,
    borderRadius: 12,
    minHeight: 160,
    justifyContent: 'space-between',
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 3,
  },
  quizTitle: { 
    fontSize: 16, 
    fontWeight: "600", 
    textAlign: "center",
    color: '#333',
    marginBottom: 8,
  },
  quizAuthor: { 
    fontSize: 14, 
    color: "#888", 
    textAlign: 'center',
  },
  button: {
    backgroundColor: "#B71C1C",
    paddingVertical: 10,
    borderRadius: 8,
    marginTop: 12,
  },
  buttonText: { 
    color: "#fff", 
    fontWeight: "600",
    textAlign: 'center',
  },
});

export default Home;
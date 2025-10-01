import { View, Text, TextInput, TouchableOpacity, StyleSheet, StatusBar } from 'react-native';
import React, { useState } from 'react';
import lightLogo from '@/assets/images/light.png';
import Animated, { FadeInDown, FadeInUp } from 'react-native-reanimated';
import { useNavigation, useRouter } from 'expo-router';

import AsyncStorage from '@react-native-async-storage/async-storage';

const LoginScreen = () => {
  const navigationRegister = useNavigation();
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');

  const handleLogin = async () => {
    try {
      setEmailError('');
      setPasswordError('');

      if (!email.trim() || !password.trim()) {
        alert('Please fill in all fields');
        return;
      }

      const response = await fetch('http://127.0.0.1:8000/login_mobile/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: email.trim().toLowerCase(),
          password: password.trim(),
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        if (data.error?.toLowerCase().includes('email')) {
          setEmailError(data.error);
        } else if (data.error?.toLowerCase().includes('password')) {
          setPasswordError(data.error);
        } else {
          alert(data.error || 'Login failed.');
        }
        return;
      }

      await AsyncStorage.setItem('loggedInUser', JSON.stringify(data));
      router.push('/home');

    } catch (error) {
      console.error('Login error:', error);
      alert('Login failed. Please try again.');
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar styles="white" />

      <View style={styles.logoContainer}>
        <Animated.Image entering={FadeInUp.delay(200).duration(1000).springify()} style={[styles.logo, styles.firstLogo]} source={lightLogo} />
        <Animated.Image entering={FadeInUp.delay(400).duration(1000).springify()} style={[styles.logo, styles.secondLogo]} source={lightLogo} />
      </View>

      <View style={styles.mainContent}>
        <Animated.Text entering={FadeInUp.duration(1000).springify()} style={styles.title}>Login</Animated.Text>

        <Animated.View entering={FadeInDown.duration(1000).springify()} style={styles.inputContainer}>
          <TextInput
            placeholder="Email"
            style={styles.input}
            value={email}
            onChangeText={(text) => {
              setEmail(text);
              setEmailError('');
            }}
            autoCapitalize="none"
            keyboardType="email-address"
          />
          {emailError ? <Text style={styles.errorText}>{emailError}</Text> : null}
        </Animated.View>

        <Animated.View entering={FadeInDown.delay(200).duration(1000).springify()} style={styles.inputContainer}>
          <TextInput
            placeholder="Password"
            style={styles.input}
            value={password}
            onChangeText={(text) => {
              setPassword(text);
              setPasswordError('');
            }}
            secureTextEntry
            autoCapitalize="none"
          />
          {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
        </Animated.View>

        <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
          <Text style={styles.buttonText}>Login</Text>
        </TouchableOpacity>

        <View style={styles.signupContainer}>
          <Text>Don't have an account? </Text>
          <TouchableOpacity onPress={() => navigationRegister.push('signup')}>
            <Text style={styles.signupLink}>Sign up</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: 'white' },
  logoContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    width: '100%',
    position: 'absolute',
  },
  logo: {
    width: 120,
  },
  firstLogo: {
    height: 350,
  },
  secondLogo: {
    height: 270,
  },
  mainContent: {
    padding: 20,
    marginTop: 320,
  },
  title: {
    fontSize: 30,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
  },
  inputContainer: {
    marginBottom: 15,
  },
  input: {
    fontSize: 16,
    padding: 12,
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
  },
  errorText: {
    color: 'red',
    fontSize: 14,
    marginTop: 5,
  },
  loginButton: {
    backgroundColor: '#B71C1C',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
    marginTop: 10,
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
  signupContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 15,
  },
  signupLink: {
    color: '#38bdf8',
  },
});

export default LoginScreen;

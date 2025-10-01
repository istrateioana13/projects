import React, { useState, useEffect } from 'react'; 
import { View, Text, TextInput, TouchableOpacity, StyleSheet, StatusBar } from 'react-native';
import { useRouter, useNavigation } from 'expo-router';
import Animated, { FadeInDown, FadeInUp } from 'react-native-reanimated';
import lightLogo from '@/assets/images/light.png';
import { Modal } from 'react-native';

const Signup = () => {

  const navigationLogin = useNavigation();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [usernameError, setUsernameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const router = useRouter(); 
  const [showModal, setShowModal] = useState(false);
  const [confirmationCodeInput, setConfirmationCodeInput] = useState('');
  const [emailToVerify, setEmailToVerify] = useState('');
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

  const handleSubmit = async () => {
    try {
      setUsernameError('');
      setEmailError('');
  
      if (!username) {
        setUsernameError('Username is required');
        return;
      }
  
      if (!email || !emailRegex.test(email)) {
        setEmailError('Please enter a valid email');
        return;
      }
  
      if (!password) {
        alert('Please enter a password');
        return;
      }
  
      const confirmationCode = Math.floor(100000 + Math.random() * 900000).toString();
  
      const payload = {
        username: username,
        email: email,
        password: password,
        confirmation_code: confirmationCode,
      };
  
      const response = await fetch('http://127.0.0.1:8000/register_mobile/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
  
      const data = await response.json();
  
      if (!response.ok) {
        if (data.error?.includes('username')) {
          setUsernameError(data.error);
        } else if (data.error?.includes('email')) {
          setEmailError(data.error);
        } else {
          alert(data.error || 'Failed to register');
        }
        return;
      }      

      setEmailToVerify(email); 

      setUsername('');
      setEmail('');
      setPassword('');
      setShowModal(true); 
      
    } catch (error) {
      console.error('Registration error:', error);
      alert('Error during registration');
    }
  };

  const verifyConfirmationCode = async () => {
    try {
      const response = await fetch('http://127.0.0.1:8000/verify_code/', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: emailToVerify.trim().toLowerCase(),
      code: confirmationCodeInput.trim()
    })
      });
  
      const data = await response.json();

      console.log('Sending verification:', {
        email: email.trim().toLowerCase(),
        code: confirmationCodeInput.trim()
      });
      
  
      if (response.ok) {
        alert('Email verified successfully!');
        setShowModal(false);
        router.push('/login');
      } else {
        alert(data.error || 'Invalid code. Please try again.');
      }
    } catch (error) {
      console.error('Verification error:', error);
      alert('Verification failed. Try again.');
    }
  };
  
  return (
    <View style={styles.container}>
      <StatusBar style="white" />
      <View style={styles.logoContainer}>
        <Animated.Image entering={FadeInUp.delay(200).duration(1000).springify() } style={[styles.logo, styles.firstLogo]} source={lightLogo} />
        <Animated.Image entering={FadeInUp.delay(400).duration(1000).springify() } style={[styles.logo, styles.secondLogo]} source={lightLogo} />
      </View>
      
      <View style={styles.mainContent}>
        <Animated.Text entering={FadeInUp.duration(1000).springify()} style={styles.title}>Register</Animated.Text>
        
        <Animated.View entering={FadeInDown.duration(1000).springify()} style={styles.inputContainer}>
          <TextInput 
            placeholder="Username" 
            style={styles.input} 
            onChangeText={(text) => setUsername(text)} 
            autoCapitalize="none" 
          />
          {usernameError ? <Text style={styles.errorText}>{usernameError}</Text> : null}
        </Animated.View>

        <Animated.View entering={FadeInDown.delay(200).duration(1000).springify()} style={styles.inputContainer}>
          <TextInput 
            placeholder="Email" 
            style={styles.input} 
            onChangeText={(text) => setEmail(text)}  
            autoCapitalize="none"
          />
          {emailError ? <Text style={styles.errorText}>{emailError}</Text> : null}
        </Animated.View>

        <Animated.View entering={FadeInDown.delay(400).duration(1000).springify()} style={styles.inputContainer}>
          <TextInput 
            placeholder="Password" 
            style={styles.input} 
            secureTextEntry  
            onChangeText={(text) => setPassword(text)} 
            autoCapitalize="none" 
          />
        </Animated.View>

        <TouchableOpacity style={styles.loginButton} onPress={handleSubmit} >
          <Text style={styles.buttonText} >Sign Up</Text>
        </TouchableOpacity>
        
        <Modal
            transparent={true}
            animationType="slide"
            visible={showModal}
            onRequestClose={() => setShowModal(false)}
          >
            <View style={styles.modalOverlay}>
              <View style={styles.modalContainer}>
                <Text style={styles.modalTitle}>Enter Confirmation Code</Text>
                <TextInput
                  style={styles.modalInput}
                  placeholder="e.g. 123456"
                  keyboardType="numeric"
                  onChangeText={setConfirmationCodeInput}
                  value={confirmationCodeInput}
              />
              <TouchableOpacity style={styles.modalButton} onPress={verifyConfirmationCode}>
           <Text style={styles.modalButtonText}>Verify</Text>

      </TouchableOpacity>
    </View>
  </View>
</Modal>


        <View style={styles.signupContainer}>
          <Text>Already have an account? </Text>
          <TouchableOpacity onPress={() => navigationLogin.push('login')}>
            <Text style={styles.signupLink}>Login</Text>
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
  mainContent: { padding: 20, marginTop: 320},
  title: { fontSize: 30, fontWeight: 'bold', textAlign: 'center', marginBottom: 20 },
  inputContainer: { marginBottom: 15 },
  input: { fontSize: 16, padding: 12, backgroundColor: '#f0f0f0', borderRadius: 8 },
  loginButton: { backgroundColor: '#B71C1C', padding: 15, borderRadius: 10, alignItems: 'center'},
  buttonText: { color: 'white', fontSize: 18, fontWeight: 'bold' },
  signupContainer: { flexDirection: 'row', justifyContent: 'center', marginTop: 10 },
  signupLink: { color: '#38bdf8' },
  errorText: { color: 'red', fontSize: 14, marginTop: 5 },
  modalOverlay: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalContainer: {
    width: '80%',
    backgroundColor: 'white',
    padding: 20,
    borderRadius: 10,
    alignItems: 'center',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
  },
  modalInput: {
    width: '100%',
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    marginBottom: 20,
    fontSize: 16,
  },
  modalButton: {
    backgroundColor: '#B71C1C',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 8,
  },
  modalButtonText: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 16,
  },
  
});

export default Signup;

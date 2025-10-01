import { View, Text, StyleSheet, Image } from 'react-native'
import React from 'react'
import logoImg from "@/assets/images/logo.png"
import { Link } from 'expo-router'
import Animated, {SlideInLeft } from 'react-native-reanimated';


const welcome = () => {

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Quest Me</Text>
      <Animated.Image
      entering={SlideInLeft.delay(100).duration(600).springify()}
        source={logoImg}
        resizeMode='contain'
        style={styles.image}
      />

      <View style={styles.buttonContainer}>
        <Link href="/login" style={styles.button}>
          <Text style={styles.buttonText}>Log In</Text>
        </Link>
        <Link href="/signup" style={[styles.button, styles.signUpButton]}>
          <Text style={styles.buttonText}>Sign Up</Text>
        </Link>
      </View>
    </View>
  )
}

export default welcome

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 42,
    fontWeight: 'bold',
    color: 'black',
    marginBottom: 40,
  },
  image: {
    width: 300,
    height: 300,
    marginBottom: 40,
  },
  buttonContainer: {
    width: '30%',
    gap: 16,
  },
  button: {
    backgroundColor: '#B71C1C',
    padding: 8,
    borderRadius: 8,
    alignItems: 'center',
    width: '100%', 
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: '600',
    textAlign: 'center', 
    width: '100%', 
  },
  signUpButton: {
    backgroundColor: '#B71C1C',
  },
})
---
layout: default
title: Home
nav_order: 1
permalink: /
---

# Tactile Board
Here you can find basic information on the Tactile Board Android application.

## What is the Tactile Board?
The Tactile Board is a mobile Augmentative and Alternative Communication (AAC) device for individuals with deafblindness. The Tactile Board allows text and speech to be translated into vibrotactile signs that are displayed real-time to the user via a haptic wearable.

The following video gives a demonstration on how the tactile board works:

[![Tactile Board Demonstration Video](http://img.youtube.com/vi/36bj-6xvPmU/0.jpg)](http://www.youtube.com/watch?v=36bj-6xvPmU)

The application utilizes an online MQTT message broker to pass data between the Tactile Board Android application, the [Tactile Board Listener](https://suitceyes-project-code.github.io/Tactile-Board-Listener/) and the ontology. 

## Getting started
* Open the project in an IDE such as Android Studio
* Sync the project with Gradle files
* Run the application on device or in emulator

## Architecture
The application uses an MVVM architecture and makes use of several Android Jetpack libraries for navigation, databinding and lifecycle management.

## Contributors
* Lea Buchweitz
* James Gay

## Authors
* [James Gay](james.gay@hs-offenburg.de)
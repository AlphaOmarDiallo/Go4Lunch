# Go4Lunch

This repository contains an application for project 7 of the path **Grande École du Numérique**. 

## Identification of the project and the mission

Name and nature: 
Your manager wants you to develop an app to help collaborators choose a place for lunch. For that you will need to get all the restaurants in the area.

Origin: 
Organising lunch between colleagues was not efficient.

Challenge: 
Allow employee to eat alone or with colleagues whenever they want. 

Tech stack used:
* Java
* Firebase
** Firebase Auth
** Firebase Cloud Firestore
* DAGGER2/HILT
* LiveData
* REST API with Retrofit
* Google:
  * Maps API
  * Places API
* GIT

## Project setup

This is an Android application, it is coded in Java and runs on SDK version 31. To run he project, clone this repository and open it on Android Studio. 

## Project architecture

MVVM is the architecture pattern used for this project. Repositories are implemented and dependency injection is done with Dagger2 / Hilt.

## Version Control

We loosely use the "Git flow" approach: master is the release branch - it should always be releasable, and only merged into when we have tested and verified that everything works and is good to go. 

Daily development is done in the development branch depending on the feature being built. Features, bugfixes and other tasks are done as branches off of develop, then merged back into develop directly or via pull requests.

Keep commit clear and self-explanatory. Clean messy branches before merge. 

## Testing

This application is using Hilt, most of the test are in the Android Test folder where we test the behaviour of the complete app. 
Encountered a known Github issue due to compatibility between Dagger2 / Hilt and Gradle. Fixing in progress.  

## How to improve this project

* Handle Github issue
* Update in Kotlin

You can either clone the repository and freely reuse it or you can make a pull request. It will only be accepted once I validate my retraining. 

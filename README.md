# Swedtugal
Android application for indoor localization

### Assignment 1
The first assignment involved creating an app which can detect if the user is standing still, walking or running. This was done by gathering training data, and then running the k-nearest neighbors algorithm.

<img src="https://user-images.githubusercontent.com/16941618/66563071-46927500-eb5d-11e9-935c-73df255e2915.png" width="180" height="300"> <img src="https://user-images.githubusercontent.com/16941618/66562855-c9ff9680-eb5c-11e9-9447-60d1eb23afdb.png" width="180" height="300"> <img src="https://user-images.githubusercontent.com/16941618/66563094-5316cd80-eb5d-11e9-903b-871494ef02d4.png" width="180" height="300"> <img src="https://user-images.githubusercontent.com/16941618/66563121-60cc5300-eb5d-11e9-9911-4b249bbf389e.png" width="180" height="300">

### Assignment 2
The task for this assignment was to create an app that, with some probability, guessed your location on the fifth floor at the EEMCS building at TU Delft. Again we gathered training data for this which meant going to the different parts of this floor and recording the visible access points and their RSS values. During testing we did the same, and then used Bayesian Localization to estimate the user's position.

<img src="https://user-images.githubusercontent.com/16941618/66563272-bc96dc00-eb5d-11e9-8461-82ad387eb266.png" width="400" height="180">

### Assignment 3
In this last assignment we used Particle Filtering for estimating the location of the user. By generting some 4000 or so particles on the screen, the particles would mimick the user's movement and follow him/her on a path around the fifth floor. Whenever particles hit or crossed a wall, we would resample them on another particle's location. Eventually, after following a set path, the particles would converge and reveal the user's location.

<img src="https://user-images.githubusercontent.com/16941618/66563346-e3eda900-eb5d-11e9-9679-25c27bc3a28a.png" width="400" height="180">

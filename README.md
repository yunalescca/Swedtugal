# Swedtugal
Android application for indoor localization

### Assignment 1
The first assignment involved creating an app which can detect if the user is standing still, walking or running. This was done by gathering training data, and then running the k-nearest neighbors algorithm.

### Assignment 2
The task for this assignment was to create an app that, with some probability, guessed your location on the fifth floor at the EEMCS building at TU Delft. Again we gathered training data for this which meant going to the different parts of this floor and recording the visible access points and their RSS values. During testing we did the same, and then used Bayesian Localization to estimate the user's position.

### Assignment 3
In this last assignment we used Particle Filtering for estimating the location of the user. By generting some 4000 or so particles on the screen, the particles would mimick the user's movement and follow him/her on a path around the fifth floor. Whenever particles hit or crossed a wall, we would resample them on another particle's location. Eventually, after following a set path, the particles would converge and reveal the user's location.

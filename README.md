# StressOppress
&lt;hack&gt; Cupertino 3rd place. An Android app that uses vision and machine learning to detect how stressed a user is, and uses a machine learning algorithm with an unsupervised training model to suggest appropriate songs to fit the user's mood.
  
 ## Inspiration: 
We were inspired by the variety of apps that use revolutionary technologies such as AI and CV to make the world spin. We also saw that people want to find just the right song to help them destress, which can often be difficult.

 ## What it does: 
The app takes a picture of the user's face, analyzes it for emotional expression, and then interprets this, keeping the user's past preferences in mind using a preference index calculated by multiplying the prevalence of the emotion by the database value. It then selects a song from a certain genre to fit the user's personality and disposition. After every song, the user is asked to rate their experience, and this data is put into the database. As a result, as the user uses the app more and more, the recommendations become more and more finely tuned to the user's tastes, as they relate to how they feel. A rating of five stars will cause a certain genre to be played more often in response to a certain emotion, while a rating of one will stifle that genre until it gets a chance to be played again. These factors combine with a fluid Android XML layout to provide an immersive experience for the user.

 ## How we built it: 
We used Firebase backend for storing user data, and Google Cloud APIs to implement the computer vision. We also used Android camera libraries for the frontend.

 ## Challenges we ran into: 
Handling GitHub's merge issues, as associated with Gradle build problems. Also API integration issues with Android Studio and Gradle.

 ## Accomplishments that we're proud of: 
Building a functional app using our own machine learning algorithm.

 ## What we learned: 
We learned a lot about GitHub merge error handling, advanced machine learning algorithms, and how to pull an all nighter :)

 ## What's next for Stress Oppress: 
We plan to expand from music recommendations to recommendations for TV shows, places to go, and even movies.

 ## More Info
View the devpost at https://devpost.com/software/stress-oppress or https://devpost.com/software/stressopress.

View the slideshow at https://docs.google.com/presentation/d/1OR-fCqXAEuL_IEZT_oGg7R4-FgPh8QYCUV8OdDQq2Tk/edit?usp=sharing

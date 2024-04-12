# MyZenly
**DISCLAIMER:** it's my first time using jetpack compose, so the code is not perfect. I tried my best to make it as good as possible.

Simple clone of Zenly app. It's a social network for sharing your location with your friends. I picked this topic because I was curious of the technical challenges I would face. 
Also, it has a lot of features that are needed for the final project.
## Screens

![screens.png](images%2Fscreens.png)
[Sketch in Figma, however real design has changed a bit](https://www.figma.com/file/sihHLLmo6SdQCJ4JGHOiB0/Zenly?type=design&node-id=0-1&mode=design)

#### Short excuse
I didn't intentionally copy the colors, but then when I searched for some more screenshots from Zenly 
(in fact I've never used it myself), I noticed that they kinda use the same color palette. But as an excuse 
I can say that many social networks use blue as primary main color (Facebook/Twitter(X)/telegram/LinkedIn (kinda), Skype).
I just like how blue looks like here. [There is a small research that nowadays the overall internet palette gets 
more blue](https://habr-com.translate.goog/ru/articles/764798/?_x_tr_sl=ru&_x_tr_tl=en&_x_tr_hl=en&_x_tr_pto=wapp)

#### Main screen
Main screen is a screen with a red marker of where you are and blue markers of your friends. 
There is the name of the place where you currently are and 2 buttons: people and settings

#### Settings screen
Screen where you can change your name and enable/disable seeing others.

#### People screen
Screen with a list of your friends, requests and people near you. You can click on a user card to find 
them on the map. Near block is a list of people who are near you (500m). Available only if you have 
enabled seeing others in the settings. 

#### Other screens
These screens are so simple, so I don't see any point in describing them in detail:
- Screen for requesting location access which is basically a text and a button. 
- Screen for registration which is a text field and a button

## API used
#### Google Maps SDK and Geocoder API
- Google Maps SDK is used for displaying the map on the main screen
- Geocoder is used to get the name of the place where the user is located

#### Social network API
I created my own backend on fastAPI for this project with a few simple endpoints:
- Creating, getting and updating user
- Creating and rejecting friend requests
- Adding and deleting friends
- There are a few extra endpoints, however they are not used in the app

The session management is quite simple: just a token in the header which is received 
on creating a new user. Disadvantage of this approach is that the token is that user cannot 
easily change his device. But it's not a big deal for this project. 
![endpoints.png](images%2Fendpoints.png)

## Device feature
Quite obvious — location. I also wanted to implement some haptic feedbacks for better **U**ser e**X**perience, but seems like 
it's not really implemented yet, that's why I gave up on this idea. Using default vibration was an option, but I didn't
because I should have created pre-recorded vibrations for each action and I didn't want to spend time on this 
since I already tired of this project.

## Challenges faced
- Main challenge was that jetpack compose is not React or Vue, and I was completely new to it. It shares similar logic, 
but some things are completely different
- Probably lack of useful tutorials and guides. It's still a new thing and up-to-date information is hard to find. 
For example, I struggled with how to make the location getting function look nice in the code, because most articles
are outdated. You can see my first attempts in the git history. Now it's kinda nice
- Retrofit's json decoder did not like fastAPI's datetime format
- My phone has android 11 and didn't support one of the features I used. I spent some time on it until I found out 
that it's simply not supported. After that I had to add a version check and use a different call for older devices

## Future development ideas
- Background location updates
- Notifications
- Chat
- More precise location, e.g. instead of the street name I can name the building if it's well-known. 
E.g. TAMK instead of Kuntokatu
- Adding haptic feedbacks and animations
- More detailed error handling
- Markers clustering (if there are too many markers on the map at the same place they should be grouped)
- Pointers to the nearest friends
- Probably websockets are better. This way it'll be easier to implement constant refreshes

# Teamdr
Test scenarios:
Users: 1, 2, 3, 4

Case 1: Join team 1 by 1
1 adds 2, 3, 4
2, 3 swipe right, 4 swipes left
Result: 1, 2, 3 are now in a team

Case 2: Join group
1 and 2 form a team, 3 and 4 form a team
1 swipes right on Team 3/4, but 2 swipes left
3, 4 receive the request
3 swipes left, 4 swipes right
Result: 1, 2, 3, 4 now in a team

Case 3: Add the same people in different classes,
such that 1, 2, 3, 4 form a team in COMS4118 and COMS4111
Result: 1, 2, 3, 4 form one team in COMS4118 and one team in COMS4111

Tasks to do:

Melanie: 
Sign in/sign up: Homepage
Sign in/sign up is on every single page
  
Bailey:
When you log in, you see Profile page which includes list of classes (add/delete classes here) & what teams you are in (ex. you can see what team you are in in the classes drop down menu)
Profile, search for and create classes
  
Anfal & Amel:
Team search page (one per class), this lets you go through users and swipe left/right - you only see one Team at a time

Updated specs: pre-populate the database with CS classes. Users are not allowed to add a new class to the database, but they can add a new class to their schedule.

Tips: If DB is storing garbage values, do activator clean, activator run.
Ex. {"username":"1","password":null} when right value should be {"username":"turtles","password":"flops"}

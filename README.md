Caleb Richard
CS 283 - Computer Networks

Assignment 0 README

Life Cycles called upon opening the app:

onCreate
onStart
onResume


Life cycles called upon rotating the screen:

onPause
onStop
onDestroy
onCreate
onStart
onResume

On observing which life cycles are called, I see there are two sets of calls. The first
is the call when the app is started. This set of calls is onCreate, onStart, onResume.
These can be seen when the app is first opened, and when the screen is rotated.
The second sequence of calls is onPause, onStop, onDestroy. This occurs as soon as the
screen is rotated. The reason these calls are made is because android actually stops the
instance of the app that is running and creates a new one when the screen is rotated. The 
start sequence of calls are made after onDestroy in order to instantiate the service again 
with the new screen orientation.

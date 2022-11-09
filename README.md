# Android-Gps-APP-
An Android GPS reading application.

This application can read data from GPS sensor, tell you about your latitude and longtitude, and 

# Table of contents
- [Android-Gps-APP-](#android-gps-app-)
- [Table of contents](#table-of-contents)
- [How to use](#how-to-use)
  - [Basic Function](#basic-function)
  - [Metric change](#metric-change)
  - [High Score](#high-score)
  - [Reset](#reset)
  - [Historical Data](#historical-data)
  - [Moving time](#moving-time)

# How to use
## Basic Function
Record location (latitude, longtitude) and speed when you press start, and start to increase using time. You can check "help" button for more information about using this app.

![BasicImg](/resource/img/Basic.PNG)

*Basic display of speed, location, height, distance and time.*

## Metric change
Press any measurement to change metric.

For example, Time(Sec) can be change to Time(Min), Time(Hour) and Time(Day).

![MetricChangeImg](/resource/img/MatricChange.PNG)

*Alternative matric display in KM/H.*

## High Score
Enter high score page by pressing "High" button. In this page you can see the maximum and minimum historical data.

![HighScoreImg](/resource/img/HighScore.PNG)

*High Score pannel*

## Reset
Hit "Reset" button to reset all measurement, including High Score page.

![ResetButtonImg](/resource/img/ResetButton.PNG)

*Reset Button*

![BeforeResetImg](/resource/img/BeforeReset.PNG)

*Before Reset*

![AfterResetImg](/resource/img/AfterReset.PNG)

*After Reset*

![ResetHighScoreImg](/resource/img/HighScoreReset.PNG)

*High Score Reset*

## Historical Data
Historical data will be stored in OnDestory() method. Encryption will be considered in further development for privacy and security purpose.

## Moving time
Moving time increase every second if your speed is non-zero.



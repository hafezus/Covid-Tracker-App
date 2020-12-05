# Covid-Tracker-App
Using Java/Android Studio 

### Before running the app, initial Setup

* Make sure to allow location access always from your Virtual Device
* Allow app to use device storage
* Ensure Android SDK 29 or above is used
* **Note:** The permission is not prompted on launching the app. This must be set by the user in settings.

<hr>
### About the app

1. I have made three accounts on the Database. You may use any of these:
        + username: hafezus password: abc
        + username: testuser password: asd
        + username: testuser1 password: aaa

2. Two of these users have updated their PCR test result for COVID-19 to positive. 
3. The application has 3 main fragment views: The dashboard, the map view, and the PCR test
4. The dashboard and the PCR test views are self-explanatory.
5. The map view uses Geofencing to mark regions where COVID-positive users have entered. This is updated on the database every 30 minutes using a background service. 
        + *Note*: The current location is also sent whenever the user logs in, or if already logged in, start the app. Give it 5 seconds to update on the Map.
6. When there is an update to the database, the user may have to re-enter this map view again to see the update.
7. The geofence uses a Broadcast Receiver for 3 of the following states: 
            + When a user enters a COVID-risk region
            + When the user exits the region, & 
            + When the user still dwells in the same region
8. The geofence broadcast receiver is responsible for sending notifications for the state-transitions mentioned above.
9. When the app is running (active or in background), the broadcast receiver is listening for these state transitions and sends a pop-up notification appropriately.
10. The app also has a logout feature

<hr>
### How to use the app

1. Make sure the permissions are set up manually from the emulator before running the app.
2. Enter app
3. Enter login credentials (Only the above three accounts are registered)
4. Use navigation drawer on the left to navigate to different views
5. Manually set up current location. For this, go to: "More" -> "Location" -> Create/Select saved point -> Set Location
6. To test the notifications, you will have to update your current locations in and out of a geofence.
7. In Covid Result view, you may update your PCR test result as Positive (For active), and Negative (If you no longer have corona)
8. You may logout to stop running the background service for live-tracking
9. 

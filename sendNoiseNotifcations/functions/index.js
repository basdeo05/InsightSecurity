// Used some code here from Firebase's FCM Notification example in https://github.com/firebase/functions-samples

'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendSoundNotification = functions.database.ref('/Users/{userId}/{noiseEvent}')
    .onUpdate(async (change, context) => {
        const userId = context.params.userId;
        const noiseEvent = context.params.noiseEvent;
        const userEmail = admin.database().ref(`/Users/{userId}/userEmail`).once('value');

        // If the value changed has not been changed to true, exit the function
        if (change.after.val() !== true) {
            console.log(change.after.val());
            return console.log('There was a change in user ' + userId + ' with email ' + userEmail + ', but it was not a noise event');

        }

        // If this user has already been notified of a noise event in the last few minutes, exit the function
        const notified = admin.database().ref(`/Users/${userId}/notified`).once("value");
        const notifiedResults = await notified;
        if (notifiedResults.val() === true) {
            return console.log('User ' + userId + ' has already been notified');
        }

        // Otherwise, continue and log the noise event
        console.log('There was a noise event with user ' + userId + ' with email ' + userEmail + '!');

        // Get the list of users
        let users;
        const userList = admin.database().ref('Users').once("value");
        const userResults = await userList;
        users = Object.keys(userResults.val());

        // Call sendToUser for each user 
        for (var i = 0, len = users.length; i < len; i++) {
            sendToUser(users[i], i);
        }

        // Function definition sendToUser
        // Parameters: 'item' is the user id of the user to send the notification to, 'index' is the index of that user in users
        // Preconditions: users is a list of users that a notification should be sent to
        // Postconditions: the noiseEvent field in the user record within the database will be set to true for each user
        function sendToUser(item, index) {
            console.log('User ' + index + ': ' + item);

            // Set noiseEvent for other users
            //const noiseEventPromise = admin.database().ref(`/Users/${index}/noiseEvent`).once("value");
            admin.database().ref(`/Users/${item}/noiseEvent`).set(true)
        }

        // Set this user's notified to true 
        // ERIC NOTE TO SELF SET NOISE EVENT FALSE HERE LATER
        admin.database().ref(`/Users/${userId}/notified`).set(true)

        // Get the list of device notification tokens
        const getDeviceTokensPromise = admin.database().ref(`/Users/${userId}/notificationToken`).once("value");

        // The snapshot to this user's tokens.
        let tokensSnapshot;

        // Await grabbing list of device notification tokens from db
        const results = await Promise.all([getDeviceTokensPromise]);
        tokensSnapshot = results[0];

        console.log('notificationToken all values: ' + tokensSnapshot.val())

        // Notification content
        const payload = {
            notification: {
                title: 'Suspicious noise detected!',
                body: `Touch here to view livestream.`,
                //icon: 
            }
        };

        // Send notifications to all tokens.
        const response = await admin.messaging().sendToDevice(tokensSnapshot.val(), payload);

        /*
        // Check each message for an error with notificationToken
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
            const error = result.error;
            if (error) {
                console.error('Failure sending notification to', tokens[index], error);
            }
        });
        

        return Promise.all(tokensToRemove);
        */
    });
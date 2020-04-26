'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.scheduledFunction = functions.pubsub.schedule('every 5 minutes').onRun(async (change, context) => {
    console.log('Setting all users notified to false');

    // Get the list of users
    let users;
    const userList = admin.database().ref('Users').once("value");
    const userResults = await userList;
    users = Object.keys(userResults.val());

    // Set notified for those users to false
    for (var i = 0, len = users.length; i < len; i++) {
        admin.database().ref(`/Users/${users[i]}/notified`).set(false);
    }

    return null;
});
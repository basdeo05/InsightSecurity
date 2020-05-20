# InsightSecurity
Java Classes:
  Users:
  User Class that defines how users will be added to the database.
  Name, Email, notified, userName, notfication token
    
   Upload:
   Upload class that defines how pictures will be uploaded to the database.
   name (userEmail), image, timeStamp
    
   Main Activity1:
   Sign in with google authentication configuration. Grabbing user information after sign in
   to pass to different views.
   
   Main Activity2:
   User chooses to be a camera or viewer. Passes email to viewer view. Passes database childId
   and email to camera view.
   
   Images Actvity:
   Take email passed from Main Activity2. Load pictures from database that have same email passed to this view. Displays 
   images and time picture was taken.
   
   Main Activity 4:
   Camera mode activity. As soon you get to this view start listeing for decibels. User can choose timer mode as well. Whichever
   event happens first will be sent to take picture activity. Trigger noiseEvent to true with child ID passed to this view.
   So notification can be sent to all phones. 
   
   Main Activtiy 3:
   Takes picture taken by camera and upload to database using upload class configurations. After upload is completed
   takes you back to camera mode and start listening and starts timer if timer mode was chosen. 
   
   takePictureActivty:
   Open camera, create preview and take picture.
   
   - Richard
    
        
  

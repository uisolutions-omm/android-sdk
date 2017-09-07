# Wifi Sdk
This open-source library allows you to integrate Wifi sdk into your Android app.

# Steps to integrate sdk into your project
These are the minimal steps required to integrate the adjust SDK into your Android project. We are going to assume that you use Android Studio for your Android development and target an Android API level 15
  - Download or clone the project 
  - Import Wifi module ```File -> New -> Import Module  ```
  
 ![image1](https://user-images.githubusercontent.com/18575369/30159529-83a22e3e-93e6-11e7-93cb-a67702179cd4.JPG)

  - The  ```Wifi ```  module should be imported into your Android Studio project
  
 ![image2](https://user-images.githubusercontent.com/18575369/30159943-fc213ef8-93e7-11e7-8901-20b6b99de130.JPG)
 
  - Open the ``` build.gradle  ``` file of your app and find the ``` dependencies ``` block. Add the following line:
 ```sh 
  compile project(':WifiProject') 
  ```
 ![image4](https://user-images.githubusercontent.com/18575369/30160019-591263d0-93e8-11e7-8d0d-aa2a412a8f43.JPG)
 
  - Include the following dependency to project level ```build.gradle ```
  ```sh 
  classpath 'com.google.gms:google-services:3.0.0
  ```
 ![image3](https://user-images.githubusercontent.com/18575369/30159973-190447d6-93e8-11e7-8261-8fe5fe33e3f4.JPG)
 
  - Add following line to your ```Application Class ```
  ```sh 
  BranchActivity.branchInstance(getApplicationContext());
  ```
 ![image5](https://user-images.githubusercontent.com/18575369/30160025-5e8a17e0-93e8-11e7-9e33-d317b7039cdb.JPG)
  
  - Add the following line in your first run to recharge your clients with data
  ```sh
   BranchActivity.branchInitialization(getApplicationContext());
  ```
 ![image6](https://user-images.githubusercontent.com/18575369/30160030-63a50dd4-93e8-11e7-9401-455b6b11012e.JPG)

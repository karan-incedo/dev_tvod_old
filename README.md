# android-appcms

ViewliftSDK README

To utilize the ViewliftSDK you must first include the project in your Android project.  
1) Import the module into your existing project by clicking File -> New -> Import Module.  Browse for and select the folder that contains the AppCMSSDK library module.
2) Make sure the library is listed at the top of your settings.gradle file.  Ex:
Include ‘:AppCMSSDK’…
3) Open your app module’s build.gradle file and a new line into the dependencies:
dependencies {
	compile project(“:AppCMSSDK”)
}

Second, you must initialize the SDK.  Add the following code to your App’s Activity onCreate() method
ppCMSSDK viewliftSDK = AppCMSSDK.initialize(this);
The only parameter is the Android context.

Third, you should call the Films API to retrieve the Film data associated with a specific Film ID.  The Film API also includes the capability to retrieve multiple Film datums from multiple Film IDs.  The URL is constructed using the base API URL, a comma delimited list of Film IDS and the value for the Site ID.  The result contains descriptive details about each film and the streaming information (such as HLS data).

Fourth, you may call launchPlayer() with a Film HLS URL and Ad URL.  
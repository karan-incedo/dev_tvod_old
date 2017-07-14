# android-appcms

AppCMSSK README

To utilize the AppCMSSDK you must first include the project in your Android project.  
1) Import the module into your existing project by clicking File -> New -> Import Module.  Browse for and select the folder that contains the AppCMSSDK library module.
2) Make sure the library is listed at the top of your settings.gradle file.  Ex:
Include ‘:AppCMSSDK’…
3) Open your app module’s build.gradle file and a new line into the dependencies:
dependencies {
	compile project(“:AppCMSSDK”)
}

Second, you must initialize the SDK.  Add the following code to your App’s Activity onCreate() method
ppCMSSDK appCMSSDK = AppCMSSDK.initialize(this,
        getString(R.string.app_cms_baseurl),
        getString(R.string.app_cms_site_id));
The first parameter is the Android context.
The app_cms_baseurl is a String resource that points to the AppCMS main.json file.
The app_cms_site_id is a String resource that points to the Site ID of main.json file.

Third, you may call launchPlayer() with a Film ID.  That Film ID will be associated with a film on the website.

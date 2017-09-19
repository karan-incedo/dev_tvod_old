# Android-AppCMS

AppCMS is a digital content management system designed to be portable and easy to use. It is targeted to those that long for a simple solution that is extremely flexible.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them:

```
Android Studio 3.0
```
Link to Android Studio 3.0 can be found at [Android Studio Preview](https://developer.android.com/studio/preview/index.html)

### Installing

To make sure you get a development environment running. Follow the instructions found in the link (it will guide you on how to install the IDE and what other tools necessary.

```
* Install Android Studio 3.0.
* Check for updates.
* Sync Project.
```
#### Note: 
If you're upgrading a project that's using an alpha version of Android plugin 3.0.0 to Android plugin 3.0.0-beta1, you'll need to first clean your project by selecting Build > Clean Project from the menu bar.

In order to be able to generate build APKs for release (after upgrading to beta):

1. Add
```
lintOptions {
        checkReleaseBuilds false
        abortOnError false
    }
``` 
in the 
```
android { 

}
``` 
section of your module level ```build.gradle``` file,
    
2. Clean Project, 
3. Rebuild Project.

#### After Installing successfully
```
* Open the Module level build.gradle file,
* Change your packageName,
* Change your customApplicationId,
* Change your hostName,
* Change your versionCode & your versionName to what you need for your project,
* Finally, sync your project.
```
Then

```
* Go to AppCMS/src/main/res/drawable and add your icons, logos, etc that you need for your project.
* Go to AppCMS/src/main/res/values/app_cms_api and change your app_cms_base_url and your app_cms_app_name.
```

## Deployment

In order to deploy this onto the [Google Play Store](https://play.google.com/store?hl=en) you will need to:

```
* Navigate to the toolbar and click on Build > Generate Signed APK...
* Pick your module,
* Select your key store path,
* Enter your Key store password,
* Enter your Key alias,
* Enter your Key password,
* Click Next.
```
Then 
```
* Choose APK destination folder
* Choose Build type
* Choose Flavors
* Select Signature Versions
* Click Finish
```
Finally
```
Upload your generated apk through your Google Developer Play Console and then publish.
```

## Builds, Frameworks & Libraries used:

* [Amazon Web Services S3](https://aws.amazon.com/s3/) - Cloud Storage Service.
* [Gradle](https://gradle.org/) - Build Tool & Dependency Management.
* [ButterKnife](http://jakewharton.github.io/butterknife/) - Field and method binding for Android views.
* [RxJava](https://github.com/ReactiveX/RxJava) - Composing asynchronous and Event-based programs using Observable Sequences.
* [Retrofit](http://square.github.io/retrofit/) - HTTP client.
* [Dagger2](https://google.github.io/dagger/) - Dependency Injection.
* [Urban Airship](https://docs.urbanairship.com/platform/) - Push Notifications.
* [Glide](https://github.com/bumptech/glide) - Image downloading and caching.
* [Exoplayer](https://github.com/google/ExoPlayer) - Media Player.
* [AppsFlyer](https://support.appsflyer.com/hc/en-us/articles/207032126-AppsFlyer-SDK-Integration-Android) - App Tracking and Attribution.
* [Stag](https://github.com/vimeo/stag-java) - Speedy Type Adapter Generation - (De)Serialization of Json Objects.

## Contributing

Please read [CONTRIBUTING.md](https://github.com/snagfilms/android-appcms/blob/master/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Authors

See the full list of [contributors](https://github.com/snagfilms/android-appcms/graphs/contributors) and [members](https://github.com/snagfilms/android-appcms/network/members) for this project.

### Contact Us
You can contact us at android-dev@viewlift.com

## License

--

## Testimonials

Android-AppCMS is currently being used in [SnagFilms](https://www.snagfilms.com/), [HoiChoiTv](http://www.hoichoi.tv/) and [AgendaTV](http://agendatv.viewlift.com/).

If you have used Android-AppCMS for your application and want to publicize this information, we would be more than happy to hear from you and add it to the list.

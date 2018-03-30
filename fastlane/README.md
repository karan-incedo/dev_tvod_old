fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
## Android
### android test
```
fastlane android test
```
Runs all the tests
### android deploy
```
fastlane android deploy
```
Deploy a new version to the Google Play
### android buildLane
```
fastlane android buildLane
```
Mobile track app. Deploy a new version to the Google Play Store - Beta channel
### android tvbeta
```
fastlane android tvbeta
```
TV release app. Upload new version of TV App on Partner Portal
### android supply_onplaystore
```
fastlane android supply_onplaystore
```
Supply build and metadata on playstore
### android phones_screenshots
```
fastlane android phones_screenshots
```
Take phone screenshots
### android seveninchtablet_screenshots
```
fastlane android seveninchtablet_screenshots
```
Take seven inch tablet screenshots
### android teninchtablet_screenshots
```
fastlane android teninchtablet_screenshots
```
Take ten inch tablet screenshots
### android tv_screenshots
```
fastlane android tv_screenshots
```
Take tv screenshots
### android slackSendMessage
```
fastlane android slackSendMessage
```
Send Messages on Slack
### android buildFailedLane
```
fastlane android buildFailedLane
```
Send Failed Messages on Slack
### android updateTheVersion
```
fastlane android updateTheVersion
```

### android get_version_play_store
```
fastlane android get_version_play_store
```


----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).

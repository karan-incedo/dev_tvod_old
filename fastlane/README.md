fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

## Choose your installation method:

<table width="100%" >
<tr>
<th width="33%"><a href="http://brew.sh">Homebrew</a></td>
<th width="33%">Installer Script</td>
<th width="33%">Rubygems</td>
</tr>
<tr>
<td width="33%" align="center">macOS</td>
<td width="33%" align="center">macOS</td>
<td width="33%" align="center">macOS or Linux with Ruby 2.0.0 or above</td>
</tr>
<tr>
<td width="33%"><code>brew cask install fastlane</code></td>
<td width="33%"><a href="https://download.fastlane.tools">Download the zip file</a>. Then double click on the <code>install</code> script (or run it in a terminal window).</td>
<td width="33%"><code>sudo gem install fastlane -NV</code></td>
</tr>
</table>

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
### android mobilebeta
```
fastlane android mobilebeta
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

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).

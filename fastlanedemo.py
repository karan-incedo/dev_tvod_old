import urllib, json, subprocess
from collections import OrderedDict
import sys
import os

fileDirPath = os.path.dirname(os.path.abspath(__file__))

platform = sys.argv[1];

url = sys.argv[2];

response = urllib.urlopen(url)
data = json.loads(response.read(), object_pairs_hook=OrderedDict)

file = open(fileDirPath + '/AppCMS/src/main/assets/version.properties', 'w')
file.close()

param = ""

siteName = sys.argv[3]
buildId = sys.argv[4]

siteId = ""
baseUrl = ""
hostName = ""
appResourcePath = ""
appName = ""
fullDescription = ""
shortDescription = ""
appVersionName = ""
appVersionCode = ""
appPackageName = ""
apptentiveApiKey = ""
keystoreFileName = ""
aliasName = ""
keystorePass = ""
track = ""
jsonKeyFile = ""
promoVideo = ""
appTitle = ""
featureGraphic = ""
promoGraphic = ""
tvBanner = ""
appIcon = ""
urbanAirshipDevKey = ""
urbanAirshipDevSecret = ""
urbanAirshipProdKey = ""
urbanAirshipProdSecret = ""
isUrbanAirshipInProduction = ""
gcmSender = ""
facebookAppId = ""
appsFlyerDevKey = ""
appsFlyerProdKey = ""
whatsNew = ""

for k in data.keys():

    if k == "siteId":
        keyval = "SiteId" + ":" + data[k]
        siteId = data[k]

    elif k == "baseUrl":
        keyval = "BaseUrl" + ":" + data[k]
        baseUrl = data[k]

    elif k == "hostName":
        keyval = "HostName" + ":" + data[k]
        hostName = data[k]

    elif k == "resourcePath":
        keyval = "AppResourcesPath" + ":" + data[k]
        appResourcePath = data[k]

    elif k == "appName":
        keyval = "AppName" + ":" + data[k]
        appName = data[k]

    elif k == "description":
        keyval = "FullDescription" + ":" + data[k]
        fullDescription = data[k]
        with open(fileDirPath + "/fastlane/metadata/android/en-US/full_description.txt",
                  "w") as myfile:
            myfile.write(fullDescription.encode("utf-8") + "\n")

    elif k == "shortDescription":
        keyval = "ShortDescription" + ":" + data[k]
        shortDescription = data[k]
        with open(fileDirPath + "/fastlane/metadata/android/en-US/short_description.txt",
                  "w") as myfile:
            myfile.write(shortDescription.encode("utf-8") + "\n")

    elif k == "appVersion":
        keyval = "AppVersionName" + ":" + data[k]
        appVersionName = data[k]

    elif k == "packageName":
        keyval = "AppPackageName" + ":" + data[k]
        appPackageName = data[k]

    elif k == "apptentiveApiKey":
        keyval = "ApptentiveApiKey" + ":" + data[k]
        apptentiveApiKey = data[k]

    elif k == "keystoreFile":
        keyval = "KeystorePath" + ":" + data[k]
        keystoreFileName = data[k]

    elif k == "aliasName":
        keyval = "AliasName" + ":" + data[k]
        aliasName = data[k]

    elif k == "keystorePassword":
        keyval = "Keystorepass" + ":" + data[k]
        keystorePass = data[k]

    elif k == "track":
        keyval = "Track" + ":" + data[k]
        track = data[k]

    elif k == "jsonKeyFile":
        keyval = "JsonKeyFile" + ":" + data[k]
        jsonKeyFile = data[k]

    elif k == "promoVideo":
        keyval = "PromoVideoUrl" + ":" + data[k]
        promoVideo = data[k]
        with open(fileDirPath + "/fastlane/metadata/android/en-US/video.txt", "w") as myfile:
            myfile.write(promoVideo.encode("utf-8") + "\n")

    elif k == "appTitle":
        keyval = "AppTitle" + ":" + data[k]
        appTitle = data[k]
        with open(fileDirPath + "/fastlane/metadata/android/en-US/title.txt", "w") as myfile:
            myfile.write(appTitle.encode("utf-8") + "\n")

    elif k == "featureGraphic":
        keyval = "FeatureGraphicUrl" + ":" + data[k]
        featureGraphic = data[k]

    elif k == "promoGraphic":
        keyval = "PromoGraphicUrl" + ":" + data[k]
        promoGraphic = data[k]

    elif k == "tvBanner":
        keyval = "TvBannerUrl" + ":" + data[k]
        tvBanner = data[k]

    elif k == "appIcon":
        keyval = "AppIconUrl" + ":" + data[k]
        appIcon = data[k]

    elif k == "urbanAirshipDevKey":
        keyval = "UAirshipDevelopmentAppKey" + ":" + data[k]
        urbanAirshipDevKey = data[k]

    elif k == "urbanAirshipDevSecret":
        keyval = "UAirshipDevelopmentAppSecret" + ":" + data[k]
        urbanAirshipDevSecret = data[k]

    elif k == "urbanAirshipProdKey":
        keyval = "UAirshipProductionAppKey" + ":" + data[k]
        urbanAirshipProdKey = data[k]

    elif k == "urbanAirshipProdSecret":
        keyval = "UAirshipProductionAppSecret" + ":" + data[k]
        urbanAirshipProdSecret = data[k]

    elif k == "isUrbanAirshipInProduction":
        keyval = "UAirshipInProduction" + ":" + data[k]
        isUrbanAirshipInProduction = data[k]

    elif k == "gcmSender":
        keyval = "GcmSender" + ":" + data[k]
        gcmSender = data[k]

    elif k == "facebookAppId":
        keyval = "FacebookAppId" + ":" + data[k]
        facebookAppId = data[k]

    elif k == "appsFlyerDevKey":
        keyval = "AppsFlyerDevKey" + ":" + data[k]
        appsFlyerDevKey = data[k]

    elif k == "appsFlyerProdKey":
        keyval = "AppsFlyerProdKey" + ":" + data[k]
        appsFlyerProdKey = data[k]

    elif k == "buildVersion":
        keyval = "AppVersionCode" + ":" + data[k]
        appVersionCode = data[k]

    elif k == "whatsNew":
        keyval = "WhatsNew" + ":" + data[k]
        whatsNew = data[k]
        with open(fileDirPath + "/fastlane/metadata/android/en-US/changelogs/" + appVersionCode + ".txt","w") as myfile:
             myfile.write(whatsNew.encode("utf-8") + "\n")
            # else:
            #   keyval = k + ":" + data[k]

    with open(fileDirPath + "/AppCMS/src/main/assets/version.properties", "a") as myfile:
         myfile.write(keyval.encode("utf-8") + "\n")


        # whatsNew = "Here details regarding what's new you are providing for this version"
        # with open(fileDirPath + "/fastlane/metadata/android/en-US/changelogs/"+appVersionCode+".txt", "w") as myfile:
        # myfile.write(whatsNew.encode("utf-8")+"\n")

param = siteId + " " \
        + baseUrl + " " \
        + appVersionName + " " \
        + appName + " " \
        + appVersionCode + " " \
        + appPackageName + " " \
        + appResourcePath + " " \
        + apptentiveApiKey + " " \
        + keystoreFileName + " " \
        + aliasName + " " \
        + keystorePass + " " \
        + track + " " \
        + jsonKeyFile + " " \
        + featureGraphic + " " \
        + promoGraphic + " " \
        + tvBanner + " " \
        + appIcon + " " \
        + urbanAirshipDevKey + " " \
        + urbanAirshipDevSecret + " " \
        + urbanAirshipProdKey + " " \
        + urbanAirshipProdSecret + " " \
        + isUrbanAirshipInProduction + " " \
        + gcmSender + " " \
        + facebookAppId + " " \
        + appsFlyerDevKey + " " \
        + appsFlyerProdKey + " " \
        + siteName + " " \
        + buildId

# print param

if platform == "android":
    subprocess.call("sh " + fileDirPath + "/fastlanemobilescripts_temp.sh " + param, shell=True)
elif platform == "firetv":
    subprocess.call("sh " + fileDirPath + "/fastlanetvscripts_temp.sh " + param, shell=True)

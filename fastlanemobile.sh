#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "DIR"$DIR
cd $DIR

POST_URL="${18}/${16}/android/appcms/build/status"
UPLOAD_URL="${18}/${16}/appcms/android/build/apk/link"


echo "111111111"
echo ${19}
echo "***********"
echo $19
echo "***********"
echo "111111111"



echo 'piyush ****'
echo $POST_URL
echo ${16}
echo ${18}
echo 'piyush *****'
echo ${21}
echo 'piyush *****'
echo ${22}


postBuildStatus(){ #buildid, posturl, status, errormsg
    BODY_DATA="{\"buildId\":$1,\"status\":\"$3\",\"errorMessage\":\"$4\"}"
    BODY_DATA="{\"buildId\":$1,\"status\":\"$3\",\"errorMessage\":\"$4\",\"message\":\"$5\",\"percentComplete\":$6,\"isAppOnStore\":\"$7\",\"buildVersion\":$8}"
    echo "\n**********BUILD_STATUS_UPDATE**********\nPOST_URL=$2\nBODY_DATA=["$BODY_DATA"]\n---------------------------------------"
    curl -H 'Content-Type: application/json' -X POST -d "$BODY_DATA" $2
}

postUpdateLink(){ #buildid, posturl, status, errormsg
    BODY_DATA="{\"buildId\":$1,\"apkLink\":\"$3\",\"platform\":\"android\"}"
    echo "\n**********BUILD_STATUS_UPDATE**********\UPLOAD_URL=$2\nBODY_DATA=["$BODY_DATA"]\n---------------------------------------"
    curl -H 'Content-Type: application/json' -X PUT -d "$BODY_DATA" $2
}

downloadFile(){ #url, output, buildid, posturl
    status=$(curl -s -w %{http_code} $1 -o $2)
    if [ "$status" -eq 200 ]
        then
        echo "File downloaded:[$1]"
    else
        echo "Missing required file:[$1]"
        postBuildStatus $3 $4 'FAILED' "Missing required file:$1"
        trap "echo exitting because my child killed me due to asset file not found" 0
        exit 1
    fi
}


base64 --decode ./AppCMS/crfile.txt > ./AppCMS/google-services.json
base64 --decode ./credentialfile.txt > ./googleplay_android.json

# if [ "${16}" -eq "0" ]
#         then
# else
#         echo "App is not Present on Play Store"
# fi

postBuildStatus ${17} $POST_URL "STARTED" "No ERROR" "Build Successfully Started" 5 false 0
#1: Slack First Message. Build Success Full Started

fastlane android slackSendMessage my_slack_msg:"${4} -> ANDROID BUILD SUCCESSFULLY STARTED. BUILD-ID -> ${17}. VERSION-NUMBER -> ${3}. Build Triggered By --> ${23}" my_user_name:"Viewlift fastlane" mySlackUrl:"{24}"


rm -rf ./AppCMS/src/main/res/drawable/logo.jpg
rm -rf ./AppCMS/src/main/res/drawable/logo.png

rm -rf ./AppCMS/src/main/res/drawable-xhdpi/logo.jpg
rm -rf ./AppCMS/src/main/res/drawable-xhdpi/logo.png

rm -rf ./AppCMS/src/main/res/drawable-xxxhdpi/logo.jpg
rm -rf ./AppCMS/src/main/res/drawable-xxxhdpi/logo.png

rm -rf ./AppCMS/src/main/res/drawable-xxhdpi/logo.jpg
rm -rf ./AppCMS/src/main/res/drawable-xxhdpi/logo.png

rm -rf ./AppCMS/src/main/res/drawable/logo_icon.jpg
rm -rf ./AppCMS/src/main/res/drawable/logo_icon.png

rm -rf ./AppCMS/src/main/res/drawable-xhdpi/logo_icon.jpg
rm -rf ./AppCMS/src/main/res/drawable-xhdpi/logo_icon.png

rm -rf ./AppCMS/src/main/res/drawable-xxxhdpi/logo_icon.jpg
rm -rf ./AppCMS/src/main/res/drawable-xxxhdpi/logo_icon.png

rm -rf ./AppCMS/src/main/res/drawable-xxhdpi/logo_icon.jpg
rm -rf ./AppCMS/src/main/res/drawable-xxhdpi/logo_icon.png


echo "viewlift"

echo $1
echo ${1}

echo "viewlift"


aws s3 cp s3://${19}/$1/build/android/resource/drawable-xhdpi ./AppCMS/src/main/res/drawable --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build drawable resources" 6 " " 0

aws s3 cp s3://${19}/$1/build/android/resource/drawable ./AppCMS/src/main/res/drawable --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build drawable resources" 6 " " 0
aws s3 cp s3://${19}/$1/build/android/resource/drawable-xhdpi ./AppCMS/src/main/res/drawable-xhdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build drawable resources" 7 " " 0
aws s3 cp s3://${19}/$1/build/android/resource/drawable-xxxhdpi ./AppCMS/src/main/res/drawable-xxxhdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build drawable resources" 8 " " 0
aws s3 cp s3://${19}/$1/build/android/resource/drawable-xxhdpi ./AppCMS/src/main/res/drawable-xxhdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build drawable resources" 9 " " 0


#2: Slack Second Message. Downloading Resources
fastlane android slackSendMessage my_slack_msg:"${4} -> ANDROID DOWNLOADING RESOURCES AND METADATA. BUILD-ID -> ${17}. VERSION-NUMBER -> ${3}. Build Triggered By --> ${23}" my_user_name:"Viewlift fastlane" mySlackUrl:"{24}"


aws s3 cp s3://${19}/$1/build/android/resource/mipmap-hdpi ./AppCMS/src/main/res/mipmap-hdpi/ --recursive
aws s3 cp s3://${19}/$1/build/android/resource/mipmap-hdpi ./AppCMS/src/mobile/res/mipmap-hdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build mipmap resources" 10 " " 0

aws s3 cp s3://${19}/$1/build/android/resource/mipmap-mdpi ./AppCMS/src/main/res/mipmap-mdpi/ --recursive
aws s3 cp s3://${19}/$1/build/android/resource/mipmap-mdpi ./AppCMS/src/mobile/res/mipmap-mdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build mipmap resources" 11 " " 0

aws s3 cp s3://${19}/$1/build/android/resource/mipmap-xhdpi ./AppCMS/src/main/res/mipmap-xhdpi/ --recursive
aws s3 cp s3://${19}/$1/build/android/resource/mipmap-xhdpi ./AppCMS/src/mobile/res/mipmap-xhdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build mipmap resources" 12 " " 0

aws s3 cp s3://${19}/$1/build/android/resource/mipmap-xxhdpi ./AppCMS/src/main/res/mipmap-xxhdpi/ --recursive
aws s3 cp s3://${19}/$1/build/android/resource/mipmap-xxhdpi ./AppCMS/src/mobile/res/mipmap-xxhdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build mipmap resources" 13 " " 0

aws s3 cp s3://${19}/$1/build/android/resource/mipmap-xxxhdpi ./AppCMS/src/main/res/mipmap-xxxhdpi/ --recursive
aws s3 cp s3://${19}/$1/build/android/resource/mipmap-xxxhdpi ./AppCMS/src/mobile/res/mipmap-xxxhdpi/ --recursive
postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build mipmap resources" 14 " " 0

rm -rf ./fastlane/metadata

rm -rf ./AppCMS/build/outputs/apk/mobile/debug/AppCMS-mobile-nonkiswe-debug.apk 
rm -rf ./AppCMS/build/outputs/apk/androidTest/mobile/debug/AppCMS-mobile-nonkiswe-debug-androidTest.apk  
rm -rf ./AppCMS/build/outputs/apk/mobile/release/AppCMS-mobile-nonkiswe-release-unsigned.apk
rm -rf ./AppCMS/build/outputs/apk/mobile/release/AppCMS-mobile-nonkiswe-release.apk


fastlane supply init --package_name $6 --json_key ./googleplay_android.json

IS_APP_ONPLAYSTORE="$?"

echo "$IS_APP_ONPLAYSTORE"

if [ "$IS_APP_ONPLAYSTORE" -eq "0" ]
        then
        echo "App is Present on Play Store"

        postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading FeatureGraphic" 15 "true" -1
        downloadFile ${12} ./fastlane/metadata/android/en-US/images/featureGraphic.png ${17} $POST_URL
        postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading PromoGraphic" 16 "true" -1
        downloadFile ${13} ./fastlane/metadata/android/en-US/images/promoGraphic.png ${17} $POST_URL
        postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the TV Banner" 17 "true" -1
        downloadFile ${14} ./fastlane/metadata/android/en-US/images/tvBanner.png ${17} $POST_URL
        postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the icon images" 18 "true" -1
        downloadFile ${15} ./fastlane/metadata/android/en-US/images/icon.png ${17} $POST_URL

        postBuildStatus ${17} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the MetaData of the Application" 19 "true" -1

        echo ${21} > ./fastlane/metadata/android/en-US/full_description.txt
        echo ${22} > ./fastlane/metadata/android/en-US/short_description.txt
        echo ${4} > ./fastlane/metadata/android/en-US/title.txt

        slackMessageThree = "${4}" + " - ANDROID BUILD SUCCESSFULLY STARTED. BUILD-ID:" + "${17}" + ". VERSION-NUMBER:" + "${3}"

        #3: Slack Second Message. Downloading Resources
        fastlane android slackSendMessage my_slack_msg:"${4} -> ANDROID UPDATING VERSION PROPERTIES OF THE APPLICATION. BUILD-ID -> ${17}. VERSION-NUMBER -> ${3}. Build Triggered By --> ${23}" my_user_name:"Viewlift fastlane" mySlackUrl:"{24}"

        #Now we need to check the Version of App on the Play Store.
        postBuildStatus ${17} $POST_URL "UPDATING_VERSION_PROPERTIES" "No ERROR" "Fetching latest version from playstore and incrementing the same" 19 " " 0
        fastlane android updateTheVersion app_package_name:$6 buildid:${17} json_key_file:./googleplay_android.json posturl:$POST_URL
        postBuildStatus ${17} $POST_URL "UPDATING_VERSION_PROPERTIES" "No ERROR" "Version Name is Incremented" 20 " " 0

else
        echo "App is not Present on Play Store"
fi

postBuildStatus ${17} $POST_URL "CONFIGURING_BUILD" "No ERROR" "Checking if app already exists on the App Store" 25 " " 0

fastlane android buildLane app_package_name:$6 buildid:${17} app_apk_path:./AppCMS/build/outputs/apk/mobileNonKiswe/debug/AppCMS-mobile-nonkiswe-debug.apk tests_apk_path:./AppCMS/build/outputs/apk/androidTest/mobileNonKiswe/debug/AppCMS-mobile-nonkiswe-debug-androidTest.apk posturl:$POST_URL keystore_path:$8 alias:${9} storepass:${20} apk_path:./AppCMS/build/outputs/apk/mobileNonKiswe/release/AppCMS-mobile-nonkiswe-release-unsigned.apk track:${10} json_key_file:./googleplay_android.json username:"Viewlift fastlane" mySlackUrl:${24} myAppName:${4} myAppVersion:${3} myEmailId:${23} myBuildId:${17}

IS_APP_SUCCESS="$?"

echo "$IS_APP_SUCCESS"

if [ "$IS_APP_SUCCESS" -eq "0" ]
        then
        
        echo "Uploading Apk File On Partner Portal"

        postBuildStatus ${17} $POST_URL "BUILD_PROGRESS" "No ERROR" "Build Generated and Preparing the Build for upload to S3 Bucket" 70 " " 0
       
        myApkName="${4}-mobile.apk"

        mv ./AppCMS/build/outputs/apk/mobileNonKiswe/release/AppCMS-mobile-nonkiswe-release.apk "./AppCMS/build/outputs/apk/mobileNonKiswe/release/${myApkName}"

        aws s3 cp "./AppCMS/build/outputs/apk/mobileNonKiswe/release/${myApkName}" s3://${19}/$1/build/android/
       
        postBuildStatus ${17} $POST_URL "BUILD_PROGRESS" "No ERROR" "Build Created Successfully and Fetching the link from S3 Bucket" 75 " " 0

        postUpdateLink ${17} $UPLOAD_URL "http://appcms-config.s3.amazonaws.com/$1/build/android/${myApkName}" 


        if [ "$IS_APP_ONPLAYSTORE" -eq "0" ]
                then
                echo "App is Present on Play Store"
                postBuildStatus ${17} $POST_URL "SUCCESS_S3_BUCKET" "No ERROR" "Uploaded to the S3 Bucket" 80 "true" 0
                sleep 5
                postBuildStatus ${17} $POST_URL "UPLOADING_PLAY_STORE" "No ERROR" "Uploading the App MetaData and Build to Play Store" 85 "true" 0
                fastlane android supply_onplaystore package_name:$6 track:${10} json_key_file:./googleplay_android.json apk_path:./AppCMS/build/outputs/apk/mobileNonKiswe/release/${myApkName} buildid:${17} posturl:$POST_URL mySlackUrl:${24} myAppName:${4} myAppVersion:${3} myEmailId:${23} myBuildId:${17}

        else
                echo "App is not Present on Play Store"
                postBuildStatus ${17} $POST_URL "SUCCESS_S3_BUCKET" "No ERROR" "Download Apk and go to <a href='https://play.google.com/apps/publish/' target='_blank'> Playstore </a> for first time manual Uploading" 100 false 0
                trap "echo exiting because first apk is not Present on playstore" 0
                exit 1
        fi

else
        echo "Error Building"
        # postBuildStatus ${17} $POST_URL "FAILED_BUILD_ERROR" "ERROR" "Build Generaton Failed" 65 " " 0
        trap "echo exitting because my child killed me due to asset file not found" 0
        exit 1
fi


# postBuildStatus ${17} $POST_URL "SUCCESS_PLAY_STORE" "No ERROR" "Successfully Uploaded to Play Store and Apk link is Updated" 100 " " 0




#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "DIR"$DIR
cd $DIR

POST_URL="${14}/${12}/fireTv/appcms/build/status"
UPLOAD_URL="${14}/${12}/appcms/fireTv/build/apk/link"

echo 'piyush ****'

echo $POST_URL
echo ${12}
echo ${14}
echo 'piyush *****'

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


rm -rf ./AppCMS/src/main/res/drawable-xhdpi/app_logo.jpg
rm -rf ./AppCMS/src/main/res/drawable-xhdpi/app_logo.png

rm -rf ./AppCMS/src/tv/res/drawable-xhdpi/app_logo.jpg
rm -rf ./AppCMS/src/tv/res/drawable-xhdpi/app_logo.png


rm -rf ./AppCMS/src/main/res/drawable-xhdpi/tv_logo.jpg
rm -rf ./AppCMS/src/main/res/drawable-xhdpi/tv_logo.png

rm -rf ./AppCMS/src/tv/res/drawable-xhdpi/tv_logo.jpg
rm -rf ./AppCMS/src/tv/res/drawable-xhdpi/tv_logo.png


base64 --decode ./AppCMS/crfile.txt > ./AppCMS/google-services.json
base64 --decode ./credentialfile.txt > ./googleplay_android.json


if [ "'$1'" = "'00000149-86ec-d4f3-a7e9-e6fe760d0000'" ]
then
   cp ./Apps/Snagfilms/colors.xml ./AppCMS/src/main/res/values/colors.xml
   echo "Default Snagfilms.xml"

elif [ "'$1'" = "'7fa0ea9a-9799-4417-99f5-cbb5343c551d'" ]
then
   cp ./Apps/Hoichoi/colors.xml ./AppCMS/src/main/res/values/colors.xml
   echo "Default hoichoi.xml"

elif [ "'$1'" = "'00000151-11b4-d29b-a17d-55fdb2b80000'" ]
then
   cp ./Apps/MonumentalSports/colors.xml ./AppCMS/src/main/res/values/colors.xml
   echo "Default Monumental.xml"

elif [ "'$1'" = "'6ea6f2a0-451c-4d87-8060-051f8c07db23'" ]
then
   cp ./Apps/FailArmy/colors.xml ./AppCMS/src/main/res/values/colors.xml
   echo "Default FailArmy.xml"

elif [ "'$1'" = "'0000014c-edbe-da12-a1fc-fdbe2f120000'"]
then
   cp ./Apps/LSN/colors.xml ./AppCMS/src/main/res/values/colors.xml
   echo "Default TampaBay.xml"

elif [ "'$1'" = "'3f6f15c7-8454-462f-917e-2c427c95fa1d'" ]
then
   cp ./Apps/TampaBay/colors.xml ./AppCMS/src/main/res/values/colors.xml
   echo "Default TampaBay.xml"

else
   echo "Default Colors.xml"
   cp ./Apps/Snagfilms/colors.xml ./AppCMS/src/main/res/values/colors.xml
fi


postBuildStatus ${13} $POST_URL "STARTED" "No ERROR" "Build Successfully Started" 5 false 0


aws s3 cp s3://${15}/$1/build/fireTv/resource/drawable ./AppCMS/src/main/res/drawable --recursive
aws s3 cp s3://${15}/$1/build/fireTv/resource/drawable ./AppCMS/src/tv/res/drawable --recursive

postBuildStatus ${13} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build resources" 10 " " 0

aws s3 cp s3://${15}/$1/build/fireTv/resource/drawable-xhdpi ./AppCMS/src/main/res/drawable-xhdpi/ --recursive
aws s3 cp s3://${15}/$1/build/fireTv/resource/drawable-xhdpi ./AppCMS/src/tv/res/drawable-xhdpi/ --recursive


postBuildStatus ${13} $POST_URL "DOWNLOADING_RESOURCES" "No ERROR" "Downloading the build resources" 15 " " 0

postBuildStatus ${13} $POST_URL "BUILD_PROGRESS" "No ERROR" "Build is In Progress." 25 " " 0

fastlane android tvbeta app_package_name:$6 buildid:${13} app_apk_path:./AppCMS/build/outputs/apk/tv/debug/AppCMS-tv-debug.apk tests_apk_path:./AppCMS/build/outputs/apk/androidTest/tv/debug/AppCMS-tv-debug-androidTest.apk posturl:$POST_URL keystore_path:$8 alias:${9} storepass:${16} apk_path:./AppCMS/build/outputs/apk/tv/release/AppCMS-tv-release-unsigned.apk mySlackUrl:"{18}" myAppName:${4} myAppVersion:${3} myEmailId:${17} myBuildId:'${13}'

IS_APP_SUCCESS="$?"

echo "Piyush"
echo "$IS_APP_SUCCESS"

if [ "$IS_APP_SUCCESS" -eq "0" ]
        then
        postBuildStatus ${13} $POST_URL "BUILD_PROGRESS" "No ERROR" "Build Created Successfully and Preparing Build to Upload on S3 Bucket" 75 " " 0
        # aws s3 cp ./AppCMS/build/outputs/apk/mobile/debug/AppCMS-mobile-debug.apk s3://appcms-config/$1/build/android/
        aws s3 cp ./AppCMS/build/outputs/apk/tv/release/AppCMS-tv-release.apk s3://appcms-config/$1/build/fireTv/

        postBuildStatus ${13} $POST_URL "BUILD_PROGRESS" "No ERROR" "Build Created Successfully and Fetching the link from S3 Bucket" 80 " " 0
        postUpdateLink ${13} $UPLOAD_URL "http://appcms-config.s3.amazonaws.com/$1/build/fireTv/AppCMS-tv-release.apk" 

        postBuildStatus ${13} $POST_URL "SUCCESS_S3_BUCKET" "No ERROR" "Download Apk and go to <a href='https://developer.amazon.com/app-submission' target='_blank'> Amazon Appstore </a> for manual Uploading" 100 " " 0

        # postBuildStatus ${13} $POST_URL "SUCCESS_S3_BUCKET" "No ERROR" "Build Created Successfully and Available for Download" 100 " " 0

else
        echo "Error Building"
        trap "echo exitting because my child killed me due to asset file not found" 0
        exit 1
fi







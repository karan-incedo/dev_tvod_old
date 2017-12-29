#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "DIR"$DIR
cd $DIR
#cd ./AndroidAppCMS
echo "TV SCRIPTS STARTED"

#POST URL
POST_URL="http://staging4.partners.viewlift.com/${27}/android/appcms/build/status"

postBuildStatus(){ #buildid, posturl, status, errormsg
    BODY_DATA="{\"buildId\":$1,\"status\":\"$3\",\"errorMessage\":\"$4\"}"
    echo "\n**********BUILD_STATUS_UPDATE**********\nPOST_URL=$2\nBODY_DATA=["$BODY_DATA"]\n---------------------------------------"
    curl -H 'Content-Type: application/json' -X POST -d "$BODY_DATA" $2
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

postBuildStatus ${28} $POST_URL "STARTED" ""

rm -f googleplay_android.json

wget -P ./ ${13}

#Copy app resources and relpace in project directory
downloadFile $2$7/drawable/app_logo.png ./AppCMS/src/tv/res/drawable/app_logo.png ${28} $POST_URL
downloadFile $2$7/drawable/app_icon_small_front.png ./AppCMS/src/tv/res/drawable/app_icon_small_front.png ${28} $POST_URL
downloadFile $2$7/drawable/footer_gradient_image.png ./AppCMS/src/tv/res/drawable/footer_gradient_image.png ${28} $POST_URL
downloadFile $2$7/drawable/home_screen_background.png ./AppCMS/src/tv/res/drawable/home_screen_background.png ${28} $POST_URL
downloadFile $2$7/drawable/image_sample.png ./AppCMS/src/tv/res/drawable/image_sample.png ${28} $POST_URL
downloadFile $2$7/drawable/info_icon.png ./AppCMS/src/tv/res/drawable/info_icon.png ${28} $POST_URL
downloadFile $2$7/drawable/poster_image_placeholder.png ./AppCMS/src/tv/res/drawable/poster_image_placeholder.png ${28} $POST_URL
downloadFile $2$7/drawable/snagfilms_app_logo.png ./AppCMS/src/tv/res/drawable/snagfilms_app_logo.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_0.png ./AppCMS/src/tv/res/drawable/spinner_0.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_1.png ./AppCMS/src/tv/res/drawable/spinner_1.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_2.png ./AppCMS/src/tv/res/drawable/spinner_2.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_3.png ./AppCMS/src/tv/res/drawable/spinner_3.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_4.png ./AppCMS/src/tv/res/drawable/spinner_4.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_5.png ./AppCMS/src/tv/res/drawable/spinner_5.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_6.png ./AppCMS/src/tv/res/drawable/spinner_6.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_7.png ./AppCMS/src/tv/res/drawable/spinner_7.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_8.png ./AppCMS/src/tv/res/drawable/spinner_8.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_9.png ./AppCMS/src/tv/res/drawable/spinner_9.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_10.png ./AppCMS/src/tv/res/drawable/spinner_10.png ${28} $POST_URL
downloadFile $2$7/drawable/spinner_11.png ./AppCMS/src/tv/res/drawable/spinner_11.png ${28} $POST_URL
downloadFile $2$7/drawable/video_image_placeholder.png ./AppCMS/src/tv/res/drawable/video_image_placeholder.png ${28} $POST_URL
downloadFile $2$7/drawable/vp_placeholder_960x480.png ./AppCMS/src/tv/res/drawable/vp_placeholder_960x480.png ${28} $POST_URL

#Download all xml files
downloadFile $2$7/drawable/appcms_edittext_background.xml ./AppCMS/src/tv/res/drawable/appcms_edittext_background.xml ${28} $POST_URL
downloadFile $2$7/drawable/button_background.xml ./AppCMS/src/tv/res/drawable/button_background.xml ${28} $POST_URL
downloadFile $2$7/drawable/grid_border.xml ./AppCMS/src/tv/res/drawable/grid_border.xml ${28} $POST_URL
downloadFile $2$7/drawable/gridview_cell_border.xml ./AppCMS/src/tv/res/drawable/gridview_cell_border.xml ${28} $POST_URL
downloadFile $2$7/drawable/navigation_item_selector.xml ./AppCMS/src/tv/res/drawable/navigation_item_selector.xml ${28} $POST_URL
downloadFile $2$7/drawable/navigation_selector.xml ./AppCMS/src/tv/res/drawable/navigation_selector.xml ${28} $POST_URL
downloadFile $2$7/drawable/progressbar_animation_list.xml ./AppCMS/src/tv/res/drawable/progressbar_animation_list.xml ${28} $POST_URL

downloadFile $2$7/drawable-xhdpi/call_icon.png ./AppCMS/src/tv/res/drawable-xhdpi/call_icon.png ${28} $POST_URL
downloadFile $2$7/drawable-xhdpi/email_icon.png ./AppCMS/src/tv/res/drawable-xhdpi/email_icon.png ${28} $POST_URL
downloadFile $2$7/drawable-xhdpi/focused_off.png ./AppCMS/src/tv/res/drawable-xhdpi/focused_off.png ${28} $POST_URL
downloadFile $2$7/drawable-xhdpi/focused_on.png ./AppCMS/src/tv/res/drawable-xhdpi/focused_on.png ${28} $POST_URL
downloadFile $2$7/drawable-xhdpi/search_icon.png ./AppCMS/src/tv/res/drawable-xhdpi/search_icon.png ${28} $POST_URL
downloadFile $2$7/drawable-xhdpi/unfocused_off.png ./AppCMS/src/tv/res/drawable-xhdpi/unfocused_off.png ${28} $POST_URL
downloadFile $2$7/drawable-xhdpi/unfocused_on.png ./AppCMS/src/tv/res/drawable-xhdpi/unfocused_on.png ${28} $POST_URL

#Update the app feature graphic
downloadFile ${14} ./fastlane/metadata/android/en-US/images/featureGraphic.png ${28} $POST_URL

#Update the app promo graphic
downloadFile ${15} ./fastlane/metadata/android/en-US/images/promoGraphic.png ${28} $POST_URL

#Update the TV banner graphic
downloadFile ${16} ./fastlane/metadata/android/en-US/images/tvBanner.png ${28} $POST_URL

#Update the playstore app icon
downloadFile ${17} ./fastlane/metadata/android/en-US/images/icon.png ${28} $POST_URL

postBuildStatus ${28} $POST_URL "CONFIGURED_FOR_BUILDING" ""

#fetch data from playstore and get metadata by providing package name and json credentials file detail
fastlane supply init --package_name $6 --json_key ./googleplay_android.json

IS_APP_ONPLAYSTORE="$?"

if [ $IS_APP_ONPLAYSTORE != "0" ]; then
    postBuildStatus ${28} $POST_URL "FOUND_APP_FAILED" "App is not found on playstore. App build will continue and provide you apk to upload on playstore in the end"
else
    postBuildStatus ${28} $POST_URL "FOUND_APP" ""
fi

emulator -avd Android_TV_API_24 & adb wait-for-device

sleep 5s # Waits 5 seconds.

#Generate TV Release singed APK
echo "Generating TV Release APK"
fastlane android tvbeta app_package_name:$6 buildid:${28} app_apk_path:./AppCMS/build/outputs/apk/tv/debug/AppCMS-tv-debug.apk tests_apk_path:./AppCMS/build/outputs/apk/androidTest/tv/debug/AppCMS-tv-debug-androidTest.apk posturl:$POST_URL keystore_path:$9 alias:${10} storepass:${11} apk_path:./AppCMS/build/outputs/apk/tv/release/AppCMS-tv-release-unsigned.apk

echo "Uploading TV App Apk File On Partner Portal"

aws s3 cp ./AppCMS/build/outputs/apk/tv/release/AppCMS-tv-release.apk s3://appcms-config/$1/_temp/build/fireTv/

#Apk link of TV App
echo "TV App Apk link: http://appcms-config.s3.amazonaws.com/'$1'/_temp/build/androidtv/AppCMS-tv-release.apk"

curl \
--header "Content-type: application/json" \
--request PUT \
--data '{"buildId":"'${28}'","apkLink":"http://appcms-config.s3.amazonaws.com/'$1'/_temp/build/androidtv/AppCMS-tv-release.apk","errorMessage":""}' \
http://staging4.partners.viewlift.com/${27}/appcms/android/build/apk/link

postBuildStatus ${28} $POST_URL "BINARY_UPLOADED_PARTNER_PORTAL" ""


if [ $IS_APP_ONPLAYSTORE != "0" ]; then
 postBuildStatus ${28} $POST_URL "SUCCESS_PARTNER_PORTAL" ""
else
  #fastlane android supply_onplaystore package_name:$6 track:${12} json_key_file:./googleplay_android.json apk_path:./AppCMS/build/outputs/apk/tv/release/AppCMS-tv-release.apk

    if [ "$?" != "0" ]; then
       postBuildStatus ${28} $POST_URL "FAILED" "Playstore upload failed"
       exit 1
    else
       postBuildStatus ${28} $POST_URL "SUCCESS" ""
    fi
fi


#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "DIR"$DIR
cd $DIR
#cd ./AndroidAppCMS

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
downloadFile $7/drawable/add_to_watchlist.png ./AppCMS/src/main/res/drawable/add_to_watchlist.png ${28} $POST_URL
downloadFile $7/drawable/cancel.png ./AppCMS/src/main/res/drawable/cancel.png ${28} $POST_URL
downloadFile $7/drawable/categories_page_categories.png ./AppCMS/src/main/res/drawable/categories_page_categories.png ${28} $POST_URL
downloadFile $7/drawable/crossicon.png ./AppCMS/src/main/res/drawable/crossicon.png ${28} $POST_URL
downloadFile $7/drawable/featured_icon.png ./AppCMS/src/main/res/drawable/featured_icon.png ${28} $POST_URL
downloadFile $7/drawable/features_mobile.png ./AppCMS/src/main/res/drawable/features_mobile.png ${28} $POST_URL
downloadFile $7/drawable/features_tablet.png ./AppCMS/src/main/res/drawable/features_tablet.png ${28} $POST_URL
downloadFile $7/drawable/home_page_.png ./AppCMS/src/main/res/drawable/home_page_.png ${28} $POST_URL
downloadFile $7/drawable/ic_deleteicon.png ./AppCMS/src/main/res/drawable/ic_deleteicon.png ${28} $POST_URL
downloadFile $7/drawable/info_icon.png ./AppCMS/src/main/res/drawable/info_icon.png ${28} $POST_URL
downloadFile $7/drawable/logo.png ./AppCMS/src/main/res/drawable/logo.png ${28} $POST_URL
downloadFile $7/drawable/logo_icon.xml ./AppCMS/src/main/res/drawable/logo_icon.xml ${28} $POST_URL
downloadFile $7/drawable/menu_.png ./AppCMS/src/main/res/drawable/menu_.png ${28} $POST_URL
downloadFile $7/drawable/play_icon.png ./AppCMS/src/main/res/drawable/play_icon.png ${28} $POST_URL
downloadFile $7/drawable/remove_from_watchlist.png ./AppCMS/src/main/res/drawable/remove_from_watchlist.png ${28} $POST_URL
downloadFile $7/drawable/cancel.png ./AppCMS/src/main/res/drawable/cancel.png ${28} $POST_URL
downloadFile $7/drawable/share.png ./AppCMS/src/main/res/drawable/share.png ${28} $POST_URL
downloadFile $7/drawable/tickicon.png ./AppCMS/src/main/res/drawable/tickicon.png ${28} $POST_URL

#Download all xml files
downloadFile $7/drawable/anim_cast.xml ./AppCMS/src/main/res/drawable/anim_cast.xml ${28} $POST_URL
downloadFile $7/drawable/border_rectangular.xml ./AppCMS/src/main/res/drawable/border_rectangular.xml ${28} $POST_URL
downloadFile $7/drawable/cc_toggle_selector.xml ./AppCMS/src/main/res/drawable/cc_toggle_selector.xml ${28} $POST_URL
downloadFile $7/drawable/ic_cash_card_logo.xml ./AppCMS/src/main/res/drawable/ic_cash_card_logo.xml ${28} $POST_URL
downloadFile $7/drawable/ic_closed_caption_disable_24dp.xml ./AppCMS/src/main/res/drawable/ic_closed_caption_disable_24dp.xml ${28} $POST_URL
downloadFile $7/drawable/ic_closed_caption_enable_24dp.xml ./AppCMS/src/main/res/drawable/ic_closed_caption_enable_24dp.xml ${28} $POST_URL
downloadFile $7/drawable/ic_credit_card_logo.xml ./AppCMS/src/main/res/drawable/ic_credit_card_logo.xml ${28} $POST_URL
downloadFile $7/drawable/ic_debig_card_logo.xml ./AppCMS/src/main/res/drawable/ic_debig_card_logo.xml ${28} $POST_URL
downloadFile $7/drawable/ic_mobile_payments_logo.xml ./AppCMS/src/main/res/drawable/ic_mobile_payments_logo.xml ${28} $POST_URL
downloadFile $7/drawable/ic_mobile_wallet_logo.xml ./AppCMS/src/main/res/drawable/ic_mobile_wallet_logo.xml ${28} $POST_URL
downloadFile $7/drawable/ic_net_banking_logo.xml ./AppCMS/src/main/res/drawable/ic_net_banking_logo.xml ${28} $POST_URL
downloadFile $7/drawable/ic_radio_bg.xml ./AppCMS/src/main/res/drawable/ic_radio_bg.xml ${28} $POST_URL
downloadFile $7/drawable/ic_radio_button_checked_24dp.xml ./AppCMS/src/main/res/drawable/ic_radio_button_checked_24dp.xml ${28} $POST_URL
downloadFile $7/drawable/ic_radio_button_unchecked_24dp.xml ./AppCMS/src/main/res/drawable/ic_radio_button_unchecked_24dp.xml ${28} $POST_URL
downloadFile $7/drawable/ic_tv_24dp.xml ./AppCMS/src/main/res/drawable/ic_tv_24dp.xml ${28} $POST_URL
downloadFile $7/drawable/star_icon.xml ./AppCMS/src/main/res/drawable/star_icon.xml ${28} $POST_URL
downloadFile $7/drawable/star_icon_no_fill.xml ./AppCMS/src/main/res/drawable/star_icon_no_fill.xml ${28} $POST_URL
downloadFile $7/drawable/tab_indicator_default.xml ./AppCMS/src/main/res/drawable/tab_indicator_default.xml ${28} $POST_URL
downloadFile $7/drawable/tab_indicator_selected.xml ./AppCMS/src/main/res/drawable/tab_indicator_selected.xml ${28} $POST_URL
downloadFile $7/drawable/toolbar_cast_0.xml ./AppCMS/src/main/res/drawable/toolbar_cast_0.xml ${28} $POST_URL
downloadFile $7/drawable/toolbar_cast_1.xml ./AppCMS/src/main/res/drawable/toolbar_cast_1.xml ${28} $POST_URL
downloadFile $7/drawable/toolbar_cast_2.xml ./AppCMS/src/main/res/drawable/toolbar_cast_2.xml ${28} $POST_URL
downloadFile $7/drawable/toolbar_cast_connected.xml ./AppCMS/src/main/res/drawable/toolbar_cast_connected.xml ${28} $POST_URL
downloadFile $7/drawable/toolbar_cast_disconnected.xml ./AppCMS/src/main/res/drawable/toolbar_cast_disconnected.xml ${28} $POST_URL

downloadFile $7/drawable-xhdpi/crossicon.png ./AppCMS/src/main/res/drawable-xhdpi/crossicon.png ${28} $POST_URL
downloadFile $7/drawable-xhdpi/ic_download.png ./AppCMS/src/main/res/drawable-xhdpi/ic_download.png ${28} $POST_URL
downloadFile $7/drawable-xhdpi/ic_downloaded.png ./AppCMS/src/main/res/drawable-xhdpi/ic_downloaded.png ${28} $POST_URL
downloadFile $7/drawable-xhdpi/tickicon.png ./AppCMS/src/main/res/drawable-xhdpi/tickicon.png ${28} $POST_URL

downloadFile $7/drawable-xxhdpi/crossicon.png ./AppCMS/src/main/res/drawable-xxhdpi/crossicon.png ${28} $POST_URL
downloadFile $7/drawable-xxhdpi/ic_download.png ./AppCMS/src/main/res/drawable-xxhdpi/ic_download.png ${28} $POST_URL
downloadFile $7/drawable-xxhdpi/ic_downloaded.png ./AppCMS/src/main/res/drawable-xxhdpi/ic_downloaded.png ${28} $POST_URL
downloadFile $7/drawable-xxhdpi/tickicon.png ./AppCMS/src/main/res/drawable-xxhdpi/tickicon.png ${28} $POST_URL


downloadFile $7/drawable-xxxhdpi/amex.png ./AppCMS/src/main/res/drawable-xxxhdpi/amex.png ${28} $POST_URL
downloadFile $7/drawable-xxxhdpi/discover.png ./AppCMS/src/main/res/drawable-xxxhdpi/discover.png ${28} $POST_URL
downloadFile $7/drawable-xxxhdpi/mastercard.png ./AppCMS/src/main/res/drawable-xxxhdpi/mastercard.png ${28} $POST_URL
downloadFile $7/drawable-xxxhdpi/verified.png ./AppCMS/src/main/res/drawable-xxxhdpi/verified.png ${28} $POST_URL
downloadFile $7/drawable-xxxhdpi/visa.png ./AppCMS/src/main/res/drawable-xxxhdpi/visa.png ${28} $POST_URL
downloadFile $7/drawable-xxxhdpi/ic_chevron_right_white_36dp.png ./AppCMS/src/main/res/drawable-xxxhdpi/ic_chevron_right_white_36dp.png ${28} $POST_URL

downloadFile $7/mipmap-hdpi/app_logo.png ./AppCMS/src/mobile/res/mipmap-hdpi/app_logo.png ${28} $POST_URL
downloadFile $7/mipmap-mdpi/app_logo.png ./AppCMS/src/mobile/res/mipmap-mdpi/app_logo.png ${28} $POST_URL
downloadFile $7/mipmap-xhdpi/app_logo.png ./AppCMS/src/mobile/res/mipmap-xhdpi/app_logo.png ${28} $POST_URL
downloadFile $7/mipmap-xxhdpi/app_logo.png ./AppCMS/src/mobile/res/mipmap-xxhdpi/app_logo.png ${28} $POST_URL
downloadFile $7/mipmap-xxxhdpi/app_logo.png ./AppCMS/src/mobile/res/mipmap-xxxhdpi/app_logo.png ${28} $POST_URL


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

emulator -avd Nexus_5X_API_23 & adb wait-for-device

sleep 5s # Waits 5 seconds.

#emulator -avd Nexus_10_API_24 & adb wait-for-device

#sleep 5s

fastlane android mobilebeta app_package_name:$6 buildid:${28} app_apk_path:./AppCMS/build/outputs/apk/mobile/debug/AppCMS-mobile-debug.apk tests_apk_path:./AppCMS/build/outputs/apk/androidTest/mobile/debug/AppCMS-mobile-debug-androidTest.apk posturl:$POST_URL keystore_path:$9 alias:${10} storepass:${11} apk_path:./AppCMS/build/outputs/apk/mobile/release/AppCMS-mobile-release-unsigned.apk

echo "Uploading Apk File On Partner Portal"

aws s3 cp ./AppCMS/build/outputs/apk/mobile/release/AppCMS-mobile-release.apk s3://appcms-config/$1/_temp/build/android/

curl \
--header "Content-type: application/json" \
--request PUT \
--data '{"buildId":"'${28}'","apkLink":"http://appcms-config.s3.amazonaws.com/'$1'/_temp/build/android/AppCMS-mobile-release.apk","errorMessage":""}' \
http://staging4.partners.viewlift.com/${27}/appcms/android/build/apk/link

postBuildStatus ${28} $POST_URL "BINARY_UPLOADED_PARTNER_PORTAL" ""

if [ $IS_APP_ONPLAYSTORE != "0" ]; then
 postBuildStatus ${28} $POST_URL "SUCCESS_PARTNER_PORTAL" ""

else
  #fastlane android supply_onplaystore package_name:$6 track:${12} json_key_file:./googleplay_android.json apk_path:./AppCMS/build/outputs/apk/mobile/release/AppCMS-mobile-release.apk
    if [ "$?" != "0" ]; then
       postBuildStatus ${28} $POST_URL "FAILED" "Playstore upload failed"
       exit 1
    else
       postBuildStatus ${28} $POST_URL "SUCCESS" ""
    fi
fi


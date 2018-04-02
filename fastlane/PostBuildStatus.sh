#!/bin/bash


echo "POSTING BUILD STATUS"
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "DIR"$DIR
cd $DIR

echo $2
echo ${5}
echo $5

postBuildStatus(){ #buildid, posturl, status, errormsg
    # BODY_DATA="{\"buildId\":$1,\"status\":\"$3\",\"errorMessage\":\"$4\"}"
    BODY_DATA="{\"buildId\":$1,\"status\":\"$3\",\"errorMessage\":\"$4\",\"message\":\"$5\",\"percentComplete\":$6,\"isAppOnStore\":\"$7\",\"buildVersion\":$8}"
    echo "\n**********BUILD_STATUS_UPDATE**********\nPOST_URL=$2\nBODY_DATA=["$BODY_DATA"]\n---------------------------------------"
    curl -H 'Content-Type: application/json' -X POST -d "$BODY_DATA" $2
}

postBuildStatus $1 $2 "$3" "$4" "$5" $6 $7 $8






#! /bin/bash
keytool -exportcert -keystore $1 -alias $2 -storepass $3 -keypass $4 | openssl sha1 -binary | openssl base64

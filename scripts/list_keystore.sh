#! /bin/bash
keytool -list -v -keystore $1 -alias $2 -storepass $3 -keypass $4 

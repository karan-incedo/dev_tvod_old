#! /bin/bash

keytool -genkey -v -keystore $1 -alias $2 -keyalg RSA -keysize 2048 -validity 10000


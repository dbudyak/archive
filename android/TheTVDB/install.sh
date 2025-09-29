#!/bin/bash

APP_NAME=com.thetvdb
MAIN_ACTIVITY_PATH=ui.LoginActivity

APK_PATH=app/build/outputs/apk/app-debug.apk

if [ -f $APK_PATH ]; then rm $APK_PATH; fi

./gradlew assemble 

if [ -f $APK_PATH ];
then
	for device in `adb devices | grep -E "device$" | awk '{print $1}'`; do
		adb -s $device shell pm clear $APP_NAME
		adb -s $device install -r $APK_PATH
	done
	for device in `adb devices | grep -E "device$" | awk '{print $1}'`; do
		adb -s $device shell am force-stop $APP_NAME
		adb -s $device shell am start -n $APP_NAME/$APP_NAME.$MAIN_ACTIVITY_PATH
	done
fi

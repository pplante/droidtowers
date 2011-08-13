#! /bin/bash

STABLE_NAME="libgdx-0.9.1"
STABLE_URL="http://libgdx.googlecode.com/files/${STABLE_NAME}.zip"
DOWNLOAD_PATH="/tmp/${STABLE_NAME}.zip"
EXTRACT_PATH="/tmp/${STABLE_NAME}"

PROJECT_DIR=`pwd`

echo "* Downloading latest STABLE from:"
echo 	$STABLE_URL
echo

curl -# -o $DOWNLOAD_PATH $STABLE_URL
DOWNLOAD_SUCCESS=$?

if [[ $DOWNLOAD_SUCCESS != 0 ]]; then
	echo 
	echo "Download failed, aborting."
	exit $DOWNLOAD_SUCCESS
fi;

echo
echo "* Unziping STABLE zip: ${DOWNLOAD_PATH}"
echo

unzip -qqo $DOWNLOAD_PATH -d $EXTRACT_PATH
UNZIP_SUCCESS=$?

if [[ $UNZIP_SUCCESS != 0 ]]; then
	echo 
	echo "Unzip failed, aborting."
	exit $UNZIP_SUCCESS
fi;

echo
echo "* Copying android libs..."
echo

ANDROID_LIBS_PATH="${PROJECT_DIR}/android/libs/."
cp -R "${EXTRACT_PATH}/armeabi" $ANDROID_LIBS_PATH
cp -R "${EXTRACT_PATH}/armeabi-v7a" $ANDROID_LIBS_PATH
cp "${EXTRACT_PATH}/gdx-backend-android.jar" $ANDROID_LIBS_PATH
cp "${EXTRACT_PATH}/gdx-backend-android-sources.jar" $ANDROID_LIBS_PATH

echo
echo "* Copying desktop libs..."
echo

DESKTOP_LIBS_PATH="${PROJECT_DIR}/desktop/libs/."
cp "${EXTRACT_PATH}/gdx-backend-lwjgl-natives.jar" $DESKTOP_LIBS_PATH
cp "${EXTRACT_PATH}/gdx-backend-lwjgl.jar" $DESKTOP_LIBS_PATH
cp "${EXTRACT_PATH}/gdx-natives.jar" $DESKTOP_LIBS_PATH

echo
echo "* Copying shared libs..."
echo

SHARED_LIBS_PATH="${PROJECT_DIR}/main/libs/."
cp "${EXTRACT_PATH}/gdx-sources.jar" $SHARED_LIBS_PATH
cp "${EXTRACT_PATH}/gdx.jar" $SHARED_LIBS_PATH

echo
echo "Complete!"
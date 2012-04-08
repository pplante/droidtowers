#! /bin/bash

curl --location http://local.happydroids.com/game-updates/download/latest > /tmp/previous-build.jar

java -jar tools/jarpatch-0.6.jar -new out/desktop-jar/DroidTowers-release.jar -old /tmp/previous-build.jar -out out/patch.jar

echo "Patch created!"

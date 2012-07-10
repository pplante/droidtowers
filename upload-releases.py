#! /usr/bin/env python

import re
import sys
import semver
import requests
import json
from datetime import datetime
from getpass import getpass
from pbs import git, ant, scp, pwd, cd

SCP_TARGET_PATH = 'pplante@happydroids.com:/var/www/happydroids.com/public/alphas'
TOWER_CONSTS_JAVA = './happydroids-common/src/com/happydroids/HappyDroidConsts.java'

if __name__ == '__main__':
    try:
        upload = scp.bake(i='/Users/pplante/.ssh/id_rsa', _fg=True)

        upload('./out/DroidTowers.exe', '%s/DroidTowers.exe' % (SCP_TARGET_PATH,))
        upload('./out/DroidTowers.zip', '%s/DroidTowers.zip' % (SCP_TARGET_PATH,))
        upload('./out/desktop-jar/DroidTowers-release.jar', '%s/DroidTowers.jar' % (SCP_TARGET_PATH,))
        upload('./out/android_google/android_google-release.apk', '%s/droidtowers-google.apk' % (SCP_TARGET_PATH,))
        upload('./out/android_amazon/android_amazon-release.apk', '%s/droidtowers-amazon.apk' % (SCP_TARGET_PATH,))

        print "http://www.happydroids.com/alphas/DroidTowers.exe"
        print "http://www.happydroids.com/alphas/DroidTowers.zip"
        print "http://www.happydroids.com/alphas/DroidTowers.jar"
        print "http://www.happydroids.com/alphas/droidtowers-google.apk"
        print "http://www.happydroids.com/alphas/droidtowers-amazon.apk"
    except Exception, e:
        print e
        sys.exit(1)


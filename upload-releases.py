#! /usr/bin/env python

import re
import sys
import semver
import requests
import json
from datetime import datetime
from getpass import getpass
from pbs import git, ant, scp, pwd, cd, ssh


def retreive_build_number():
    return open('build.ver').read()

if __name__ == '__main__':
    try:
        releases_root = '/apps/www.happydroids.com/public/releases'
        new_release_root = '%s/%s' % (releases_root, retreive_build_number())
        SCP_TARGET_PATH = 'pplante@happydroids.com:%s' % (new_release_root,)

        upload = scp.bake(i='/Users/pplante/.ssh/id_rsa', r=True, C=True, _fg=True)
        remote_cmd = ssh.bake('pplante@happydroids.com', i='/Users/pplante/.ssh/id_rsa', _fg=True)

        remote_cmd('mkdir %s' % (new_release_root,))

        upload('./out/applet/', SCP_TARGET_PATH)

        upload('./out/DroidTowers.exe', '%s/DroidTowers.exe' % (SCP_TARGET_PATH,))
        upload('./out/DroidTowers.zip', '%s/DroidTowers.zip' % (SCP_TARGET_PATH,))
        #upload('./out/android_google/android_google-release.apk', '%s/droidtowers-google.apk' % (SCP_TARGET_PATH,))
        #upload('./out/android_amazon/android_amazon-release.apk', '%s/droidtowers-amazon.apk' % (SCP_TARGET_PATH,))

        remote_cmd('rm %s/current' % (releases_root,))
        remote_cmd('ln -s %s %s/current' % (new_release_root, releases_root,))

        print "http://www.happydroids.com/releases/current/DroidTowers.exe"
        print "http://www.happydroids.com/releases/current/DroidTowers.zip"
        # print "http://www.happydroids.com/releases/current/DroidTowers.jar"
        # print "http://www.happydroids.com/releases/current/droidtowers-google.apk"
        # print "http://www.happydroids.com/releases/current/droidtowers-amazon.apk"
    except Exception, e:
        print e
        sys.exit(1)


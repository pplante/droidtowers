#! /usr/bin/env python

import re
import sys
import semver
import requests
import json
import tempfile
from datetime import datetime
from getpass import getpass
from pbs import git, ant, scp, pwd, cd

SCP_TARGET_PATH = 'pplante@happydroids.com:/var/www/happydroids.com/public/alphas'
TOWER_CONSTS_JAVA = './happydroids-common/src/com/happydroids/HappyDroidConsts.java'
GOOGLE_MANIFEST_PATH = './android-google/AndroidManifest.xml'
AMAZON_MANIFEST_PATH = './android-amazon/AndroidManifest.xml'

debug_flag_re = re.compile(r'(boolean DEBUG = (?:true|false);)')
server_url_re = re.compile(r'(String HAPPYDROIDS_SERVER = "(?:.+?)";)')
server_https_re = re.compile(r'(String HAPPYDROIDS_URI = "(?:.+?)" \+ HAPPYDROIDS_SERVER;)')
version_re = re.compile(r'(String VERSION = "(?:.+?)";)')
git_sha_re = re.compile(r'(String GIT_SHA = "(?:.+?)";)')

android_manifest_re = re.compile(r'package="com.happydroids.droidtowers"\s+android:versionCode="\d+"\s+android:versionName=".+?">')

def retrieve_git_revision():
    git_status = git.status('--porcelain').strip()
    if len(git_status) > 0:
        print("Git status reports there are changes to be committed.")
        if raw_input("Continue? [y/n]") == 'n':
            sys.exit(1)

    return git('rev-parse', 'HEAD').strip()


def retreive_build_number():
    return open('build.ver').read()


def git_check_for_tag_collision(new_build_number):
    existing_tags = git.tag().split('\n')
    for tag in existing_tags:
        if tag == new_build_number:
            raise Exception('Build number has already been used as a tag in git, aborting.')


def prompt_for_new_build_number(previous_build_number):
    while True:
        try:
            input_ver = raw_input('Please provide build number: ')

            if semver.from_string(input_ver) <= previous_build_number:
                raise semver.SemanticVersionError('\tVersion cannot be less than: %s' % previous_build_number)

            return input_ver
        except semver.SemanticVersionError, e:
            print e

if __name__ == '__main__':
    try:
        root_dir = pwd().strip()
        working_dir = tempfile.mkdtemp()

        git.clone('%s %s' % (root_dir, working_dir))

        cd(working_dir)

        revision = retrieve_git_revision()

        previous_build_number = semver.from_string(retreive_build_number())
        print 'Previous build: ', previous_build_number
        new_build_number = '%d.%d.%d' % (
            previous_build_number.major, previous_build_number.minor, previous_build_number.patchlevel + 1)

        if raw_input('Use "%s" as next build number? (yes) ' % (new_build_number,)) != 'yes':
            new_build_number = prompt_for_new_build_number(previous_build_number)

        git_check_for_tag_collision(new_build_number)

        tower_consts = open(TOWER_CONSTS_JAVA).read()
        tower_consts = debug_flag_re.sub('boolean DEBUG = false;', tower_consts)
        tower_consts = server_https_re.sub('String HAPPYDROIDS_URI = "https://" + HAPPYDROIDS_SERVER;', tower_consts)
        tower_consts = server_url_re.sub('String HAPPYDROIDS_SERVER = "www.happydroids.com";', tower_consts)
        tower_consts = version_re.sub('String VERSION = "%s";' % (new_build_number,), tower_consts)
        tower_consts = git_sha_re.sub('String GIT_SHA = "%s";' % (revision,), tower_consts)

        android_version_code = int(open('build.code').read().strip()) + 1

        google_manifest = open(GOOGLE_MANIFEST_PATH).read().strip()
        google_manifest = android_manifest_re.sub('package="com.happydroids.droidtowers" android:versionCode="%s" android:versionName="%s">' %(android_version_code, new_build_number), google_manifest)

        amazon_manifest = open(AMAZON_MANIFEST_PATH).read().strip()
        amazon_manifest = android_manifest_re.sub('package="com.happydroids.droidtowers" android:versionCode="%s" android:versionName="%s">' %(android_version_code, new_build_number), amazon_manifest)

        print tower_consts

        if raw_input('Is this correct? [y/n]') == 'n':
            print "\n\nABORTED!\n\n"
            sys.exit(1)

        print google_manifest

        if raw_input('Is this correct? [y/n]') == 'n':
            print "\n\nABORTED!\n\n"
            sys.exit(1)

        print amazon_manifest

        if raw_input('Is this correct? [y/n]') == 'n':
            print "\n\nABORTED!\n\n"
            sys.exit(1)

        with open(TOWER_CONSTS_JAVA, 'w+') as fp:
            fp.write(tower_consts)

        with open(GOOGLE_MANIFEST_PATH, 'w+') as fp:
            fp.write(google_manifest)

        with open(AMAZON_MANIFEST_PATH, 'w+') as fp:
            fp.write(amazon_manifest)

        with open('build.ver', 'w') as fp:
            fp.write(new_build_number)

        with open('build.code', 'w') as fp:
            fp.write('%s' % android_version_code)        

        with open('release.properties', 'w') as fp:
            fp.write('project.version=%s' % (new_build_number))

        ant('release', _fg=True)

        cd('%s/android-google' % (root_dir,))

        ant('release', _fg=True)

        cd('%s/android-amazon' % (root_dir,))

        ant('release', _fg=True)

        cd(root_dir)

        git.commit(a=True, m='Artifacts from release-%s' % (new_build_number,))
        git.tag('release-%s' % (new_build_number,))
        git.push('origin release-%s' % (new_build_number,))

        tower_consts = open(TOWER_CONSTS_JAVA).read()
        tower_consts = debug_flag_re.sub('boolean DEBUG = true;', tower_consts)
        tower_consts = server_url_re.sub('String HAPPYDROIDS_SERVER = "local.happydroids.com";', tower_consts)
        tower_consts = server_https_re.sub('String HAPPYDROIDS_URI = "http://" + HAPPYDROIDS_SERVER;', tower_consts)

        with open(TOWER_CONSTS_JAVA, 'w') as fp:
            fp.write(tower_consts)

    except Exception, e:
        print e
        sys.exit(1)


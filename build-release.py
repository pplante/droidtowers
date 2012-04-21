#! /usr/bin/env python

import re
import sys
import semver
import requests
import json
from datetime import datetime
from getpass import getpass
from pbs import git, ant, scp, pwd

SCP_TARGET_PATH = 'pplante@happydroids.com:/var/www/happydroids.com/public/alphas'
TOWER_CONSTS_JAVA = './happydroids-common/src/com/happydroids/HappyDroidConsts.java'

debug_flag_re = re.compile(r'(public static final boolean DEBUG = (?:true|false);)')
server_url_re = re.compile(r'(public static final String HAPPYDROIDS_SERVER = "(?:.+?)";)')
server_https_re = re.compile(r'(public static final String HAPPYDROIDS_URI = "(?:.+?)" \+ HAPPYDROIDS_SERVER;)')
version_re = re.compile(r'(public static String VERSION = "(?:.+?)";)')
git_sha_re = re.compile(r'(public static String GIT_SHA = "(?:.+?)";)')

def retrieve_git_revision():
    git_status = git.status('--porcelain').strip()
    if len(git_status) > 0:
        raise Exception("Git status reports there are changes to be committed.")

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
        revision = retrieve_git_revision()
        previous_build_number = semver.from_string(retreive_build_number())
        print 'Previous build: ', previous_build_number
        new_build_number = 'v%d.%d.%d' % (
            previous_build_number.major, previous_build_number.minor, previous_build_number.patchlevel + 1)

        if raw_input('Use "%s" as next build number? (yes) ' % (new_build_number,)) != 'yes':
            new_build_number = prompt_for_new_build_number(previous_build_number)

        git_check_for_tag_collision(new_build_number)

        tower_consts = open(TOWER_CONSTS_JAVA).read()
        tower_consts = debug_flag_re.sub('public static final boolean DEBUG = false;', tower_consts)
        tower_consts = server_https_re.sub(
            'public static final String HAPPYDROIDS_URI = "https://" + HAPPYDROIDS_SERVER;',
            tower_consts)
        tower_consts = server_url_re.sub('public static final String HAPPYDROIDS_SERVER = "www.happydroids.com";',
            tower_consts)
        tower_consts = version_re.sub('public static String VERSION = "%s";' % (new_build_number,), tower_consts)
        tower_consts = git_sha_re.sub('public static String GIT_SHA = "%s";' % (revision,), tower_consts)

        print tower_consts

        with open(TOWER_CONSTS_JAVA, 'w+') as fp:
            fp.write(tower_consts)

        with open('build.ver', 'w') as fp:
            fp.write(new_build_number)

        with open('release.properties', 'w') as fp:
            fp.write('project.version=%s' % (new_build_number))

        git.commit(a=True, m='Artifacts from release-%s' % (new_build_number,))
        git.tag('release-%s' % (new_build_number,))

        ant('release', _fg=True)

        upload = scp.bake(i='/Users/pplante/.ssh/id_rsa', _fg=True)

        upload('./out/DroidTowers.exe', '%s/DroidTowers.exe' % (SCP_TARGET_PATH,))
        upload('./out/DroidTowers.zip', '%s/DroidTowers.zip' % (SCP_TARGET_PATH,))

        print "http://www.happydroids.com/alphas/DroidTowers.exe"
        print "http://www.happydroids.com/alphas/DroidTowers.zip"

        tower_consts = open(TOWER_CONSTS_JAVA).read()
        tower_consts = debug_flag_re.sub('public static final boolean DEBUG = true;', tower_consts)
        tower_consts = server_url_re.sub('public static final String HAPPYDROIDS_SERVER = "local.happydroids.com";',
            tower_consts)
        tower_consts = server_https_re.sub(
            'public static final String HAPPYDROIDS_URI = "http://" + HAPPYDROIDS_SERVER;',
            tower_consts)
        with open(TOWER_CONSTS_JAVA, 'w') as fp:
            fp.write(tower_consts)

        #        notes = unicode(git.log('--no-decorate', '--pretty=format:[%h]  %s', '--no-merges',
        #            'release-v%s..' % (previous_build_number,)))
        #
        #        blob = json.dumps(dict(
        #            version=new_build_number,
        #            git_sha=revision,
        #            released_on=datetime.now().isoformat(),
        #            notes=notes
        #        ))
        #
        #        headers = {'content-type': 'application/json'}
        #
        #        r = requests.post('https://www.happydroids.com/api/v1/gameupdate/?format=json',
        #            auth=('pplante', getpass('Please enter password for game update: ')), data=blob, headers=headers)
        #
        #        if r.status_code == 201:
        #            print 'Game update successfully posted!'
        #            print r.headers['location']
        #        else:
        #            print 'Failure posting game update:'
        #            print r.status_code
        #            print r.text
        #

    except Exception, e:
        print e
        sys.exit(1)


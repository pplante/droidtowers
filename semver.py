#!/usr/bin/python
# -*- coding: utf-8 -*-

# semver.py --- short description
#
# Copyright  (C)  2010  Martin Marcher <martin@marcher.name>
#
# Version: 0.0.0
# Keywords:
# Author: Martin Marcher <martin@marcher.name>
# Maintainer: Martin Marcher <martin@marcher.name>
# URL: http://
#
# Permission is hereby granted, free of charge, to any person
# obtaining a copy of this software and associated documentation files
# (the "Software"), to deal in the Software without restriction,
# including without limitation the rights to use, copy, modify, merge,
# publish, distribute, sublicense, and/or sell copies of the Software,
# and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
# BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
# ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#
# Commentary:
"""
============================================
 Semantic Versioning Specification (SemVer)
============================================

The key words **"MUST"**, **"MUST NOT"**, **"REQUIRED"**, **"SHALL"**,
**"SHALL NOT"**, **"SHOULD"**, **"SHOULD NOT"**, **"RECOMMENDED"**,
**"MAY"**, and **"OPTIONAL"** in this document are to be interpreted
as described in RFC2119.

(1) Software using Semantic Versioning **MUST** declare a public
    API. This API could be declared in the code itself or exist
    strictly in documentation.  However it is done, it should be
    precise and comprehensive.

(#) A normal version number **MUST** take the form ``X.Y.Z`` where
    ``X``, ``Y``, and ``Z`` are integers. ``X`` is the *major
    version*, ``Y`` is the *minor version*, and ``Z`` is the *patch
    version*. Each element **MUST** increase numerically. For
    instance: ``1.9.0 < 1.10.0 < 1.11.0``.

(#) A special version number **MAY** be denoted by appending an
    arbitrary string immediately following the patch version. The
    string **MUST** be comprised of only alphanumerics
    (``[0-9A-Za-z]``) and **MUST** begin with a lower case alpha
    character (``[a-z]``). Special versions satisfy but have a lower
    precedence than the associated normal version. Precedence
    **SHOULD** be determined by *lexicographic ASCII sort order*. For
    instance: ``1.0.0beta1 < 1.0.0beta2 < 1.0.0``.

(#) Once a versioned package has been released, the contents of that
    version **MUST NOT** be modified. Any modifications must be
    released as a new version.

(#) *Major version zero* (``0.y.z``) is for initial
    development. Anything may change at any time. The public API
    should not be considered stable.

(#) *Version* ``1.0.0`` defines the public API. The way in which the
    version number is incremented is now dependent on this public API
    and how it changes.

(#) *Patch version* ``Z`` (``x.y.Z | x > 0``) **MUST** be incremented
    if only backwards compatible bug fixes are introduced. A bug fix
    is defined as an internal change that fixes incorrect behavior.

(#) *Minor version* ``Y`` (``x.Y.z | x > 0``) MUST be incremented if
    new, backwards compatible functionality is introduced to the
    public API. It **MAY** be incremented if substantial new
    functionality or improvements are introduced within the private
    code. It **MAY** include patch level changes.

(#) *Major version* ``X`` (``X.y.z | X > 0``) **MUST** be incremented
    if any backwards incompatible changes are introduced to the public
    API. It **MAY** include minor and patch level changes.

===================================
 Tagging Specification (SemVerTag)
===================================

This sub-specification **SHOULD** be used if you use a version control
system (Git, Mercurial, SVN, etc) to store your code. Using this
system allows automated tools to inspect your package and determine
SemVer compliance and released versions.

When tagging releases in a version control system, the tag for a
version MUST be "``vX.Y.Z``" e.g. "``v3.1.0``".

The first revision that introduces SemVer compliance **SHOULD** be
tagged "``semver``". This allows pre-existing projects to assume
compliance at any arbitrary point and for automated tools to discover
this fact.

"""

import os
import sys
import logging

import re


class SemanticVersionError(Exception):
    """Base error for SemanticVersion instances"""
    pass


class SemanticVersion(object):
    _SPECIAL_PATTERN = r"(?P<special>[a-z]\w+[\d+])?$"
    PATTERN = r"^v?(?P<major>\d+)\."\
              r"(?P<minor>\d+)\."\
              r"(?P<patchlevel>\d+)~?"\
              + _SPECIAL_PATTERN

    def __init__(self, major, minor, patchlevel, special=None):
        self.major = int(major)
        self.minor = int(minor)
        self.patchlevel = int(patchlevel)
        self.special = special

    def __unicode__(self):
        return unicode(str(self))

    def __str__(self):
        if self.special:
            return "%s.%s.%s~%s" % (self.major,
                                    self.minor,
                                    self.patchlevel,
                                    self.special)
        else:
            return "%s.%s.%s" % (self.major,
                                    self.minor,
                                    self.patchlevel)

    def __nonzero__(self):
        return (self.major or self.minor or self.patchlevel or self.special)

    def __repr__(self):
        return "<%s (%s)>" % (SemanticVersion.__name__, str(self))

    def __eq__(self, other):
        """Compares wether self and other are equal.
        """
        try:
            return (self.major, self.minor, self.patchlevel,
                    self.special) == (other.major, other.minor,
                                      other.patchlevel, other.special)
        except (AttributeError, ), error:
            return False

    def __ne__(self, other):
        """Compares wether self and other are not equal.
        """
        return not self == other

    def __le__(self, other):
        """Compares wether self is less or equal than other.
        """
        if self == other:
            return True
        if (self.major, self.minor, self.patchlevel) <= (other.major, other.minor, other.patchlevel):
            # existing special version number makes self lower than
            # non existing special version number
            if self.special is None and other.special is not None:
                return not (self.special < other.special)
            else:
                return self.special <= other.special
        return False

    def __ge__(self, other):
        """Compares wether self is greater or equal than other.
        """
        if self == other:
            return True
        return not self <= other

    def __lt__(self, other):
        """Compares wether self is less than other.
        """
        if self == other:
            return False
        if (self.major, self.minor, self.patchlevel) < (other.major, other.minor, other.patchlevel):
            # existing special version number makes self lower than
            # non existing special version number
            if self.special is not None and other.special is None:
                return not (self.special < other.special)
            else:
                return self.special <= other.special
        else:
            return False

    def __gt__(self, other):
        """Compares wether self is greater than other.
        """
        if self == other:
            return False
        return not self < other


def from_string(value):

    pattern = re.compile(SemanticVersion.PATTERN)
    try:
        result = pattern.match(value).groupdict()
        return SemanticVersion(**result)
    except (AttributeError, ), error:
        raise SemanticVersionError("ERROR: A version must either look like `0.0.0`, `0.0.0~special0` or `v0.0.0~special0`")

__author__ = "Martin Marcher"
__version__ = from_string("0.0.0")

# Local Variables:
# mode:python
# comment-column:0
# End:

# vim: set ts=4 sts=4 expandtab encoding=utf-8:

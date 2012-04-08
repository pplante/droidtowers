/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.happydroids.TestHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.happydroids.sparky.Expect.expect;


public class JarJoinerTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  private File patchFile;
  private File existingFile;

  @Before
  public void setUp() throws IOException {
    temp.create();

    patchFile = temp.newFile("patch.jar");
    existingFile = temp.newFile("existing.jar");

    TestHelper.makeFakeZip(patchFile);
    TestHelper.makeFakeZip(existingFile);
  }

  @Test
  public void join_shouldMergeJarsKeepingNewerVersions() throws IOException {
    File resultFile = temp.newFile("result.jar");

    JarJoiner joiner = new JarJoiner(resultFile);
    joiner.addFile(patchFile);
    joiner.addFile(existingFile);
    joiner.join("version:v0.10.50", null);

    JarFile resultZip = new JarFile(resultFile);
    JarFile existingZip = new JarFile(existingFile);
    JarFile patchZip = new JarFile(patchFile);

    expect(resultZip.getManifest().getMainAttributes().getValue("Game-Version")).toEqual("version:v0.10.50");

    Enumeration<JarEntry> existingEntries = existingZip.entries();
    Enumeration<JarEntry> patchEntries = patchZip.entries();
    Enumeration<JarEntry> resultEntries = resultZip.entries();

    while (existingEntries.hasMoreElements()) {
      JarEntry entry = existingEntries.nextElement();

      expect(resultZip.getInputStream(entry)).not.toEqual(existingZip.getInputStream(entry));
    }

    while (patchEntries.hasMoreElements()) {
      JarEntry entry = patchEntries.nextElement();

      expect(resultZip.getInputStream(entry)).toEqual(patchZip.getInputStream(entry));
    }
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.happydroids.droidtowers.unhappyrobot.Expect.expect;


public class JarJoinerTest {
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  private File patchFile;
  private File existingFile;

  @Before
  public void setUp() throws IOException {
    patchFile = temp.newFile("patch.jar");
    existingFile = temp.newFile("existing.jar");

    makeFakeZip(patchFile);
    makeFakeZip(existingFile);
  }

  @Test
  public void join_shouldMergeJarsKeepingNewerVersions() throws IOException {
    File resultFile = temp.newFile("result.jar");

    JarJoiner joiner = new JarJoiner(resultFile);
    joiner.addFile(patchFile);
    joiner.addFile(existingFile);
    joiner.join("version:v0.10.50");

    JarFile resultZip = new JarFile(resultFile);
    JarFile existingZip = new JarFile(existingFile);
    JarFile patchZip = new JarFile(patchFile);

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

  private void makeFakeZip(File zipFile) throws IOException {
    ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
    zipStream.putNextEntry(new ZipEntry("image.png"));
    byte[] bytes = ("Random bytes: " + new Random().nextInt()).getBytes();
    zipStream.write(bytes, 0, bytes.length);
    zipStream.closeEntry();
    zipStream.close();
  }
}

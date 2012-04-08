/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarJoiner {
  private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
  private final File outputFile;
  private Set<String> allEntries;
  private List<JarFile> jarsToProcess;
  private int numTotalEntries;
  private int numEntriesProcessed;
  private Runnable progressCallback;

  public JarJoiner(File outputFile) {
    this.outputFile = outputFile;
    jarsToProcess = Lists.newArrayList();
    allEntries = Sets.newHashSet(MANIFEST_NAME);
  }

  public void addFile(File file) throws IOException {
    JarFile jarFile = new JarFile(file);
    numTotalEntries += jarFile.size();
    jarsToProcess.add(jarFile);
  }

  public void join(String gameVersion, String gameVersionSHA) throws IOException {
    // Construct a string version of a manifest
    StringBuilder sbuf = new StringBuilder();
    sbuf.append("Manifest-Version: 1.0").append("\n");
    sbuf.append("Game-Version: ").append(gameVersion).append("\n");
    sbuf.append("Game-Version-SHA: ").append(gameVersionSHA).append("\n");
    sbuf.append("\n");

    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
    Manifest manifest = new Manifest(new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8")));
    JarOutputStream outputZip = new JarOutputStream(fileOutputStream, manifest);

    for (JarFile jarFile : jarsToProcess) {
      copyEntriesToFromZipFile(outputZip, jarFile);
    }
    outputZip.close();
    fileOutputStream.close();
  }

  private void copyEntriesToFromZipFile(JarOutputStream outputZip, JarFile existingZip) throws IOException {
    Enumeration<? extends JarEntry> entries = existingZip.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      if (!allEntries.contains(entry.getName())) {
        outputZip.putNextEntry(entry);
        IOUtils.copy(existingZip.getInputStream(entry), outputZip);
        outputZip.closeEntry();
        outputZip.flush();
        allEntries.add(entry.getName());

        numEntriesProcessed++;

        if (numEntriesProcessed % 25 == 0 && progressCallback != null) {
          progressCallback.run();
        }
      }
    }
  }

  public int getNumEntriesProcessed() {
    return numEntriesProcessed;
  }

  public int getNumTotalEntries() {
    return numTotalEntries;
  }

  public void setProgressCallback(Runnable progressCallback) {
    this.progressCallback = progressCallback;
  }

  public List<JarFile> getJars() {
    return jarsToProcess;
  }
}

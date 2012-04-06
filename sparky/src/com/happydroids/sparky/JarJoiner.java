/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarJoiner {
  private final File outputFile;
  private Set<String> allEntries;
  private List<JarFile> jarsToProcess;
  private int numTotalEntries;
  private int numEntriesProcessed;
  private Runnable progressCallback;

  public JarJoiner(File outputFile) {
    this.outputFile = outputFile;
    jarsToProcess = Lists.newArrayList();
    allEntries = Sets.newHashSet("META-INF/MANIFEST.MF");
  }

  public void addFile(File file) throws IOException {
    JarFile jarFile = new JarFile(file);
    numTotalEntries += jarFile.size();
    jarsToProcess.add(jarFile);
  }

  public void join(String gameVersion) throws IOException {
    // Construct a string version of a manifest
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("Manifest-Version: 1.0\n");
    sbuf.append("Game-Version: " + gameVersion + "\n");

    JarOutputStream outputZip = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)), new Manifest(new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8"))));

    for (JarFile jarFile : jarsToProcess) {
      copyEntriesToFromZipFile(outputZip, jarFile);
    }

    outputZip.close();
  }

  private void copyEntriesToFromZipFile(JarOutputStream outputZip, JarFile existingZip) throws IOException {
    Enumeration<? extends JarEntry> entries = existingZip.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      if (!allEntries.contains(entry.getName())) {
        outputZip.putNextEntry(entry);
        IOUtils.copy(existingZip.getInputStream(entry), outputZip);
        outputZip.closeEntry();
        allEntries.add(entry.getName());

        numEntriesProcessed++;

        if (numEntriesProcessed % 25 == 0) {
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
}

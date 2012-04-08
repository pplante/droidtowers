/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids;

import com.happydroids.server.TestHappyDroidService;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Random;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestHelper {
  public static void queueApiResponse(String fixtureFilename) throws IOException {
    String content = null;

    if (fixtureFilename != null) {
      content = FileUtils.readFileToString(new File("fixtures/", fixtureFilename));
    }

    ((TestHappyDroidService) TestHappyDroidService.instance()).queueResponse(content);
  }

  public static void makeFakeZip(File zipFile) throws IOException {
    ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
    zipStream.putNextEntry(new ZipEntry("image.png"));
    byte[] bytes = ("Random bytes: " + new Random().nextInt()).getBytes();
    zipStream.write(bytes, 0, bytes.length);
    zipStream.closeEntry();
    zipStream.close();
  }

  public static void makeFakeGameJar(File jarFile, String gameVersion, String versionSha) throws IOException {
    StringBuilder sbuf = new StringBuilder();
    sbuf.append("Manifest-Version: 1.0").append("\n");
    sbuf.append("Game-Version: ").append(gameVersion).append("\n");
    sbuf.append("Game-Version-SHA: ").append(versionSha).append("\n");


    JarOutputStream outputZip = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jarFile)), new Manifest(new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8"))));
    outputZip.putNextEntry(new ZipEntry("image.png"));
    byte[] bytes = ("Random bytes: " + new Random().nextInt()).getBytes();
    outputZip.write(bytes, 0, bytes.length);
    outputZip.closeEntry();
    outputZip.flush();
    outputZip.close();

    if (!jarFile.exists()) {
      throw new RuntimeException("Could not create fake game jar!");
    }
  }

  public static void disableNetworkConnection() {
  }
}

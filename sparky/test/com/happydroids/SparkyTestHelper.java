/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids;

import java.io.*;
import java.util.Random;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SparkyTestHelper extends TestHelper {
  public static void makeFakeZip(File zipFile) throws IOException {
    ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
    zipStream.putNextEntry(new ZipEntry("image.png"));
    byte[] bytes = ("Random bytes: " + new Random().nextInt()).getBytes();
    zipStream.write(bytes, 0, bytes.length);
    zipStream.closeEntry();
    zipStream.close();
  }

  public static void makeFakeGameJar(File jarFile, String gameVersion, String versionSha) throws IOException {
    makeFakeGameJar(new FileOutputStream(jarFile), gameVersion, versionSha);

    if (!jarFile.exists()) {
      throw new RuntimeException("Could not create fake game jar!");
    }
  }

  public static void makeFakeGameJar(OutputStream outputStream, String gameVersion, String versionSha) throws IOException {
    StringBuilder sbuf = new StringBuilder();
    sbuf.append("Manifest-Version: 1.0").append("\n");
    sbuf.append("Game-Version: ").append(gameVersion).append("\n");
    sbuf.append("Game-Version-SHA: ").append(versionSha).append("\n");


    JarOutputStream outputZip = new JarOutputStream(outputStream, new Manifest(new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8"))));
    byte[] bytes = ("Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                            "Cras fringilla ante diam, ut mollis magna. Vestibulum consectetur mattis leo, vel lobortis " +
                            "arcu volutpat nec. Morbi at neque non tortor dapibus gravida. Proin luctus est quis quam cursus " +
                            "egestas. Nullam id elit arcu. Donec nisl tellus, lacinia vel vulputate vel, pellentesque et tellus. " +
                            "Nulla nec ullamcorper nulla. Quisque lorem mi, pulvinar non iaculis at, accumsan nec ipsum. Nunc cursus " +
                            "tortor ut est ornare pulvinar. In et diam neque, ut fermentum sem. Suspendisse dui tellus, hendrerit sit " +
                            "amet mattis at, imperdiet vel purus. Phasellus malesuada rhoncus pretium. Sed metus enim, placerat quis pharetra id, " +
                            "posuere quis est. Sed blandit, purus quis volutpat lacinia, enim metus ultrices diam, vel convallis mi tortor sit amet leo. " +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.").getBytes();
    outputZip.putNextEntry(new ZipEntry("image.png"));
    outputZip.write(bytes);
    outputZip.closeEntry();
    outputZip.flush();
    outputZip.close();
  }

}

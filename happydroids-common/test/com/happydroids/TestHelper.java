/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class TestHelper {
  private static final String[] fixturePaths = new String[]{"sparky/fixtures/"};

  public static void queueApiResponseFromFixture(String uri, String fixtureFilename) throws IOException {
    File file = null;

    for (String fixturePath : fixturePaths) {
      file = new File(fixturePath, fixtureFilename);
      System.out.println(file.getAbsolutePath());
      if (file.exists()) {
        queueApiResponse(uri, FileUtils.readFileToString(file));
        return;
      }
    }

    throw new IOException("Could not find: " + fixtureFilename + " in search paths: " + Arrays.toString(fixturePaths));
  }

  public static void queueApiResponse(String uri, String content) throws IOException {
    queueApiResponse(uri, content.getBytes());
  }

  public static void queueApiResponse(String uri, byte[] bytes) throws IOException {
    Header[] headers = {new BasicHeader("Location", uri)};

    HttpTestHelper.instance().queueResponse(uri, bytes, headers);
  }

  public static void disableNetworkConnection() {
  }

  public static ArrayList<byte[]> getQueuedRequests() {
    ArrayList<byte[]> list = Lists.newArrayList();

    for (LinkedList<byte[]> linkedList : HttpTestHelper.instance().getResponseQueue().values()) {
      list.addAll(linkedList);
    }

    return list;
  }

  public static void clearQueuedRequests() {
    HttpTestHelper.instance().getResponseQueue().clear();
  }
}

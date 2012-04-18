/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids;

import com.fasterxml.jackson.databind.ObjectMapper;
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
  private static final String[] fixturePaths = new String[]{"fixtures/", "sparky/fixtures/", "main/fixtures/"};

  public static File fixture(String fixtureFilename) throws IOException {
    File file;

    for (String fixturePath : fixturePaths) {
      file = new File(fixturePath, fixtureFilename);
      if (file.exists()) {
        return file;
      }
    }

    throw new IOException("Could not find: " + fixtureFilename + " in search paths: " + Arrays.toString(fixturePaths));
  }

  public static void queueApiResponseFromFixture(String uri, String fixtureFilename) throws IOException {
    queueApiResponse(uri, FileUtils.readFileToString(fixture(fixtureFilename)));
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

  public static Object readJson(File file) throws IOException {
    return new ObjectMapper().readValue(file, Object.class);
  }

  public static Object readJson(String content) throws IOException {
    return new ObjectMapper().readValue(content, Object.class);
  }

  public static Object readJson(byte[] bytes) throws IOException {
    return new ObjectMapper().readValue(bytes, Object.class);
  }
}

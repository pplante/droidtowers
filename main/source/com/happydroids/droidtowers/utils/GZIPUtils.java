/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GZIPUtils {
  public static String compress(FileHandle pngFile) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
      gzipOutputStream.write(pngFile.readBytes());
      gzipOutputStream.close();
      return StringUtils.newStringUtf8(Base64.encodeBase64(byteArrayOutputStream.toByteArray(), true));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

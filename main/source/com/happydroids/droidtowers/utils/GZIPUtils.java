/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

import com.badlogic.gdx.files.FileHandle;
import org.apach3.commons.codec.binary.Base64;
import org.apach3.commons.codec.binary.StringUtils;
import org.apach3.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPUtils {
  public static String compress(FileHandle pngFile) {
    try {
      if (!pngFile.exists()) {
        return null;
      }

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
      gzipOutputStream.write(pngFile.readBytes());
      gzipOutputStream.close();
      return StringUtils.newStringUtf8(Base64.encodeBase64(byteArrayOutputStream.toByteArray(), true, true));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] decompress(String bytes) {
    try {
      GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(Base64.decodeBase64(bytes.getBytes())));
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      IOUtils.copy(gzipInputStream, outputStream);

      gzipInputStream.close();

      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

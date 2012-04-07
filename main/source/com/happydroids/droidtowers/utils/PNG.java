/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SuppressWarnings({"PointlessBitwiseExpression", "NumericOverflow"})
public class PNG {
  public static byte[] toPNG(Pixmap pixmap) throws IOException {
    byte[] signature = new byte[]{(byte) 137, (byte) 80, (byte) 78,
                                         (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};
    byte[] header = createHeaderChunk(pixmap.getWidth(), pixmap.getHeight());
    byte[] data = createDataChunk(pixmap);
    byte[] trailer = createTrailerChunk();

    ByteArrayOutputStream png = new ByteArrayOutputStream(signature.length
                                                                  + header.length + data.length + trailer.length);
    png.write(signature);
    png.write(header);
    png.write(data);
    png.write(trailer);
    pixmap.dispose();
    return png.toByteArray();
  }

  public static byte[] createHeaderChunk(int width, int height)
          throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(13);
    DataOutputStream chunk = new DataOutputStream(baos);
    chunk.writeInt(width);
    chunk.writeInt(height);
    chunk.writeByte(8); // Bitdepth
    chunk.writeByte(2); // Colortype ARGB
    chunk.writeByte(0); // Compression
    chunk.writeByte(0); // Filter
    chunk.writeByte(0); // Interlace
    return toChunk("IHDR", baos.toByteArray());
  }

  public static byte[] createDataChunk(Pixmap pixmap) throws IOException {
    int width = pixmap.getWidth();
    int height = pixmap.getHeight();
    int source = 0;
    int dest = 0;
    byte[] raw = new byte[3 * (width * height) + height];
    Color color8888 = new Color();
    for (int y = 0; y < height; y++) {
      raw[dest++] = 0; // No filter
      for (int x = 0; x < width; x++) {

        // 32-bit RGBA8888
        int pixel = pixmap.getPixel(x, y);
        Color.rgba8888ToColor(color8888, pixel);
        pixel = Color.rgb888(color8888);

        int mask = pixel & 0xFFFFFF;
        int rr = (mask >> 16) & 0xff;
        int gg = (mask >> 8) & 0xff;
        int bb = (mask) & 0xff;

        if (rr < 0 || rr > 255 || gg < 0 || gg > 255 || bb < 0
                    || bb > 255) {
          // break ! (assert doesn't always kick-in with the
          // Eclipse
          // debugger...)
          int divide_by_zero = 0 / 0;
        }

        raw[dest++] = (byte) rr;
        raw[dest++] = (byte) gg;
        raw[dest++] = (byte) bb;
        source++;
      }
    }
    return toChunk("IDAT", toZLIB(raw));
  }

  public static byte[] createTrailerChunk() throws IOException {
    return toChunk("IEND", new byte[]{});
  }

  public static byte[] toChunk(String id, byte[] raw) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 12);
    DataOutputStream chunk = new DataOutputStream(baos);

    chunk.writeInt(raw.length);

    byte[] bid = new byte[4];
    for (int i = 0; i < 4; i++) {
      bid[i] = (byte) id.charAt(i);
    }

    chunk.write(bid);

    chunk.write(raw);

    int crc = 0xFFFFFFFF;
    crc = updateCRC(crc, bid);
    crc = updateCRC(crc, raw);
    chunk.writeInt(~crc);

    return baos.toByteArray();
  }

  static int[] crcTable = null;

  public static void createCRCTable() {
    crcTable = new int[256];

    for (int i = 0; i < 256; i++) {
      int c = i;
      for (int k = 0; k < 8; k++) {
        c = ((c & 1) > 0) ? 0xedb88320 ^ (c >>> 1) : c >>> 1;
      }
      crcTable[i] = c;
    }
  }

  public static int updateCRC(int crc, byte[] raw) {
    if (crcTable == null) {
      createCRCTable();
    }

    for (byte aRaw : raw) {
      crc = crcTable[(crc ^ aRaw) & 0xFF] ^ (crc >>> 8);
    }

    return crc;
  }

  /*
  * This method is called to encode the image data as a zlib block as
  * required by the PNG specification. This file comes with a minimal ZLIB
  * encoder which uses uncompressed deflate blocks (fast, short, easy, but no
  * compression). If you want compression, call another encoder (such as
  * JZLib?) here.
  */
  public static byte[] toZLIB(byte[] raw) throws IOException {
    return ZLIB.toZLIB(raw);
  }
}

class ZLIB {
  static final int BLOCK_SIZE = 32000;

  public static byte[] toZLIB(byte[] raw) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 6
                                                                   + (raw.length / BLOCK_SIZE) * 5);
    DataOutputStream zlib = new DataOutputStream(baos);

    byte tmp = (byte) 8;
    zlib.writeByte(tmp); // CM = 8, CMINFO = 0
    zlib.writeByte((31 - ((tmp << 8) % 31)) % 31); // FCHECK
    // (FDICT/FLEVEL=0)

    int pos = 0;
    while (raw.length - pos > BLOCK_SIZE) {
      writeUncompressedDeflateBlock(zlib, false, raw, pos,
                                           (char) BLOCK_SIZE);
      pos += BLOCK_SIZE;
    }

    writeUncompressedDeflateBlock(zlib, true, raw, pos,
                                         (char) (raw.length - pos));

    // zlib check sum of uncompressed data
    zlib.writeInt(calcADLER32(raw));

    return baos.toByteArray();
  }

  private static void writeUncompressedDeflateBlock(DataOutputStream zlib,
                                                    boolean last, byte[] raw, int off, char len) throws IOException {
    zlib.writeByte((byte) (last ? 1 : 0)); // Final flag, Compression type 0
    zlib.writeByte((byte) (len & 0xFF)); // Length LSB
    zlib.writeByte((byte) ((len & 0xFF00) >> 8)); // Length MSB
    zlib.writeByte((byte) (~len & 0xFF)); // Length 1st complement LSB
    zlib.writeByte((byte) ((~len & 0xFF00) >> 8)); // Length 1st complement
    // MSB
    zlib.write(raw, off, len); // Data
  }

  private static int calcADLER32(byte[] raw) {
    int s1 = 1;
    int s2 = 0;
    for (byte aRaw : raw) {
      int abs = aRaw >= 0 ? aRaw : (aRaw + 256);
      s1 = (s1 + abs) % 65521;
      s2 = (s2 + s1) % 65521;
    }
    return (s2 << 16) + s1;
  }
}

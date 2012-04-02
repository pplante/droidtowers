package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.unhappyrobot.TowerConsts;
import com.unhappyrobot.gamestate.GameSave;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CloudGameSave extends HappyDroidServiceObject {
  private String blob;
  private String image;
  private Date syncedOn;

  @Override
  public String getBaseResourceUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/gamesave/";
  }

  public CloudGameSave() {

  }

  @Override
  protected boolean requireAuthentication() {
    return true;
  }

  public CloudGameSave(GameSave gameSave, FileHandle pngFile) {
    try {
      resourceUri = gameSave.getCloudSaveUri();
      blob = getObjectMapper().writeValueAsString(gameSave);
      image = GZIPImage.compress(pngFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class GZIPImage {
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
}

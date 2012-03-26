package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.files.FileHandle;
import com.unhappyrobot.gamestate.GameSave;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CloudGameSave extends HappyDroidServiceObject {
  private String blob;
  private GZIPImage image;
  private Date syncedOn;

  @Override
  protected String getResourceBaseUri() {
    return Consts.HAPPYDROIDS_URI + "/api/v1/gamesave/";
  }

  public CloudGameSave() {

  }

  public CloudGameSave(GameSave gameSave, FileHandle pngFile) {
    try {
      resourceUri = gameSave.getCloudSaveUri();
      blob = GameSave.getObjectMapper().writeValueAsString(gameSave);
      image = new GZIPImage(pngFile);
      System.out.println("image = " + image);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class GZIPImage {
    public String name;
    public String file;

    public GZIPImage(FileHandle pngFile) {
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gzipOutputStream.write(pngFile.readBytes());
        gzipOutputStream.close();
        name = pngFile.name();
        file = Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

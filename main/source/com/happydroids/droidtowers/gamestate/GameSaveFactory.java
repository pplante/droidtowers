/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.files.FileHandle;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_MoveMetadata;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_RemoveObjectCounts;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_UnhappyrobotToDroidTowers;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import sk.seges.acris.json.server.migrate.JacksonTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class GameSaveFactory {
  public static String generateFilename() {
    return UUID.randomUUID().toString().replaceAll("-", "") + ".json";
  }

  public static GameSave readFile(FileHandle fileHandle) throws Exception {
    return readFile(fileHandle.read(), fileHandle.name());
  }

  public static GameSave readFile(InputStream read, String fileName) {
    try {
      JacksonTransformer transformer = new JacksonTransformer(read, fileName);
      transformer.addTransform(Migration_GameSave_UnhappyrobotToDroidTowers.class);
      transformer.addTransform(Migration_GameSave_RemoveObjectCounts.class);
      transformer.addTransform(Migration_GameSave_MoveMetadata.class);

      byte[] bytes = transformer.process();

      return TowerGameService.instance().getObjectMapper().readValue(bytes, GameSave.class);
    } catch (Exception e) {
      throw new RuntimeException("There was a problem parsing: " + fileName, e);
    }
  }

  @SuppressWarnings("PointlessBooleanExpression")
  public static void save(GameSave gameSave, FileHandle gameFile) throws IOException {
    if (TowerConsts.DEBUG && gameSave.isSaveToDiskDisabled()) return;

    OutputStream stream = gameFile.write(false);
    TowerGameService.instance().getObjectMapper().writeValue(stream, gameSave);
    stream.flush();
    stream.close();
  }
}

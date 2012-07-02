/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_MoveMetadata;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_RemoveObjectCounts;
import com.happydroids.droidtowers.gamestate.migrations.Migration_GameSave_UnhappyrobotToDroidTowers;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.jackson.HappyDroidObjectMapper;
import com.happydroids.security.AESObfuscator;
import sk.seges.acris.json.server.migrate.JacksonTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
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
      AESObfuscator obfuscator = new AESObfuscator(HappyDroidConsts.OBFUSCATION_SALT, HappyDroidConsts.OBFUSCATION_KEY);
      JacksonTransformer transformer = new JacksonTransformer(read, fileName);
      transformer.addTransform(Migration_GameSave_UnhappyrobotToDroidTowers.class);
      transformer.addTransform(Migration_GameSave_RemoveObjectCounts.class);
      transformer.addTransform(Migration_GameSave_MoveMetadata.class);

      return TowerGameService.instance().getObjectMapper().readValue(transformer.process(), GameSave.class);
    } catch (Exception e) {
      throw new RuntimeException("There was a problem parsing: " + fileName, e);
    }
  }

  @SuppressWarnings("PointlessBooleanExpression")
  public static void save(GameSave gameSave, FileHandle gameFile) throws IOException {
    if (TowerConsts.DEBUG && gameSave.isSaveToDiskDisabled()) return;
    gameSave.getMetadata().lastPlayed = new Date();
    OutputStream stream = gameFile.write(false);
    TowerGameService.instance().getObjectMapper().writeValue(stream, gameSave);
    stream.flush();
    stream.close();
  }

  public static GameSave readMetadata(InputStream read) {
    try {
      JacksonTransformer transformer = new JacksonTransformer(read, null);
      transformer.addTransform(Migration_GameSave_UnhappyrobotToDroidTowers.class);
      transformer.addTransform(Migration_GameSave_RemoveObjectCounts.class);
      transformer.addTransform(Migration_GameSave_MoveMetadata.class);

      HappyDroidObjectMapper objectMapper = TowerGameService.instance().getObjectMapper();
      return objectMapper.reader(GameSave.class).withView(GameSave.Views.Metadata.class).readValue(transformer.process());
    } catch (Exception e) {
      throw new RuntimeException("There was a problem parsing gamesave metadata.", e);
    }
  }

  public static FileHandle getStorageRoot() {
    return Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
  }
}

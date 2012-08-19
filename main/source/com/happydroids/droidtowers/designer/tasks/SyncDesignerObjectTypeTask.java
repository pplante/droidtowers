/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.tasks;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.collect.Lists;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.designer.server.DesignerObjectTypeCollection;
import com.happydroids.droidtowers.designer.types.DesignerObjectType;
import com.happydroids.droidtowers.designer.types.DesignerObjectTypeFactory;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.utils.FileUtils;
import com.happydroids.utils.BackgroundTask;

import java.util.List;

public class SyncDesignerObjectTypeTask extends BackgroundTask {
  private PixmapPacker packer;
  private TextureAtlas textureAtlas;
  private DesignerObjectTypeCollection objectTypes;

  public SyncDesignerObjectTypeTask(TextureAtlas textureAtlas) {
    this.textureAtlas = textureAtlas;
  }

  @Override public synchronized void beforeExecute() {
    objectTypes = new DesignerObjectTypeCollection();
    packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 0, false);
  }

  @Override protected void execute() throws Exception {
    objectTypes.fetch();

    FileHandle downloadDir = GameSaveFactory.getStorageRoot().child("download/designer");
    if (!downloadDir.exists()) {
      downloadDir.mkdirs();
    }

    List<DesignerObjectType> downloadQueue = Lists.newArrayList();
    for (DesignerObjectType type : objectTypes.getObjects()) {
      FileHandle imageFile = downloadDir.child(type.getImageHash());
      if (!imageFile.exists()) {
        downloadQueue.add(type);
      }
    }

    for (DesignerObjectType type : downloadQueue) {
      FileHandle imageFile = downloadDir.child(type.getImageHash());
      FileUtils.downloadAndCacheFile(type.getImageUrl(), HappyDroidConsts.ONE_YEAR, imageFile);
    }

    for (DesignerObjectType type : objectTypes.getObjects()) {
      FileHandle imageFile = downloadDir.child(type.getImageHash());
      packer.pack(type.getTypeId(), new Pixmap(imageFile));
    }
  }

  @Override public synchronized void afterExecute() {
    for (DesignerObjectType type : objectTypes.getObjects()) {
      DesignerObjectTypeFactory.instance().add(type);
    }


    packer.updateTextureAtlas(textureAtlas, Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
    packer.dispose();
    packer = null;
    objectTypes = null;
  }

  public TextureAtlas getTextureAtlas() {
    return textureAtlas;
  }
}

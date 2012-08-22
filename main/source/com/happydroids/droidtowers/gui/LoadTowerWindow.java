/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.GameSaveFactory;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.LoadTowerSplashScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.tasks.WaitForCloudSyncTask;
import org.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;
import java.util.Set;

import static java.text.NumberFormat.getNumberInstance;

public class LoadTowerWindow extends ScrollableTowerWindow {
  private static final String TAG = LoadTowerWindow.class.getSimpleName();

  private boolean foundSaveFile;
  private final Dialog progressDialog;
  private final WaitForCloudSyncTask waitForCloudSyncTask;
  private Set<Texture> towerImageTextures;


  public LoadTowerWindow(Stage stage) {
    super("Load a Tower", stage);
    towerImageTextures = Sets.newHashSet();
    progressDialog = new ProgressDialog()
                             .setMessage("looking for towers")
                             .hideButtons(true);

    waitForCloudSyncTask = new WaitForCloudSyncTask(this);
    waitForCloudSyncTask.run();

    setDismissCallback(new Runnable() {
      @Override
      public void run() {
        progressDialog.dismiss();
        waitForCloudSyncTask.cancel();
      }
    });
  }

  public void buildGameSaveList() {
    FileHandle storage = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
    FileHandle[] files = storage.list(".json");

    if (files != null && files.length > 0) {
      for (FileHandle file : files) {
        Table fileRow = makeGameFileRow(file);
        if (fileRow != null) {
          row().fillX();
          add(fileRow).expandX();
          foundSaveFile = true;
        }
      }
    }

    if (!foundSaveFile) {
      add(FontManager.RobotoBold18.makeLabel("No saved games were found on this device."));
    } else {
      shoveContentUp();
    }

    progressDialog.dismiss();
  }

  private Table makeGameFileRow(final FileHandle gameSaveFile) {
    GameSave towerData;
    try {
      towerData = GameSaveFactory.readMetadata(gameSaveFile.read());
    } catch (Exception e) {
      Gdx.app.log(TAG, "Failed to parse file.", e);
      return null;
    }

    FileHandle imageFile = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY + gameSaveFile.name() + ".png");

    Actor imageActor = null;
    if (imageFile.exists()) {
      try {
        imageActor = new Image(loadTowerImage(imageFile), Scaling.fit, Align.top);
      } catch (Exception ignored) {
        imageActor = null;
      }
    }

    if (imageActor == null) {
      imageActor = FontManager.Default.makeLabel("No image.");
    }

    Table fileRow = new Table();
    fileRow.defaults().fillX().pad(Display.devicePixel(10)).space(Display.devicePixel(10));
    fileRow.row();
    fileRow.add(imageActor).width(Display.devicePixel(64)).height(Display.devicePixel(64)).center();
    fileRow.add(makeGameFileInfoBox(fileRow, gameSaveFile, towerData)).expandX().top();
    fileRow.row().fillX();
    fileRow.add(new HorizontalRule(Color.DARK_GRAY, 2)).colspan(2);

    return fileRow;
  }

  private TextureRegionDrawable loadTowerImage(FileHandle imageFile) {
    Texture texture = new Texture(imageFile);
    towerImageTextures.add(texture);
    return new TextureRegionDrawable(new TextureRegion(texture));
  }

  private Table makeGameFileInfoBox(final Table fileRow, final FileHandle savedGameFile, GameSave towerData) {
    TextButton launchButton = FontManager.RobotoBold18.makeTextButton("Play");
    launchButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
        try {
          SceneManager.changeScene(LoadTowerSplashScene.class, GameSaveFactory.readFile(savedGameFile));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });

    TextButton deleteButton = FontManager.RobotoBold18.makeTextButton("Delete");
    deleteButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new Dialog().setTitle("Are you sure you want to delete this Tower?")
                .setMessage("If you delete this tower, it will disappear forever.\n\nAre you sure?")
                .addButton("Yes, delete it", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    savedGameFile.delete();

                    content.getCell(fileRow).ignore();
                    content.removeActor(fileRow);
                    content.invalidate();

                    dialog.dismiss();
                  }
                })
                .addButton("Keep it!", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.dismiss();
                  }
                })
                .show();
      }
    });

    Table metadata = new Table();
    metadata.defaults().top().left().fillX();
    addLabelRow(metadata, towerData.getTowerName(), FontManager.RobotoBold18, Color.WHITE);
    addLabelRow(metadata, "Population: " + getNumberInstance().format(towerData.getPlayer()
                                                                              .getTotalPopulation()), FontManager.Default, Color.GRAY);
    Date lastPlayed = towerData.getMetadata().lastPlayed;
    if (lastPlayed != null) {
      PrettyTime prettyTime = new PrettyTime();
      addLabelRow(metadata, "Last played: " + prettyTime.format(lastPlayed), FontManager.Default, Color.GRAY);
    }

    Table box = new Table();
    box.defaults().fillX().space(Display.devicePixel(5));
    box.row().top().left().fillX();
    box.add(metadata).top().left().expandX();
    box.add(deleteButton).width(Display.devicePixel(80));
    box.add(launchButton).width(Display.devicePixel(80));

    return box;
  }

  private void addLabelRow(Table table, String content, FontHelper font, Color fontColor) {
    table.row().fillX();
    table.add(font.makeLabel(content, fontColor)).expandX();
  }

  @Override
  public TowerWindow show() {
    super.show();

    progressDialog.show();
    progressDialog.clearActions();
    progressDialog.getColor().a = 1f;

    return this;
  }

  @Override
  public void dismiss() {
    super.dismiss();

    for (Texture texture : towerImageTextures) {
      try {
        texture.dispose();
      } catch (Exception ignored) {

      }
    }

    towerImageTextures.clear();
  }
}

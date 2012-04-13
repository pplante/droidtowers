/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.scenes.TowerScene;

public class LoadGameWindow extends TowerWindow {
  private boolean foundSaveFile;

  public LoadGameWindow(Stage stage, Skin skin) {
    super("Load a saved game", stage, skin);

    defaults().top().left().pad(5);

    FileHandle storage = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
    FileHandle[] files = storage.list();


    Table gameFiles = new Table();
    gameFiles.defaults();

    if (files != null && files.length > 0) {
      for (FileHandle file : files) {
        if (file.name().endsWith(".json")) {
          Table fileRow = makeGameFileRow(file);
          if (fileRow != null) {
            gameFiles.row();
            gameFiles.add(fileRow);
            foundSaveFile = true;
          }
        }
      }
    }

    if(!foundSaveFile) {
      gameFiles.add(LabelStyle.Default.makeLabel("No saved games were found on this device."));
    }

    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setWidget(gameFiles);
    add(scrollPane).maxWidth(500).maxHeight(300).minWidth(400);
  }

  private Table makeGameFileRow(final FileHandle gameSave) {
    String towerName = "Unnamed Tower";
    try {
      towerName = GameSave.readFile(gameSave).getTowerName();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    FileHandle imageFile = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY + gameSave.name() + ".png");


    Actor imageActor;
    if (imageFile.exists()) {
      imageActor = new Image(new Texture(imageFile), Scaling.fit, Align.TOP);
    } else {
      imageActor = LabelStyle.Default.makeLabel("No image.");
    }


    Table fileRow = new Table();
    fileRow.defaults().pad(5).width(360);
    fileRow.row().top().left();
    fileRow.add(imageActor).top().left().width(100);
    fileRow.add(makeGameFileInfoBox(gameSave, towerName)).top().left().fill();

    return fileRow;
  }

  private Table makeGameFileInfoBox(final FileHandle savedGameFile, String towerName) {
    TextButton launchButton = new TextButton("Play", skin);
    launchButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
        try {
          TowerGame.changeScene(TowerScene.class, GameSave.readFile(savedGameFile));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });

    Table box = new Table();
    box.defaults().top().left().expand();
    box.row();
    box.add(LabelStyle.Default.makeLabel(towerName)).top().left();
    box.add(launchButton).top().right();

    return box;
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.scenes.TowerScene;

import java.text.NumberFormat;

public class LoadGameWindow extends TowerWindow {
  private boolean foundSaveFile;
  private Pixmap pixmap;
  private NinePatch background;

  public LoadGameWindow(Stage stage, Skin skin) {
    super("Load a saved game", stage, skin);

    if (pixmap == null) {
      pixmap = new Pixmap(2, 2, Pixmap.Format.RGB888);
      pixmap.setColor(Color.BLACK);
      pixmap.fill();
      pixmap.setColor(new Color(0.075f, 0.075f, 0.075f, 1f));
      pixmap.drawPixel(0, 0);
      pixmap.drawPixel(1, 0);

      Texture texture = new Texture(pixmap);
      texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
      texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
      background = new NinePatch(texture);
    }

    defaults().top().left().pad(0).space(0);

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

    if (!foundSaveFile) {
      gameFiles.add(LabelStyle.Default.makeLabel("No saved games were found on this device."));
    }

    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setWidget(gameFiles);
    add(scrollPane).height(380).width(700);
  }

  private Table makeGameFileRow(final FileHandle gameSave) {
    GameSave towerData = null;
    try {
      towerData = GameSave.readFile(gameSave);
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
    fileRow.setBackground(background);
    fileRow.defaults().width(560);
    fileRow.row().top().left();
    fileRow.add(imageActor).top().left().width(100).pad(10);
    fileRow.add(makeGameFileInfoBox(gameSave, towerData)).top().left().pad(10).fill();

    return fileRow;
  }

  private Table makeGameFileInfoBox(final FileHandle savedGameFile, GameSave towerData) {
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
    box.add(LabelStyle.Default.makeLabel(towerData.getTowerName())).top().left();
    box.add(launchButton).top().right();

    box.row();
    String population = NumberFormat.getNumberInstance().format(towerData.getPlayer().getTotalPopulation());
    box.add(LabelStyle.Default.makeLabel(String.format("Population: %s", population))).top().left();

    return box;
  }
}

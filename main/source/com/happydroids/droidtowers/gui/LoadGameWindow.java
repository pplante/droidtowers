/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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

import java.text.NumberFormat;

import static com.happydroids.droidtowers.platform.Display.scale;

public class LoadGameWindow extends TowerWindowTwo {
  private boolean foundSaveFile;
  private final Table gameFiles;

  public LoadGameWindow(Stage stage, Skin skin) {
    super("Load a Tower", stage, skin);

    FileHandle storage = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
    FileHandle[] files = storage.list();

    gameFiles = new Table();
    gameFiles.clear();
    gameFiles.defaults();

    if (files != null && files.length > 0) {
      for (FileHandle file : files) {
        if (file.name().endsWith(".json")) {
          Table fileRow = makeGameFileRow(file);
          if (fileRow != null) {
            gameFiles.row().fill();
            gameFiles.add(fileRow).expandX();
            gameFiles.row().fillX();
            gameFiles.add(new HorizontalRule(Color.DARK_GRAY, 2));
            foundSaveFile = true;
          }
        }
      }
    }

    if (!foundSaveFile) {
      gameFiles.add(FontManager.RobotoBold18.makeLabel("No saved games were found on this device."));
    }

    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setWidget(gameFiles);
    add(scrollPane).fill();
  }

  private Table makeGameFileRow(final FileHandle gameSave) {
    GameSave towerData;
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
      imageActor = FontManager.Default.makeLabel("No image.");
    }


    Table fileRow = new Table();
    fileRow.defaults().fill().pad(scale(10)).space(scale(10));
    fileRow.row();
    fileRow.add(imageActor).width(scale(100)).right();
    fileRow.add(makeGameFileInfoBox(fileRow, gameSave, towerData)).expandX().top();

    return fileRow;
  }

  private Table makeGameFileInfoBox(final Table fileRow, final FileHandle savedGameFile, GameSave towerData) {
    TextButton launchButton = FontManager.RobotoBold18.makeTextButton("Play", skin);
    launchButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
//        dismiss();
        try {
          TowerGame.changeScene(TowerScene.class, GameSave.readFile(savedGameFile));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });

    TextButton deleteButton = FontManager.RobotoBold18.makeTextButton("Delete", skin);
    deleteButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        new Dialog().setTitle("Are you sure you want to delete this Tower?")
                .setMessage("If you delete this tower, it will disappear forever.\n\nAre you sure?")
                .addButton(ResponseType.POSITIVE, "Yes, delete it", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    savedGameFile.delete();

                    gameFiles.getCell(fileRow).ignore(true);
                    gameFiles.removeActor(fileRow);
                    gameFiles.invalidate();

                    dialog.dismiss();
                  }
                })
                .addButton(ResponseType.NEGATIVE, "Keep it!", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.dismiss();
                  }
                })
                .show();
      }
    });

    StackGroup group = new StackGroup();
    String population = NumberFormat.getNumberInstance().format(towerData.getPlayer().getTotalPopulation());
    group.addActor(FontManager.Default.makeLabel(String.format("Population: %s", population)));
    group.addActor(FontManager.RobotoBold18.makeLabel(towerData.getTowerName()));

    Table box = new Table();
    box.defaults().fillX().space(scale(5));
    box.row().top().left().fillX();
    box.add(group).top().left().expandX();
    box.add(deleteButton).width(scale(80));
    box.add(launchButton).width(scale(80));

//    box.debug();

    return box;
  }
}

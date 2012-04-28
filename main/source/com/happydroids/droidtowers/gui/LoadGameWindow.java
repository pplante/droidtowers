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
import com.happydroids.droidtowers.SplashSceneStates;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.scenes.SplashScene;

import java.text.NumberFormat;

import static com.happydroids.droidtowers.platform.Display.scale;

public class LoadGameWindow extends ScrollableTowerWindow {
  private boolean foundSaveFile;

  public LoadGameWindow(Stage stage, Skin skin) {
    super("Load a Tower", stage, skin);

    FileHandle storage = Gdx.files.external(TowerConsts.GAME_SAVE_DIRECTORY);
    FileHandle[] files = storage.list(".json");

    if (files != null && files.length > 0) {
      for (FileHandle file : files) {
        if (file.name().endsWith(".json")) {
          Table fileRow = makeGameFileRow(file);
          if (fileRow != null) {
            row().fillX();
            add(fileRow).expandX();
            foundSaveFile = true;
          }
        }
      }

      shoveContentUp();
    }

    if (!foundSaveFile) {
      add(FontManager.RobotoBold18.makeLabel("No saved games were found on this device."));
    }
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
    fileRow.defaults().fillX().pad(scale(10)).space(scale(10));
    fileRow.row();
    fileRow.add(imageActor).width(scale(64)).height(scale(64)).center();
    fileRow.add(makeGameFileInfoBox(fileRow, gameSave, towerData)).expandX().top();
    fileRow.row().fillX();
    fileRow.add(new HorizontalRule(Color.DARK_GRAY, 2)).colspan(2);

    return fileRow;
  }

  private Table makeGameFileInfoBox(final Table fileRow, final FileHandle savedGameFile, GameSave towerData) {
    TextButton launchButton = FontManager.RobotoBold18.makeTextButton("Play", skin);
    launchButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
        try {
          TowerGame.changeScene(SplashScene.class, SplashSceneStates.FULL_LOAD, GameSave.readFile(savedGameFile));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });

    TextButton deleteButton = FontManager.RobotoBold18.makeTextButton("Delete", skin);
    deleteButton.setClickListener(new ClickListener() {
      public void click(final Actor actor, float x, float y) {
        new Dialog().setTitle("Are you sure you want to delete this Tower?")
                .setMessage("If you delete this tower, it will disappear forever.\n\nAre you sure?")
                .addButton(ResponseType.POSITIVE, "Yes, delete it", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    savedGameFile.delete();

                    content.getCell(fileRow).ignore();
                    content.removeActor(fileRow);
                    content.invalidate();

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

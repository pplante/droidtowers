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

public class LoadGameWindow extends TowerWindowTwo {
  private boolean foundSaveFile;
  private Pixmap pixmap;
  private NinePatch background;
  private final Table gameFiles;

  public LoadGameWindow(Stage stage, Skin skin) {
    super("Load a Tower", stage, skin);

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
      gameFiles.add(FontManager.Default.makeLabel("No saved games were found on this device."));
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
    fileRow.defaults().fill().pad(10).space(10);
    fileRow.row();
    fileRow.add(imageActor).width(100).right();
    fileRow.add(makeGameFileInfoBox(fileRow, gameSave, towerData)).expandX().top();

    return fileRow;
  }

  private Table makeGameFileInfoBox(final Table fileRow, final FileHandle savedGameFile, GameSave towerData) {
    TextButton launchButton = new TextButton("Play", skin);
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

    TextButton deleteButton = new TextButton("Delete", skin);
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
    box.defaults().fillX().space(5);
    box.row().top().left().fillX();
    box.add(group).top().left().expandX();
    box.add(deleteButton).width(80);
    box.add(launchButton).width(80);

//    box.debug();

    return box;
  }
}

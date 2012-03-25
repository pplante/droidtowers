package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.scenes.TowerScene;

import java.io.File;
import java.io.FilenameFilter;

public class LoadGameWindow extends TowerWindow {
  public LoadGameWindow(Stage stage, Skin skin) {
    super("Load a saved game", stage, skin);

    defaults().top().left().pad(5);

    File storage = new File(Gdx.files.getExternalStoragePath(), ".towergame/");
    File[] files = storage.listFiles(new FilenameFilter() {
      public boolean accept(File file, String fileName) {
        return fileName.endsWith(".json");
      }
    });

    Table gameFiles = new Table();
    gameFiles.defaults();

    for (File file : files) {
      gameFiles.row();
      gameFiles.add(makeGameFileRow(Gdx.files.absolute(file.getAbsolutePath())));
    }

    add(gameFiles).fill();
  }

  private Table makeGameFileRow(final FileHandle gameSave) {
    FileHandle imageFile = Gdx.files.absolute(gameSave.path() + ".png");


    Actor imageActor;
    if (imageFile.exists()) {
      imageActor = new Image(new Texture(imageFile), Scaling.fit, Align.TOP);
    } else {
      imageActor = LabelStyles.Default.makeLabel("No image.");
    }


    Table fileRow = new Table();
    fileRow.defaults().pad(5).width(360);
    fileRow.row().top().left();
    fileRow.add(imageActor).top().left().width(100);
    fileRow.add(makeGameFileInfoBox(gameSave)).top().left().fill();

    return fileRow;
  }

  private Table makeGameFileInfoBox(final FileHandle gameSave) {
    TextButton launchButton = new TextButton("Play", skin);
    launchButton.setClickListener(new ClickListener() {
      public void click(Actor actor, float x, float y) {
        dismiss();
        TowerGame.changeScene(TowerScene.class, gameSave.name());
      }
    });

    Table box = new Table();
    box.defaults().top().left().expand();
    box.row();
    box.add(LabelStyles.Default.makeLabel(gameSave.name())).top().left();
    box.row();
    box.add(launchButton).bottom().right();

    return box;
  }
}

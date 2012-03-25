package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

    for (File file : files) {
      final FileHandle gameSave = Gdx.files.absolute(file.getAbsolutePath());

      TextButton launchButton = new TextButton(file.getName(), skin);
      launchButton.setClickListener(new ClickListener() {
        public void click(Actor actor, float x, float y) {
          dismiss();
          TowerGame.changeScene(TowerScene.class, gameSave.name());
        }
      });

      gameFiles.row();
      Image towerImage = new Image(new Texture(Gdx.files.absolute(gameSave.path() + ".png")), Scaling.fit);
      gameFiles.add(towerImage).size(100, 100);
      gameFiles.add(launchButton).fill();
    }

    add(gameFiles).fill();
  }
}

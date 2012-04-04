/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.gamestate.server.GameUpdate;
import com.unhappyrobot.gamestate.server.HappyDroidServiceCollection;

public class GameUpdateWindow extends TowerWindow {
  private final Table updatePane;

  public GameUpdateWindow(Stage stage, Skin skin) {
    super("Update Manager for Droid Towers", stage, skin);

    updatePane = new Table();
    updatePane.defaults().top().left();

    WheelScrollFlickScrollPane scrollPane = new WheelScrollFlickScrollPane();
    scrollPane.setWidget(updatePane);
    add(scrollPane).maxWidth(500).maxHeight(300).minWidth(400);


    pack();
  }

  public void setUpdateCollection(HappyDroidServiceCollection<GameUpdate> updateCollection) {
    updatePane.clear();

    for (GameUpdate update : updateCollection.getObjects()) {
      GameUpdateRow updateRow = new GameUpdateRow(update);
      updatePane.row();
      updatePane.add(updateRow).fill();
    }

    updatePane.pack();
  }

  private class GameUpdateRow extends Table {
    public GameUpdateRow(GameUpdate update) {
      Label version = LabelStyle.Default.makeLabel("Version: " + update.version);
      Label gitSha = LabelStyle.Default.makeLabel("SHA: " + update.gitSha);
      Label releaseDate = LabelStyle.Default.makeLabel("Released: " + update.releasedOn);

      Label notesField = LabelStyle.Default.makeLabel(update.notes);
      notesField.setWrap(true);

      row();
      add(version).fill();
      add(gitSha);

      row();
      add(releaseDate);

      row();
      add(notesField).colspan(2).fill();


      debug();
    }
  }
}

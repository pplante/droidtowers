/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSave;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.utils.BackgroundTask;

import java.util.List;

public class ViewNeighborSplashScene extends DroidSplashScene {
  private GameObject droid;
  private ViewNeighborSplashScene.FetchNeighborsList fetchNeighborsList;
  private CloudGameSave playerCloudGameSave;

  @Override
  public void create(Object... args) {
    super.create(args);
    GameState playerGameState = (GameState) args[0];
    playerCloudGameSave = playerGameState.getCloudGameSave();

    fetchNeighborsList = new FetchNeighborsList();
    fetchNeighborsList.run();

  }

  @Override
  protected void handleBackButton() {
    fetchNeighborsList.cancel();
  }

  private class FetchNeighborsList extends BackgroundTask {
    private List<FriendCloudGameSave> friendGames;
    public boolean fetchWasSuccessful;

    @Override
    protected void execute() throws Exception {
      if (TowerGameService.instance().isAuthenticated()) {
        playerCloudGameSave.fetchBlocking();
        friendGames = Lists.newArrayList();
        fetchWasSuccessful = playerCloudGameSave.fetchNeighbors();
      }
    }

    @Override
    public synchronized void afterExecute() {
      if (fetchWasSuccessful) {
        TowerGame.popScene();
        TowerGame.pushScene(ViewNeighborScene.class, playerCloudGameSave, friendGames);
      } else {
        displayConnectionErrorDialog();
      }
    }
  }

  private void displayConnectionErrorDialog() {
    new Dialog()
            .setTitle("Connection Failed")
            .setMessage("Sorry, but we were not able to fetch your neighborhood.\n\nPlease check your internet connection and try again.")
            .addButton("Okay", new OnClickCallback() {
              @Override
              public void onClick(Dialog dialog) {
                dialog.dismiss();
              }
            })
            .setDismissCallback(new Runnable() {
              @Override
              public void run() {
                TowerGame.popScene();
              }
            })
            .show();
  }
}

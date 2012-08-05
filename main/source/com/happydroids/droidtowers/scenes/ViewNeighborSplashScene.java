/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSaveCollection;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.scenes.components.ProgressPanel;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.utils.BackgroundTask;

public class ViewNeighborSplashScene extends SplashScene {
  private GameObject droid;
  private ViewNeighborSplashScene.FetchNeighborsList fetchNeighborsList;
  private GameState playerGameState;

  @Override
  public void create(Object... args) {
    super.create(args);

    progressPanel.remove();
    progressPanel = null;

    progressPanel = new ProgressPanel();
    progressPanel.setProgress(0);
    center(progressPanel);
    addActor(progressPanel);

    playerGameState = (GameState) args[0];

    fetchNeighborsList = new FetchNeighborsList();
    fetchNeighborsList.run();
  }

  private class FetchNeighborsList extends BackgroundTask {
    public boolean fetchWasSuccessful;

    @Override
    protected void execute() throws Exception {
      progressPanel.setProgress(10);
      if (TowerGameService.instance().isAuthenticated()) {
        CloudGameSave cloudGameSave = playerGameState.getCloudGameSave();
        if (!cloudGameSave.isSaved()) {
          playerGameState.saveGame(true);
        } else {
          cloudGameSave.fetch();
        }

        progressPanel.setProgress(33);

        FriendCloudGameSaveCollection neighborGameSaves = cloudGameSave.getNeighborGameSaves();
        if (!neighborGameSaves.isEmpty()) {
          int progressPerFetch = 66 / neighborGameSaves.size();
          int progressSoFar = 33;
          for (FriendCloudGameSave friendGameSave : neighborGameSaves.getObjects()) {
            friendGameSave.fetch();
            progressSoFar += progressPerFetch;
            progressPanel.setProgress(progressSoFar);
          }
        }

        fetchWasSuccessful = true;
        progressPanel.setProgress(100);
      }
    }

    @Override
    public synchronized void afterExecute() {
      progressPanel.setProgress(100);
      if (fetchWasSuccessful) {
        SceneManager.popScene();
        SceneManager.pushScene(ViewNeighborScene.class, playerGameState);
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
                SceneManager.popScene();
              }
            })
            .show();
  }
}

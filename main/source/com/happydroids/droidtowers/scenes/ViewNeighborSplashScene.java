/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSaveCollection;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.OnClickCallback;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.utils.Random;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import com.happydroids.utils.BackgroundTask;
import org.apache.http.HttpResponse;

public class ViewNeighborSplashScene extends Scene {
  private GameObject droid;
  private ViewNeighborSplashScene.FetchNeighborsList fetchNeighborsList;

  @Override
  public void create(Object... args) {
    getCamera().position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    getCamera().zoom = CameraController.ZOOM_MIN;

    InputSystem.instance().bind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);

    droid = new GameObject(new Texture("happy-droid.png"));
    droid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
    Tween.to(droid, GameObjectAccessor.OPACITY, 1000)
            .target(0f)
            .setCallback(new TweenCallback() {
              @Override
              public void onEvent(int type, BaseTween source) {
                droid.setPosition(Random.randomInt(Gdx.graphics.getWidth() / 2), Random.randomInt(Gdx.graphics.getHeight()) / 2);
              }
            })
            .setCallbackTriggers(TweenCallback.END)
            .repeat(Tween.INFINITY, 100)
            .start(TweenSystem.getTweenManager());

    Label fetchingLabel = FontManager.Roboto64.makeLabel("fetching neighbors :D");
    getStage().addActor(fetchingLabel);

    fetchNeighborsList = new FetchNeighborsList();
    fetchNeighborsList.run();
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
    getSpriteBatch().begin();
    droid.draw(getSpriteBatch());
    getSpriteBatch().end();
  }

  @Override
  public void dispose() {
    InputSystem.instance().unbind(TowerConsts.NEGATIVE_BUTTON_KEYS, goBackHomeCallback);
  }

  private InputCallback goBackHomeCallback = new InputCallback() {
    @Override
    public boolean run(float timeDelta) {
      fetchNeighborsList.cancel();
      TowerGame.popScene();
      return true;
    }
  };

  private class FetchNeighborsList extends BackgroundTask {
    private FriendCloudGameSaveCollection friendGames;
    public boolean fetchWasSuccessful;

    @Override
    protected void execute() {
      friendGames = new FriendCloudGameSaveCollection();
      friendGames.fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<FriendCloudGameSave>>() {
        @Override
        public void onError(HttpResponse response, int statusCode, HappyDroidServiceCollection<FriendCloudGameSave> collection) {
          System.out.println("collection = " + collection);
        }

        @Override
        public void onSuccess(HttpResponse response, HappyDroidServiceCollection<FriendCloudGameSave> collection) {
          fetchWasSuccessful = true;
        }
      });
    }

    @Override
    public synchronized void afterExecute() {
      if (fetchWasSuccessful) {
        TowerGame.popScene();
        TowerGame.pushScene(ViewNeighborScene.class, friendGames);
      } else {
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
  }
}

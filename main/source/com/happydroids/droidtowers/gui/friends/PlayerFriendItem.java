/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.friends;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSave;
import com.happydroids.droidtowers.gamestate.server.FriendCloudGameSaveCollection;
import com.happydroids.droidtowers.gamestate.server.PlayerProfile;
import com.happydroids.droidtowers.gui.HorizontalRule;
import com.happydroids.droidtowers.gui.VibrateClickListener;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import org.apach3.http.HttpResponse;

import java.net.URI;

import static com.happydroids.droidtowers.Colors.DARK_GRAY;
import static com.happydroids.droidtowers.gui.FontManager.Roboto18;

public class PlayerFriendItem extends Table {
  private PlayerProfile profile;
  private final GameState playerGameState;

  public PlayerFriendItem(PlayerProfile profile, GameState playerGameState) {
    this.profile = profile;
    this.playerGameState = playerGameState;
  }

  public PlayerFriendItem(GameState playerGameState) {
    this.playerGameState = playerGameState;
  }

  protected String getPlayerName() {
    return profile.getFullName();
  }

  public void createChildren(TextureRegionDrawable facebookIcon) {
    clear();
    defaults().pad(Display.devicePixel(4));

    row().fill();
    add(new Image(facebookIcon, Scaling.none)).spaceRight(Display.devicePixel(10));
    add(Roboto18.makeLabel(getPlayerName())).expandX();
    add(makeActionButton());

    row().fill();
    add(new HorizontalRule(DARK_GRAY, 1)).colspan(3);
  }

  protected TextButton makeActionButton() {
    TextButton button = Roboto18.makeTextButton("Add Neighbor");
    button.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new FriendCloudGameSaveCollection()
                .filterBy("owner_resource_uri", URI.create(profile.getResourceUri()).getPath())
                .fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<FriendCloudGameSave>>() {
                  @Override
                  public void onSuccess(HttpResponse response, HappyDroidServiceCollection<FriendCloudGameSave> collection) {
                    for (FriendCloudGameSave cloudGameSave : collection.getObjects()) {
                      playerGameState.getCloudGameSave().getNeighborGameSaves().add(cloudGameSave);
                    }

                    playerGameState.saveGame(true);
                  }
                });
      }
    });
    return button;
  }

  public boolean playerNameMatches(String text) {
    return getPlayerName().toLowerCase().contains(text);
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.entities.Avatar;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.platform.Display;

public class AvatarInfoRow extends Table {

  private final ProgressBar hungerBar;
  private final ProgressBar restaurantsSatsifaction;
  private float timeUntilUpdate;
  private final Avatar avatar;
  private final Label movingToLabel;


  public AvatarInfoRow(Avatar avatar) {
    super();
    this.avatar = avatar;

    hungerBar = new ProgressBar((int) (avatar.getHungerLevel() * 100));
    restaurantsSatsifaction = new ProgressBar((int) (avatar.getSatisfactionFood() * 100));
    movingToLabel = FontManager.Roboto12.makeLabel("");

    row().fillX().space(Display.devicePixel(8));
    Image avatarImage = new Image(new TextureRegionDrawable(avatar), Scaling.none);
    avatarImage.setColor(avatar.getColor());
    add(avatarImage);
    add(FontManager.Default.makeLabel(avatar.getName())).expandX();
    add(hungerBar);
    add(restaurantsSatsifaction);
    add(movingToLabel).width(200);
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    timeUntilUpdate -= delta;
    if (timeUntilUpdate <= 0) {
      timeUntilUpdate = 2f;

      hungerBar.setValue((int) (avatar.getHungerLevel() * 100));
      restaurantsSatsifaction.setValue((int) (avatar.getSatisfactionFood() * 100));

      GridObject target = avatar.getMovementTarget();
      if (target != null) {
        movingToLabel.setText(target.getName());
      } else {
        movingToLabel.setText("");
      }
    }
  }
}

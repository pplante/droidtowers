/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class AnimatedHappyDroid extends Group {
  private final Image leftArm;
  private boolean shouldYoYo;
  private final Image body;


  public AnimatedHappyDroid() {
    super();

    TextureAtlas atlas = TowerAssetManager.textureAtlas("happydroid.txt");
    body = new Image(atlas.findRegion("body"));
    body.setScaling(Scaling.fill);

    leftArm = new Image(atlas.findRegion("left-arm"));
    leftArm.setScaling(Scaling.fill);
    leftArm.setOrigin(leftArm.getWidth() / 2, 0);
    leftArm.setRotation(30f);
    leftArm.addAction(forever(sequence(
                                              repeat(3, sequence(
                                                                        rotateTo(-15f, 0.2f),
                                                                        rotateTo(45f, 0.2f)
                                              )),
                                              rotateTo(30f),
                                              delay(5f)

    )
    ));

    addActor(body);
    addActor(leftArm);
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    body.setScale(getScaleX(), getScaleY());
    leftArm.setScale(getScaleX(), getScaleY());

    body.setPosition(getX() + leftArm.getImageWidth() * getScaleX() * 2, getY());
    body.draw(batch, parentAlpha);

    leftArm.setPosition(getX() + leftArm.getImageWidth() * getScaleX(), getY() + (body.getImageHeight() * 0.7f) * getScaleY());
    leftArm.draw(batch, parentAlpha);
  }

  @Override
  public float getHeight() {
    return body.getHeight();
  }


  @Override
  public void setHeight(float height) {
    int regionHeight = ((TextureRegionDrawable) body.getDrawable()).getRegion().getRegionHeight();
    setScale(height / regionHeight);
  }
}

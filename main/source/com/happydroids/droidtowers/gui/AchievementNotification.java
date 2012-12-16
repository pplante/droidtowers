/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HeyZapCheckInButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.Achievement;
import com.happydroids.droidtowers.platform.Display;

public class AchievementNotification extends Dialog {
  private final ParticleEffect particleEffect;

  public AchievementNotification(Achievement achievement) {
    super();

    particleEffect = new ParticleEffect();
    particleEffect.load(Gdx.files.internal("particles/sparkle-dialog.p"), Gdx.files.internal("particles"));

    setTitle("Achievement Complete: " + achievement.getName());

    Table t = new Table();
    t.defaults().left().top().space(Display.devicePixel(8)).expandX();
    t.add(FontManager.Roboto24.makeLabel("Great job!")).top();
    t.row();
    t.add(new HorizontalRule(Color.GRAY, 1)).fillX();
    t.row();
    t.add(FontManager.Default.makeLabel(achievement.toRewardString())).top();

    Table c = new Table();
    c.defaults().top().left();
    c.row().fillX();
    c.add(new Image(TowerAssetManager.drawableFromAtlas("trophy", "hud/menus.txt"), Scaling.none))
        .padRight(Display.devicePixel(8));
    c.add(t).expandX().minWidth(300);
    c.setClip(true);
    c.pack();


    addButton("Dismiss", new VibrateClickListener() {
      @Override public void onClick(InputEvent event, float x, float y) {
        dismiss();
      }
    });
    if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
      addButton(new HeyZapCheckInButton("Completed achievement: " + achievement.getName()));
    }

    setView(c);
  }

  @Override public void act(float delta) {
    super.act(delta);
    particleEffect.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
    particleEffect.update(delta);
  }

  @Override protected void drawModalNoise(SpriteBatch batch) {
    super.drawModalNoise(batch);
    particleEffect.draw(batch);
  }
}

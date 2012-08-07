/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.achievements.TutorialStep;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.events.AchievementCompletionEvent;
import com.happydroids.droidtowers.events.GridObjectPlacedEvent;
import com.happydroids.droidtowers.events.PurchaseEvent;
import com.happydroids.droidtowers.events.RespondsToWorldSizeChange;
import com.happydroids.droidtowers.graphics.effects.ParticleEffectManager;
import com.happydroids.droidtowers.grid.GameGrid;

import java.util.Iterator;
import java.util.Set;

import static com.happydroids.droidtowers.TowerConsts.GRID_UNIT_SIZE;
import static com.happydroids.droidtowers.TowerConsts.LOBBY_FLOOR;

public class FireWorksLayer extends GameLayer<ParticleEffectManager> implements RespondsToWorldSizeChange {
  private static final float FIREWORK_DURATION = 10f;

  private final Iterator<float[]> colorsIterator;
  private final Rectangle worldBounds;
  private boolean playFireWorks;
  private float timePlaying;

  public FireWorksLayer(GameGrid gameGrid) {
    super();

    gameGrid.events().register(this);
    TutorialEngine.instance().eventBus().register(this);
    AchievementEngine.instance().eventBus().register(this);

    ParticleEffect particleEffect = new ParticleEffect();
    particleEffect.load(Gdx.files.internal("particles/firework.p"), Gdx.files.internal("particles"));

    Set<float[]> colors = Sets.newHashSet();
    colors.add(makeParticleColorArray(Color.WHITE, Color.RED, Color.ORANGE));
    colors.add(makeParticleColorArray(Color.WHITE, Color.BLUE, Color.GREEN));
    colors.add(makeParticleColorArray(Color.WHITE, Color.YELLOW, Color.PINK));
    colors.add(makeParticleColorArray(Color.WHITE, Color.PINK, Color.MAGENTA));
    colors.add(makeParticleColorArray(Color.WHITE, Color.BLUE, Color.CYAN));

    colorsIterator = Iterators.cycle(colors);

    worldBounds = new Rectangle();
    for (int i = 0; i < 10; i++) {
      addChild(new ParticleEffectManager(new ParticleEffect(particleEffect), colorsIterator, worldBounds));
    }
  }

  private float[] makeParticleColorArray(final Color colorA, final Color colorB, final Color colorC) {
    return new float[]{
                              colorA.r, colorA.g, colorA.b, colorA.a,
                              colorB.r, colorB.g, colorB.b, colorB.a,
                              colorC.r, colorC.g, colorC.b, colorC.a
    };
  }

  @Override
  protected boolean shouldCullObjects() {
    return false;
  }

  private void play() {
    playFireWorks = true;
    for (GameObject gameObject : gameObjects) {
      ((ParticleEffectManager) gameObject).resetEffect();
    }
  }

  @Override
  public void update(float timeDelta) {
    super.update(timeDelta);

    if (playFireWorks) {
      timePlaying += timeDelta;

      if (timePlaying >= FIREWORK_DURATION) {
        timePlaying = 0f;
        playFireWorks = false;

        for (GameObject gameObject : gameObjects) {
          ((ParticleEffectManager) gameObject).stop();
        }
      }
    }
  }

  @Override
  public void updateWorldSize(Vector2 worldSize) {
    int groundHeight = GRID_UNIT_SIZE * LOBBY_FLOOR;
    worldBounds.set(0, groundHeight + GRID_UNIT_SIZE * 5, worldSize.x, groundHeight + GRID_UNIT_SIZE * 20);
  }

  @Subscribe
  public void PurchaseManger_onPurchase(PurchaseEvent event) {
    play();
  }

  @Subscribe
  public void GameGrid_onGridObjectPlaced(GridObjectPlacedEvent event) {
//    play();
  }

  @Subscribe
  public void AchievementEngine_onAchievementCompletion(AchievementCompletionEvent event) {
    if (event.getAchievement() instanceof TutorialStep) {
      if (event.getAchievement().getId().equalsIgnoreCase("tutorial-finished")) {
        play();
      }
    } else {
      play();
    }
  }
}

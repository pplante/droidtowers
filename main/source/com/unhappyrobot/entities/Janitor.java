package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.controllers.AvatarLayer;

import java.util.List;

public class Janitor extends Avatar {
  public Janitor(AvatarLayer avatarLayer) {
    super(avatarLayer);
    setColor(Color.WHITE);
  }

  @Override
  protected TextureAtlas getTextureAtlas() {
    return new TextureAtlas(Gdx.files.internal("characters/janitor.txt"));
  }

  @Override
  public void beginNextAction() {
    GuavaSet<GridObject> commercialSpaces = gameGrid.getInstancesOf(CommercialSpace.class);
    if (commercialSpaces != null) {
      commercialSpaces.filterBy(new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject input) {
          return CommercialSpace.class.cast(input).getLastCleanedAt() > 0;
        }
      });

      List<GridObject> sortedObjects = commercialSpaces.sortedBy(new Function<GridObject, Long>() {
        public Long apply(@Nullable GridObject gridObject) {
          return ((CommercialSpace) gridObject).getLastCleanedAt();
        }
      });

      navigateToGridObject(Iterables.getFirst(sortedObjects, null));
    }
  }
}

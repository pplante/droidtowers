package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sun.istack.internal.Nullable;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.types.ProviderType;
import com.unhappyrobot.types.RoomType;

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
      List<GridObject> sortedObjects = commercialSpaces.filterBy(new Predicate<GridObject>() {
        public boolean apply(@Nullable GridObject input) {
          ProviderType providerType = RoomType.class.cast(input.getGridObjectType()).provides();
          int numVisitors = CommercialSpace.class.cast(input).getNumVisitors();
          return numVisitors > 0 && checkProviderType(providerType);
        }
      }).sortedBy(new Function<GridObject, Long>() {
        public Long apply(@Nullable GridObject gridObject) {
          return ((CommercialSpace) gridObject).getLastCleanedAt();
        }
      });

      navigateToGridObject(Iterables.getFirst(sortedObjects, null));
    }
  }

  protected boolean checkProviderType(ProviderType providerType) {
    return providerType.equals(ProviderType.FOOD) || providerType.equals(ProviderType.OFFICE_SERVICES);
  }
}

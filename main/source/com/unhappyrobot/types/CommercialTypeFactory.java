package com.unhappyrobot.types;

import com.badlogic.gdx.Gdx;

public class CommercialTypeFactory extends GridObjectTypeFactory<CommercialType> {
  private static CommercialTypeFactory instance;

  private CommercialTypeFactory() {
    super(CommercialType.class);

    parseTypesFile(Gdx.files.internal("params/commercial-spaces.json"));
  }

  public static CommercialTypeFactory getInstance() {
    if (instance == null) {
      instance = new CommercialTypeFactory();
    }

    return instance;
  }
}

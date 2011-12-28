package com.unhappyrobot.types;

import com.unhappyrobot.entities.GridObject;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class GridObjectType {
  @JsonProperty
  private int coins;
  @JsonProperty
  private int gold;

  public abstract GridObject makeGridObject();

  public boolean continuousPlacement() {
    return false;
  }

  @JsonProperty
  public void setCoins(int c) {
    coins = c;
  }

  @JsonProperty
  public void setGold(int g) {
    gold = g;
  }

  public int getCoins() {
    return coins;
  }

  public int getGold() {
    return gold;
  }

  public abstract String getName();
}

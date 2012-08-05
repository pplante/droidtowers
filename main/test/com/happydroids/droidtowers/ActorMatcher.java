/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pivotallabs.greatexpectations.MatcherOf;
import com.pivotallabs.greatexpectations.matchers.ObjectMatcher;

@MatcherOf(Actor.class)
public class ActorMatcher<T extends Actor, M extends ActorMatcher<T, M>> extends ObjectMatcher<T, M> {
  public boolean toBeAt(float x, float y) {
    return actual.getX() == x && actual.getY() == y;
  }
}


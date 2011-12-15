package com.unhappyrobot.spec;

import com.badlogic.gdx.math.Vector2;
import com.pivotallabs.greatexpectations.MatcherOf;
import com.pivotallabs.greatexpectations.matchers.ObjectMatcher;

@MatcherOf(Vector2.class)
public class Vector2Matcher<T extends Vector2, M extends Vector2Matcher<T, M>> extends ObjectMatcher<T, M> {
  @Override
  public boolean toEqual(T expected) {
    return actual.x == expected.x && actual.y == expected.y;
  }
}


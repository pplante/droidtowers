/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.pivotallabs.greatexpectations.MatcherOf;
import com.pivotallabs.greatexpectations.matchers.ObjectMatcher;

import static com.unhappyrobot.Expect.expect;

@MatcherOf(Group.class)
public class GroupMatcher<T extends Group, M extends GroupMatcher<T, M>> extends ObjectMatcher<T, M> {
  @Override
  public boolean toEqual(T expected) {
    return actual.x == expected.x && actual.y == expected.y;
  }

  public boolean toHaveChildren(int numChildren) {
    return actual.getActors().size() == numChildren;
  }

  public boolean toHaveLabelWithText(String text) {
    for (Actor actor : actual.getActors()) {
      if (actor instanceof Label) {
        Label label = (Label) actor;
        if (label.getText().equals(text)) {
          return true;
        } else if (label.getText().toString().contains(text)) {
          throw new RuntimeException("Found potential match: " + label.getText());
        }
      } else if (actor instanceof Group) {
        Group group = (Group) actor;
        if (expect(group).toHaveLabelWithText(text)) {
          return true;
        }
      }
    }

    return false;
  }
}


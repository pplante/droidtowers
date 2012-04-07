/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.unhappyrobot;

import com.pivotallabs.greatexpectations.MatcherOf;
import com.pivotallabs.greatexpectations.matchers.ObjectMatcher;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

@MatcherOf({InputStream.class})
public class InputStreamMatcher<T extends InputStream, M extends InputStreamMatcher<T, M>> extends ObjectMatcher<T, M> {
  @Override
  public boolean toEqual(T expected) {
    try {
      return IOUtils.toString(actual).equals(IOUtils.toString(expected));
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }


}

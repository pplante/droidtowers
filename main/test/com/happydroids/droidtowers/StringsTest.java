/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.happydroids.droidtowers.utils.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(BlockJUnit4ClassRunner.class)
public class StringsTest {
  @Test
  public void wrap_shouldWork() {
    expect(StringUtils.wrap("this is a test.", 4)).toEqual("this\nis a\ntest.");
    expect(StringUtils.wrap("this is a, test.", 4)).toEqual("this\nis a,\ntest.");
  }
}

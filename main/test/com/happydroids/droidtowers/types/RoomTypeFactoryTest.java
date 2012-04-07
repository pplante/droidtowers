/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.happydroids.droidtowers.GdxTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(GdxTestRunner.class)
public class RoomTypeFactoryTest {
  @Test
  public void all_returnsKnownRoomTypes() {
    List<RoomType> types = RoomTypeFactory.instance().all();

    expect(types.size()).toEqual(3);

    expect(types.get(0).getName()).toEqual("Generic 2x1");
    expect(types.get(1).getName()).toEqual("Generic 3x1");
    expect(types.get(2).getName()).toEqual("Generic 4x1");
  }
}

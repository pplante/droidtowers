/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.achievements;

import com.happydroids.droidtowers.NonGLTestRunner;
import com.happydroids.droidtowers.types.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(NonGLTestRunner.class)
public class AchievementEngineTest {
  @Before
  public void setUp() {
    RoomTypeFactory.instance();
    CommercialTypeFactory.instance();
    ServiceRoomTypeFactory.instance();
    ElevatorTypeFactory.instance();
    StairTypeFactory.instance();
  }

  @Test
  public void shouldParseAllAchievements() {
    AchievementEngine engine = new AchievementEngine();
  }
}

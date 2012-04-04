/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerGameTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TowerGameTestRunner.class)
public class HappyDroidServiceTest {
  @Test
  public void uploadGameSave() {
    HappyDroidService.setInstance(new HappyDroidService());
    HappyDroidService.instance().setDeviceOSName("tests");
    HappyDroidService.instance().setDeviceOSVersion("1.0");

    HappyDroidService.instance().registerDevice();
//    HappyDroidService.instance().uploadGameSave(new GameSave(new GameGrid(camera), new OrthographicCamera(), Player.instance()));
  }
}

package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.unhappyrobot.TowerGameTestRunner;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.gamestate.GameSave;
import com.unhappyrobot.grid.GameGrid;
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
    HappyDroidService.instance().uploadGameSave(new GameSave(new GameGrid(), new OrthographicCamera(), Player.instance()));
  }
}

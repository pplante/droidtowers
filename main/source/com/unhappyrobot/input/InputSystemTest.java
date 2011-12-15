package com.unhappyrobot.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.unhappyrobot.spec.GdxTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.unhappyrobot.spec.Expect.expect;

@RunWith(GdxTestRunner.class)
public class InputSystemTest {
  private InputSystem inputSystem;
  private OrthographicCamera camera;

  @Before
  public void setUp() {
    camera = new OrthographicCamera();
    inputSystem = new InputSystem(camera, new Vector2(800, 800));
  }

  @Test
  public void touchAndFling_shouldMoveCamera() {
    inputSystem.touchDown(10, 10, 0, 0);
    inputSystem.touchDragged(100, 100, 0);
    inputSystem.touchUp(150, 150, 0, 0);

    expect(camera.position).toEqual(new Vector3(310, 490, 0));
  }
}

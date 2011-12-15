package com.unhappyrobot.spec.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import org.junit.Before;
import org.junit.Test;

import static com.unhappyrobot.spec.Expect.expect;

public class GameGridTest {
  private GameGrid grid;
  private GridObject gridObject;

  @Before
  public void setUp() {
    grid = new GameGrid();
    grid.setUnitSize(8, 8);
    grid.setGridSize(16, 16);

    gridObject = new TestGridObject();
    gridObject.position = new Vector2(1, 1);
    gridObject.size = new Vector2(2, 2);
  }

  @Test
  public void getObjectsAtPosition_shouldReturnNullWhenPositionIsEmpty() {
    expect(grid.getObjectsAtPosition(1, 1)).toBeNull();
  }

  @Test
  public void resizeGrid_shouldCreateANewGridLayout() {
    grid.setGridSize(10, 4);
    grid.resizeGrid();

    expect(grid.getLayout()).not.toBeNull();
    expect(grid.getLayout().length).toEqual(10);
    expect(grid.getLayout()[0].length).toEqual(4);
  }

  @Test
  public void resizeGrid_shouldCopyTheExistingLayout() {
    gridObject.position = new Vector2(1, 1);
    grid.addObject(gridObject);

    grid.setGridSize(10, 4);
    grid.resizeGrid();

    expect(grid.getObjectsAtPosition(1, 1)).toContainExactly(gridObject);
  }

  @Test
  public void addObject_shouldAddObjectToInternalSet() {
    grid.addObject(gridObject);
    grid.addObject(gridObject);
    grid.addObject(gridObject);

    expect(grid.getObjects()).toContain(gridObject);
  }

  @Test
  public void addObject_doesNotAllowObjectsWithoutAPosition() {
    expect(grid.addObject(new TestGridObject())).toBeFalse();
  }

  @Test
  public void addObject_shouldFillInTheGridLayout() {
    expect(grid.getObjectsAtPosition(1, 1)).toBeNull();

    gridObject.position = new Vector2(1, 1);
    grid.addObject(gridObject);

    expect(grid.getObjectsAtPosition(1, 1)).toContainExactly(gridObject);
  }

  @Test
  public void canObjectBeAt_shouldReturnFalseIfObjectsCannotOccupySameSpace() {
    GreedyBastard bastard = new GreedyBastard();
    bastard.position = new Vector2(4, 4);
    bastard.size = new Vector2(3, 3);

    GridObject shareable = new TestGridObject();
    shareable.size = new Vector2(1, 1);

    grid.addObject(bastard);
    expect(grid.canObjectBeAt(shareable, bastard.position)).toBeFalse();
  }

  @Test
  public void canObjectBeAt_shouldReturnTrueIfObjectsCanShareSpace() {
    GridObject shareable1 = new TestGridObject();
    shareable1.position = new Vector2(1, 1);
    shareable1.size = new Vector2(1, 1);
    grid.addObject(shareable1);

    GridObject shareable2 = new TestGridObject();
    shareable2.size = new Vector2(1, 1);

    expect(grid.canObjectBeAt(shareable2, shareable1.position)).toBeTrue();
  }

  private class GreedyBastard extends GridObject {
    @Override
    public boolean canShareSpace() {
      return false;
    }

    @Override
    public void render(SpriteBatch spriteBatch, Camera camera) {
    }

    @Override
    public Texture getTexture() {
      return null;
    }
  }

  private class TestGridObject extends GridObject {
    @Override
    public void render(SpriteBatch spriteBatch, Camera camera) {
    }

    @Override
    public Texture getTexture() {
      return null;
    }
  }
}

package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.grid.GridPosition;

import java.util.HashSet;
import java.util.Set;

public class GameGrid {
  public Vector2 unitSize;
  public Color gridColor;
  public Vector2 gridOrigin;
  public Vector2 gridSize;

  private HashSet<GridObject> children;
  private GridPosition[][] gridLayout;

  public GameGrid() {
    gridColor = Color.GREEN;
    gridOrigin = new Vector2();
    gridSize = new Vector2(8, 8);
    unitSize = new Vector2(16, 16);

    children = new HashSet<GridObject>();

    resizeGrid();
  }

  public void resizeGrid() {
    GridPosition[][] newGrid = new GridPosition[(int) gridSize.x][(int) gridSize.y];
    if (gridLayout != null) {
      GridPosition[][] oldGrid = gridLayout;

      for (int x = 0; x < Math.min(newGrid.length, oldGrid.length); x++) {
        for (int y = 0; y < Math.min(newGrid[x].length, oldGrid[x].length); y++) {
          if (oldGrid[x][y] != null) {
            newGrid[x][y] = new GridPosition();
            newGrid[x][y].addAll(oldGrid[x][y]);
          }
        }
      }
    }

    gridLayout = newGrid;
  }

  public void setUnitSize(int width, int height) {
    unitSize.set(width, height);
  }

  public void setGridSize(int width, int height) {
    gridSize.set(width, height);
  }

  public Renderer getRenderer() {
    return new Renderer();
  }

  public void setGridOrigin(float x, float y) {
    gridOrigin.set(x, y);
  }

  public Vector2 getPixelSize() {
    return new Vector2(gridSize.x * unitSize.x, gridSize.y * unitSize.y);
  }

  public boolean addObject(GridObject gridObject) {
    if (gridObject.position == null) {
      return false;
    }

    children.add(gridObject);

    for (int x = (int) gridObject.position.x; x < gridObject.position.x + gridObject.size.x; x++) {
      for (int y = (int) gridObject.position.y; y < gridObject.position.y + gridObject.size.y; y++) {
        if (gridLayout[x][y] == null) {
          gridLayout[x][y] = new GridPosition();
        }

        gridLayout[x][y].add(gridObject);
      }
    }

    return true;
  }

  public Set<GridObject> getObjects() {
    return children;
  }

  public boolean canObjectBeAt(GridObject object, Vector2 otherPosition) {
    for (int x = (int) otherPosition.x; x < otherPosition.x + object.size.x; x++) {
      for (int y = (int) otherPosition.y; y < otherPosition.y + object.size.y; y++) {
        for (GridObject otherObject : getObjectsAtPosition(x, y)) {
          if (!otherObject.canShareSpace()) {
            return false;
          }
        }
      }
    }

    return true;
  }

  public GridPosition[][] getLayout() {
    return gridLayout;
  }

  public GridPosition getObjectsAtPosition(int x, int y) {
    return gridLayout[x][y];
  }

  public void setGridColor(float r, float g, float b, float a) {
    gridColor.set(r, g, b, a);
  }

  public class Renderer extends GameLayer {
    private ImmediateModeRenderer10 gl;

    public Renderer() {
      gl = new ImmediateModeRenderer10();
    }

    @Override
    public void render(SpriteBatch spriteBatch, Camera camera) {
      spriteBatch.begin();
      for (GridObject child : children) {
        Sprite sprite = new Sprite(child.getTexture());

        sprite.setPosition(gridOrigin.x + child.position.x * unitSize.x, gridOrigin.y + child.position.y * unitSize.y);
        sprite.setSize(child.size.x * unitSize.x, child.size.y * unitSize.y);
        sprite.setU(0f);
        sprite.setV(0f);
        sprite.setU2(sprite.getWidth() / sprite.getTexture().getWidth());
        sprite.setV2(sprite.getHeight() / sprite.getTexture().getHeight());

        sprite.draw(spriteBatch);
      }
      spriteBatch.end();

      renderGridLines();
    }

    private void renderGridLines() {
      gl.begin(GL10.GL_LINES);

      for (int i = 0; i <= gridSize.x; i++) {
        addPoint(gridOrigin.x + (i * unitSize.x), gridOrigin.y);
        addPoint(gridOrigin.x + (i * unitSize.x), gridOrigin.y + gridSize.y * unitSize.y);
      }

      for (int i = 0; i <= gridSize.y; i++) {
        addPoint(gridOrigin.x, gridOrigin.y + (i * unitSize.y));
        addPoint(gridOrigin.x + gridSize.x * unitSize.x, gridOrigin.y + (i * unitSize.y));
      }

      gl.end();
    }

    private void addPoint(float x, float y) {
      gl.color(gridColor.r, gridColor.g, gridColor.b, gridColor.a);
      gl.vertex(x, y, 0.0f);
    }
  }
}

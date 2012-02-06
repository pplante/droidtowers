package com.unhappyrobot.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;

import java.util.List;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class TransitLine {
  private List<Vector2> points;
  private final GameGrid gameGrid;
  private GridObject startObject;
  private GridObject endObject;
  private Color color = Color.RED;

  public TransitLine(GameGrid gameGrid) {
    this.gameGrid = gameGrid;
    points = Lists.newArrayList();
  }

  public void addPoint(Vector2 point) {
    points.add(point);
  }

  public void render(ShapeRenderer shapeRenderer) {
    if (points.size() < 2) {
      return;
    }

    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(color);

    Vector2 prevGridPoint = null;
    for (Vector2 point : points) {
      if (prevGridPoint != null) {
        shapeRenderer.line(prevGridPoint.x, prevGridPoint.y, point.x, point.y);
      }
      prevGridPoint = point;
    }

    shapeRenderer.end();
  }

  public ImmutableList<Vector2> getPoints() {
    return ImmutableList.copyOf(points);
  }

  public void setColor(Color color) {
    this.color = color;
  }
}

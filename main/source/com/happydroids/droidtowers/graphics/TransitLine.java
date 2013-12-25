/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class TransitLine {
  private List<Vector2> points;
  private Color color = Color.RED;
  private int highlightPoint;
  private boolean visible;

  public TransitLine() {
    points = Lists.newArrayList();
  }

  public void addPoint(Vector2 point) {
    points.add(point);
  }

  public void render(ShapeRenderer shapeRenderer) {
    if (!visible || points.size() < 2) {
      return;
    }

    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(color);

    Vector2 prevGridPoint = points.get(0);
    for (int i = 1; i < points.size(); i++) {
      Vector2 point = points.get(i);
      shapeRenderer.line(prevGridPoint.x, prevGridPoint.y, point.x, point.y);
      prevGridPoint = point;
    }

    shapeRenderer.end();

    shapeRenderer.begin(ShapeType.Filled);
    for (Vector2 point : points) {
      shapeRenderer.setColor(color);
      shapeRenderer.circle(point.x, point.y, points.indexOf(point) == highlightPoint ? 8f : 5f);
    }

    shapeRenderer.end();
  }

  public ImmutableList<Vector2> getPoints() {
    return ImmutableList.copyOf(points);
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void addPoint(float x, float y) {
    points.add(new Vector2(x, y));
  }

  public void addPoints(List<Vector2> points) {
    this.points.addAll(points);
  }

  public void clear() {
    points.clear();
  }

  public void highlightPoint(int highlightPoint) {
    this.highlightPoint = highlightPoint;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}

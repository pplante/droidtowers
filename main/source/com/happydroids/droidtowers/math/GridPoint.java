/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import static com.happydroids.droidtowers.TowerConsts.GRID_UNIT_SIZE;

/**
 * Encapsulates a 2D vector. Allows chaining methods by returning a reference to itself
 * Copied from Vector2 which uses floats, I wanted integers so I didn't have to constantly cast and round
 * for my grid points.
 *
 * @author badlogicgames@gmail.com
 * @author phil@happydroids.com
 */
public class GridPoint implements Serializable {
  /**
   * Static temporary vector. Use with care! Use only when sure other code will not also use this.
   *
   * @see #tmp() *
   */
  public final static GridPoint TMP = new GridPoint();
  public final static GridPoint X = new GridPoint(1, 0);
  public final static GridPoint Y = new GridPoint(0, 1);
  public final static GridPoint ZERO = new GridPoint(0, 0);

  /**
   * the x-component of this vector *
   */
  public int x;
  /**
   * the y-component of this vector *
   */
  public int y;

  /**
   * Constructs a new vector at (0,0)
   */
  public GridPoint() {

  }

  /**
   * Constructs a vector with the given components
   *
   * @param x The x-component
   * @param y The y-component
   */
  public GridPoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Constructs a vector from the given vector
   *
   * @param v The vector
   */
  public GridPoint(GridPoint v) {
    set(v);
  }

  /**
   * @return a copy of this vector
   */
  public GridPoint cpy() {
    return new GridPoint(this);
  }

  /**
   * @return The euclidian length
   */
  public int len() {
    return (int) Math.sqrt(x * x + y * y);
  }

  /**
   * @return The squared euclidian length
   */
  public int len2() {
    return x * x + y * y;
  }

  /**
   * Sets this vector from the given vector
   *
   * @param v The vector
   * @return This vector for chaining
   */
  public GridPoint set(GridPoint v) {
    x = v.x;
    y = v.y;
    return this;
  }

  /**
   * Sets the components of this vector
   *
   * @param x The x-component
   * @param y The y-component
   * @return This vector for chaining
   */
  public GridPoint set(int x, int y) {
    this.x = x;
    this.y = y;
    return this;
  }

  /**
   * Substracts the given vector from this vector.
   *
   * @param v The vector
   * @return This vector for chaining
   */
  public GridPoint sub(GridPoint v) {
    x -= v.x;
    y -= v.y;
    return this;
  }

  /**
   * Normalizes this vector
   *
   * @return This vector for chaining
   */
  public GridPoint nor() {
    int len = len();
    if (len != 0) {
      x /= len;
      y /= len;
    }
    return this;
  }

  /**
   * Adds the given vector to this vector
   *
   * @param v The vector
   * @return This vector for chaining
   */
  public GridPoint add(GridPoint v) {
    x += v.x;
    y += v.y;
    return this;
  }

  /**
   * Adds the given components to this vector
   *
   * @param x The x-component
   * @param y The y-component
   * @return This vector for chaining
   */
  public GridPoint add(int x, int y) {
    this.x += x;
    this.y += y;
    return this;
  }

  /**
   * @param v The other vector
   * @return The dot product between this and the other vector
   */
  public int dot(GridPoint v) {
    return x * v.x + y * v.y;
  }

  /**
   * Multiplies this vector by a scalar
   *
   * @param scalar The scalar
   * @return This vector for chaining
   */
  public GridPoint mul(int scalar) {
    x *= scalar;
    y *= scalar;
    return this;
  }

  /**
   * @param v The other vector
   * @return the distance between this and the other vector
   */
  public int dst(GridPoint v) {
    final int x_d = v.x - x;
    final int y_d = v.y - y;
    return (int) Math.sqrt(x_d * x_d + y_d * y_d);
  }

  /**
   * @param x The x-component of the other vector
   * @param y The y-component of the other vector
   * @return the distance between this and the other vector
   */
  public int dst(int x, int y) {
    final int x_d = x - this.x;
    final int y_d = y - this.y;
    return (int) Math.sqrt(x_d * x_d + y_d * y_d);
  }

  /**
   * @param v The other vector
   * @return the squared distance between this and the other vector
   */
  public int dst2(GridPoint v) {
    final int x_d = v.x - x;
    final int y_d = v.y - y;
    return x_d * x_d + y_d * y_d;
  }

  /**
   * @param x The x-component of the other vector
   * @param y The y-component of the other vector
   * @return the squared distance between this and the other vector
   */
  public int dst2(int x, int y) {
    final int x_d = x - this.x;
    final int y_d = y - this.y;
    return x_d * x_d + y_d * y_d;
  }

  public String toString() {
    return "[" + x + ":" + y + "]";
  }

  /**
   * Substracts the other vector from this vector.
   *
   * @param x The x-component of the other vector
   * @param y The y-component of the other vector
   * @return This vector for chaining
   */
  public GridPoint sub(int x, int y) {
    this.x -= x;
    this.y -= y;
    return this;
  }

  /**
   * NEVER EVER SAVE THIS REFERENCE! Do not use this unless you are aware of the side-effects, e.g. other methods might call this
   * as well.
   *
   * @return a temporary copy of this vector. Use with care as this is backed by a single static Vector2 instance. v1.TMP().add(
   *         v2.TMP() ) will not work!
   */
  public GridPoint tmp() {
    return TMP.set(this);
  }

  /**
   * Multiplies this vector by the given matrix
   *
   * @param mat the matrix
   * @return this vector
   */
  public GridPoint mul(Matrix3 mat) {
    int x = (int) (this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6]);
    int y = (int) (this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7]);
    this.x = x;
    this.y = y;
    return this;
  }

  /**
   * Calculates the 2D cross product between this and the given vector.
   *
   * @param v the other vector
   * @return the cross product
   */
  public int crs(GridPoint v) {
    return this.x * v.y - this.y * v.x;
  }

  /**
   * Calculates the 2D cross product between this and the given vector.
   *
   * @param x the x-coordinate of the other vector
   * @param y the y-coordinate of the other vector
   * @return the cross product
   */
  public int crs(int x, int y) {
    return this.x * y - this.y * x;
  }

  /**
   * @return the angle in degrees of this vector (point) relative to the x-axis. Angles are counter-clockwise and between 0 and
   *         360.
   */
  public int angle() {
    int angle = (int) (Math.atan2(y, x) * MathUtils.radiansToDegrees);
    if (angle < 0) {
      angle += 360;
    }
    return angle;
  }

  /**
   * Rotates the Vector2 by the given angle, counter-clockwise.
   *
   * @param angle the angle in degrees
   * @return the
   */
  public GridPoint rotate(int angle) {
    int rad = (int) (angle * MathUtils.degreesToRadians);
    int cos = (int) Math.cos(rad);
    int sin = (int) Math.sin(rad);

    int newX = this.x * cos - this.y * sin;
    int newY = this.x * sin + this.y * cos;

    this.x = newX;
    this.y = newY;

    return this;
  }

  /**
   * Linearly interpolates between this vector and the target vector by alpha which is in the range [0,1]. The result is stored
   * in this vector.
   *
   * @param target The target vector
   * @param alpha  The interpolation coefficient
   * @return This vector for chaining.
   */
  public GridPoint lerp(GridPoint target, int alpha) {
    GridPoint r = this.mul(1 - alpha);
    r.add(target.tmp().mul(alpha));
    return r;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    GridPoint other = (GridPoint) obj;
    return x == other.x && y == other.y;
  }

  /**
   * Compares this vector with the other vector, using the supplied
   * epsilon for fuzzy equality testing.
   *
   * @return whether the vectors are the same.
   */
  public boolean epsilonEquals(GridPoint obj, int epsilon) {
    return obj != null && Math.abs(obj.x - x) <= epsilon && Math.abs(obj.y - y) <= epsilon;
  }

  @JsonIgnore
  public float getWorldX() {
    return GRID_UNIT_SIZE * x;
  }

  @JsonIgnore
  public float getWorldY() {
    return GRID_UNIT_SIZE * y;
  }
}

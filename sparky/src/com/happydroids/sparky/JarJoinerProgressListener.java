/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

public abstract class JarJoinerProgressListener {
  public abstract void run(int numEntriesProcessed, int numTotalEntries);
}

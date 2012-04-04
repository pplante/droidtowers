/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerGameTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.unhappyrobot.Expect.expect;

@RunWith(TowerGameTestRunner.class)
public class HappyDroidServiceCollectionTest {

  private HappyDroidServiceCollection collection;

  @Before
  public void setUp() throws Exception {
    collection = new GameUpdateCollection();
  }

  @Test
  public void getBaseResourceUri_shouldReflectivelyLookUpTheResourceUriFromClass() {
    expect(collection.getBaseResourceUri()).toContain("/api/v1/gamestate/");
  }

  @Test
  public void fetch_shouldWorkWhenResponseCodeIs200() {
//    collection.fetch(new ApiRunnable<HappyDroidServiceCollection>() {
//      @Override
//      public void onSuccess(HttpResponse response, HappyDroidServiceCollection object) {
//
//      }
//    }, );
  }
}

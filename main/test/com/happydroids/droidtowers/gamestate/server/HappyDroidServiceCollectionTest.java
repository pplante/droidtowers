/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.happydroids.droidtowers.NonGLTestRunner;
import com.happydroids.server.GameUpdateCollection;
import com.happydroids.server.HappyDroidServiceCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.happydroids.droidtowers.Expect.expect;


@RunWith(NonGLTestRunner.class)
public class HappyDroidServiceCollectionTest {

  private HappyDroidServiceCollection collection;

  @Before
  public void setUp() throws Exception {
    collection = new GameUpdateCollection();
  }

  @Test
  public void getBaseResourceUri_shouldReflectivelyLookUpTheResourceUriFromClass() {
    expect(collection.getBaseResourceUri()).toContain("/api/v1/gameupdate/");
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

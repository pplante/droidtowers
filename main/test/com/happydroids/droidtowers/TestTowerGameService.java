/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.happydroids.HttpTestHelper;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import org.apache.http.HttpResponse;

import java.util.HashMap;

class TestTowerGameService extends TowerGameService {
  public TestTowerGameService() {
    super();
    hasNetworkConnection = true;
  }

  @Override
  public HttpResponse makeGetRequest(String uri, HashMap<String, String> queryParams) {
    return HttpTestHelper.instance().makeGetRequest(uri);
  }

  @Override
  public HttpResponse makePostRequest(String uri, Object forServerDoNotCare) {
    return HttpTestHelper.instance().makePostRequest(uri, forServerDoNotCare);
  }

  public static void disableNetworkConnection() {
    hasNetworkConnection = false;
  }

  @Override
  public void withNetworkConnection(Runnable runnable) {
    if (hasNetworkConnection) {
      runnable.run();
    }
  }
}

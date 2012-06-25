/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.happydroids.HttpTestHelper;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.platform.Platform;
import com.happydroids.platform.PlatformConnectionMonitor;
import org.apache.http.HttpResponse;

import java.util.HashMap;

class TestTowerGameService extends TowerGameService {
  public TestTowerGameService() {
    super();
    Platform.setConnectionMonitor(new PlatformConnectionMonitor() {
      @Override
      public boolean isConnectedOrConnecting() {
        return true;
      }
    });
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
    Platform.setConnectionMonitor(new PlatformConnectionMonitor() {
      @Override
      public boolean isConnectedOrConnecting() {
        return false;
      }
    });
  }

}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.happydroids.HttpTestHelper;
import com.happydroids.platform.Platform;
import com.happydroids.platform.PlatformConnectionMonitor;
import org.apache.http.HttpResponse;

import java.util.HashMap;

public class TestHappyDroidService extends HappyDroidService {
  public TestHappyDroidService() {
    super(13);

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

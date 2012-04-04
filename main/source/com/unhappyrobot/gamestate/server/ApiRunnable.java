/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class ApiRunnable<T extends HappyDroidServiceObject> {
  public void onSuccess(HttpResponse response, T object) {
  }

  public void onError(HttpResponse response, int statusCode, T object) {
  }

  void handleResponse(HttpResponse response, T object) {
    boolean handled = false;

    if (response != null) {
      StatusLine statusLine = response.getStatusLine();
      if (statusLine != null) {
        switch (statusLine.getStatusCode()) {
          case 200:
          case 201:
          case 204:
            onSuccess(response, object);
            break;
          default:
            onError(response, statusLine.getStatusCode(), object);
            break;
        }

        handled = true;
      }
    }

    if (!handled) {
      onError(response, HttpStatusCode.Unknown, object);
    }
  }
}

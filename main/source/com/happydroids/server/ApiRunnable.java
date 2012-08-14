/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.happydroids.HappyDroidConsts;
import org.apach3.http.HttpResponse;
import org.apach3.http.StatusLine;
import org.apach3.http.util.EntityUtils;

import java.io.IOException;

public class ApiRunnable<T extends HappyDroidServiceObject> {
  public void onSuccess(HttpResponse response, T object) {
    if (HappyDroidConsts.DEBUG) {
      System.out.println("Object: " + object);
    }
  }

  public void onError(HttpResponse response, int statusCode, T object) {
    if (HappyDroidConsts.DEBUG) {
      System.out.println("HTTP ERR: " + statusCode);
      if (response != null) {
        try {
          System.out
                  .println("HTTP RES: " + response.getStatusLine() + "\n" + EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
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

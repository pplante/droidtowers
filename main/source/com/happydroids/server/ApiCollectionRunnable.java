/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.happydroids.HappyDroidConsts;
import org.apach3.http.HttpResponse;
import org.apach3.http.StatusLine;

public class ApiCollectionRunnable<CollectionType extends HappyDroidServiceCollection<? extends HappyDroidServiceObject>> {
  public void onSuccess(HttpResponse response, CollectionType collection) {
    if (HappyDroidConsts.DEBUG) {
      System.out.println("Collection: " + collection);
    }
  }

  public void onError(HttpResponse response, int statusCode, CollectionType collection) {
    if (HappyDroidConsts.DEBUG) {
      System.out.println("HTTP ERR: " + statusCode);
    }
  }

  void handleResponse(HttpResponse response, CollectionType collection) {
    boolean handled = false;

    if (response != null) {
      StatusLine statusLine = response.getStatusLine();
      if (statusLine != null) {
        switch (statusLine.getStatusCode()) {
          case 200:
          case 201:
          case 204:
            onSuccess(response, collection);
            break;
          default:
            onError(response, statusLine.getStatusCode(), collection);
            break;
        }

        handled = true;
      }
    }

    if (!handled) {
      onError(response, HttpStatusCode.Unknown, collection);
    }
  }
}

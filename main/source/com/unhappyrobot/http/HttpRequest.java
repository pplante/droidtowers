package com.unhappyrobot.http;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequest {
  private static final String TAG = HttpRequest.class.getSimpleName();

  protected static HttpRequest instance;

  public static HttpRequest instance() {
    if (instance == null) {
      instance = new HttpRequest();
    }

    return instance;
  }

  public static void setInstance(HttpRequest inst) {
    instance = inst;
  }


  public static enum REQUEST_TYPE {
    GET,
    POST

  }

  public static HttpResponse makeRequest(REQUEST_TYPE type, String urlString) throws IOException {
    switch (type) {
      case GET:
        return instance().getRequest(urlString);
      case POST:
        break;
    }

    return null;
  }

  protected HttpResponse getRequest(String urlString) throws IOException {
    if (urlString == null) {
      return null;
    }
    try {
      Gdx.app.log(TAG, "GET: "+ urlString);
      URL url = new URL(urlString);
      URLConnection urlConnection = url.openConnection();
      int contentLength = urlConnection.getContentLength();
      InputStream inputStream = urlConnection.getInputStream();

      if (contentLength == -1) {
        contentLength = inputStream.available();
      }

      try {
        byte[] data = new byte[contentLength];
        int offset = 0;

        while (offset < contentLength) {
          int bytesRead = inputStream.read(data, offset, data.length - offset);
          if (bytesRead < 0) {
            break;
          }

          offset += bytesRead;
        }

        if (offset < contentLength) {
          throw new IOException(String.format("Read %d bytes, expected %d", offset, contentLength));
        }

        return new HttpResponse(data);
      } finally {
        inputStream.close();
      }
    } catch (Exception ignored) {

    }

    return null;
  }

}

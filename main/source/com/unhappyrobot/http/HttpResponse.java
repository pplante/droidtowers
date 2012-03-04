package com.unhappyrobot.http;

public class HttpResponse {
  private byte[] body;

  public HttpResponse(byte[] data) {
    body = data;
  }

  public HttpResponse(String data) {
    body = data.getBytes();
  }

  public byte[] getBody() {
    return body;
  }

  public String getBodyString() {
    return new String(body);
  }
}

package com.unhappyrobot;

import org.omg.CORBA.Request;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequest {
    public static enum REQUEST_TYPE {
        GET,
        POST
    }


    public HttpRequest() {
    }

    public static HttpResponse makeRequest(REQUEST_TYPE type, URL url) throws IOException {
        switch (type) {
            case GET:
                return new HttpRequest().getRequest(url);
            case POST:
                break;
        }

        return null;
    }

    HttpResponse getRequest(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        int contentLength = urlConnection.getContentLength();

        InputStream inputStream = urlConnection.getInputStream();
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

            HttpResponse httpResponse = new HttpResponse();
            httpResponse.body = data;
            return httpResponse;
        } finally {
            inputStream.close();
        }
    }

    public class HttpResponse {
        public byte[] getBody() {
            return body;
        }

        public String getBodyString() {
            return new String(body);
        }

        private byte[] body;
    }
}

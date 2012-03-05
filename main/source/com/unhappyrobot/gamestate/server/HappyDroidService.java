package com.unhappyrobot.gamestate.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.unhappyrobot.gamestate.GameSave;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class HappyDroidService {
  public static String uploadGameSave(GameSave gameSave) {
    String gameSaveUri = Consts.API_V1_GAMESAVE_LIST;
    if (gameSave.getCloudSaveUri() != null) {
      gameSaveUri = Consts.HAPPYDROIDS_SERVER + gameSave.getCloudSaveUri();
    }

    HttpResponse response = makePostRequest(gameSaveUri, new CloudGameSave(gameSave));
    if (response != null) {
      Header location = Iterables.getFirst(Lists.newArrayList(response.getHeaders("Location")), null);
      if (location != null) {
        return location.toString();
      }
    }
    return null;
  }

  private static HttpResponse makePostRequest(String uri, Object objectForServer) {
    HttpClient client = new DefaultHttpClient();
    try {
      HttpPost request = new HttpPost(uri);
      request.setHeader("Content-Type", "application/json");
      request.setHeader("X-Token", getSessionToken());

      ObjectMapper mapper = new ObjectMapper();
      request.setEntity(new StringEntity(mapper.writeValueAsString(objectForServer), ContentType.MULTIPART_FORM_DATA));
      HttpResponse response = client.execute(request);
      EntityUtils.consume(response.getEntity());

      return response;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.getConnectionManager().shutdown();
    }

    return null;
  }

  public static String getSessionToken() {
    Preferences connect = Gdx.app.getPreferences("CONNECT");

    return connect.getString("SESSION_TOKEN");
  }
}

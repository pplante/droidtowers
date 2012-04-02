package com.unhappyrobot.gamestate.server;

import java.util.List;

public abstract class HappyDroidServiceCollection<ApiType extends HappyDroidServiceObject> {
  private Metadata meta;
  private List<ApiType> objects;

  public HappyDroidServiceCollection() {
    objects = null;
  }

  /*
    public void fetch(final ApiCollectionRunnable apiRunnable, final HappyDroidServiceCollection<ApiType> clazz) {
      HappyDroidService.instance().withNetworkConnection(new Runnable() {
        public void run() {
          HttpResponse response = HappyDroidService.instance().makeGetRequest(getBaseResourceUri());

          if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
            mapper.getTypeFactory().constructCollectionType(ArrayList.class, Achievement.class)
              collection = objectMapper.readValue(new BufferedHttpEntity(response.getEntity()).getContent(), clazz);

            } catch (IOException e) {
              e.printStackTrace();
            }
          }

          apiRunnable.handleResponse(response, collection);
        }
      });
    }
  */
  protected boolean requireAuthentication() {
    if (objects != null && !objects.isEmpty()) {
      return objects.get(0).requireAuthentication();
    }

    throw new RuntimeException("Cannot call requireAuthentication() before providing objects.");
  }

  public String getBaseResourceUri() {
    if (objects != null && !objects.isEmpty()) {
      return objects.get(0).getBaseResourceUri();
    }

    throw new RuntimeException("Cannot call getBaseResourceUri() before providing objects.");
  }

  private class Metadata {
    public int limit;
    public int offset;
    public int totalCount;
    public String next;
    public String previous;
  }
}

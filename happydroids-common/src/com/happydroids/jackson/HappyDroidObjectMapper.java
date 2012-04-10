/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.jackson;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.happydroids.server.StackTraceSerializer;

public class HappyDroidObjectMapper extends ObjectMapper {
  protected final SimpleModule happyModules;

  public HappyDroidObjectMapper() {
    super();

    happyModules = new SimpleModule();
    happyModules.addSerializer(new StackTraceSerializer());
    registerModule(happyModules);
  }

  public void addSerializer(JsonSerializer serializer) {
    happyModules.addSerializer(serializer);
    registerModule(happyModules);
  }

  public void addDeserializer(Class<Class> clazz, JsonDeserializer<Class> deserializer) {
    happyModules.addDeserializer(clazz, deserializer);
    registerModule(happyModules);
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.happydroids.droidtowers.utils.ClassNameResolver;

import java.io.IOException;

public class TowerGameClassDeserializer extends JsonDeserializer<Class> {
  @Override
  public Class deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    return ClassNameResolver.resolveClass(jp.getText().trim());
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.jackson;

import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class Vector3Serializer extends JsonSerializer<Vector3> {
  @Override
  public void serialize(Vector3 vector3, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeNumberField("x", vector3.x);
    jsonGenerator.writeNumberField("y", vector3.y);
    jsonGenerator.writeNumberField("z", vector3.z);
    jsonGenerator.writeEndObject();
  }

  @Override
  public Class<Vector3> handledType() {
    return Vector3.class;
  }
}

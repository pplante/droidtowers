package com.unhappyrobot.json;

import com.badlogic.gdx.math.Vector3;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

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

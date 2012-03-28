package com.unhappyrobot.gamestate.server;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class StackTraceSerializer extends JsonSerializer<StackTraceElement> {
  @Override
  public void serialize(StackTraceElement stackTraceElement, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("declaringClass", stackTraceElement.getClassName());
    jsonGenerator.writeStringField("methodName", stackTraceElement.getMethodName());
    jsonGenerator.writeStringField("fileName", stackTraceElement.getFileName());
    jsonGenerator.writeNumberField("lineNumber", stackTraceElement.getLineNumber());
    jsonGenerator.writeEndObject();
  }

  @Override
  public Class<StackTraceElement> handledType() {
    return StackTraceElement.class;
  }
}

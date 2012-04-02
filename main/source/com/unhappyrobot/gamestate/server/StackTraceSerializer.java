package com.unhappyrobot.gamestate.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

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

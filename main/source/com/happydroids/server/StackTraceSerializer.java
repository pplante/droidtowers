/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StackTraceSerializer extends JsonSerializer<StackTraceElement> {
  @Override
  public void serialize(StackTraceElement stackTraceElement, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
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

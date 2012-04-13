/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package sk.seges.acris.json.server.migrate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Method;

/**
 * Transforms JSON data using Jackson's tree model based scripts.
 *
 * @author ladislav.gazo
 * @see sk.seges.acris.json.server.migrate.JacksonTransformationScript
 */
public class JacksonTransformer extends Transformer<String> {
  public JacksonTransformer(InputStream inputStream) throws ScriptException, FileNotFoundException {
    super(inputStream);
  }

  @Override
  public byte[] transform(Class<? extends JacksonTransformationScript> transformationClass, ByteArrayInputStream input) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode jsonNode = mapper.readValue(input, JsonNode.class);

      Object transformation = transformationClass.newInstance();
      Method method = transformation.getClass().getMethod("execute", JsonNode.class);
      method.invoke(transformation, jsonNode);

      return mapper.writeValueAsBytes(jsonNode);
    } catch (Exception e) {
      throw new RuntimeException("Unable to transform data using transformationClass = " + transformationClass, e);
    }
  }

  @Override
  public byte[] process() throws IOException {
    byte bytes[] = new byte[inputStream.available()];
    IOUtils.readFully(inputStream, bytes);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    for (Class<? extends JacksonTransformationScript> transformClass : transforms) {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      bytes = transform(transformClass, input);
    }

    return bytes;
  }
}

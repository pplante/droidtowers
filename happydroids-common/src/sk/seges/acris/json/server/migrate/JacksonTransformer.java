/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package sk.seges.acris.json.server.migrate;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.script.ScriptException;
import java.io.*;

/**
 * Transforms JSON data using Jackson's tree model based scripts.
 *
 * @author ladislav.gazo
 * @see sk.seges.acris.json.server.migrate.JacksonTransformationScript
 */
public class JacksonTransformer extends Transformer<String> {
  private static final String TAG = JacksonTransformer.class.getSimpleName();
  private static final int EOF = -1;

  private final String fileName;

  public JacksonTransformer(InputStream inputStream, String fileName) throws ScriptException, FileNotFoundException {
    super(inputStream);
    this.fileName = fileName;
  }

  @SuppressWarnings("unchecked")
  @Override
  public byte[] transform(Class<? extends JacksonTransformationScript> transformationClass, ByteArrayInputStream input) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode jsonNode = mapper.readValue(input, JsonNode.class);
      Gdx.app.debug(TAG, "Executing migration: " + transformationClass.getSimpleName());
      JacksonTransformationScript transformation = transformationClass.newInstance();

      transformation.process(jsonNode, fileName);

      return mapper.writeValueAsBytes(jsonNode);
    } catch (Exception e) {
      throw new RuntimeException("Unable to transform data using transformationClass = " + transformationClass, e);
    }
  }

  @Override
  public byte[] process() throws IOException {
    byte bytes[] = new byte[inputStream.available()];
    readFully(inputStream, bytes);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    for (Class<? extends JacksonTransformationScript> transformClass : transforms) {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      bytes = transform(transformClass, input);
    }

    return bytes;
  }

  private void readFully(InputStream inputStream, byte[] bytes) throws IOException {
    int length = bytes.length;
    int remaining = length;

    while (remaining > 0) {
      int location = length - remaining;
      int count = inputStream.read(bytes, location, remaining);
      if (EOF == count) { // EOF
        break;
      }
      remaining -= count;
    }
  }
}

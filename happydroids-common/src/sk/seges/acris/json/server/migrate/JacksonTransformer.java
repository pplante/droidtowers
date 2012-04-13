/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package sk.seges.acris.json.server.migrate;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * Transforms JSON data using Jackson's tree model based scripts.
 *
 * @author ladislav.gazo
 * @see sk.seges.acris.json.server.migrate.JacksonTransformationScript
 */
public class JacksonTransformer extends Transformer<String> {
  public JacksonTransformer(InputStream inputStream, OutputStream outputStream) throws ScriptException, FileNotFoundException {
    super(inputStream, outputStream);
  }

  @Override
  public void transform(Class<? extends JacksonTransformationScript> transformationClass) {

    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode jsonNode = mapper.readValue(inputStream, JsonNode.class);

      Object transformation = transformationClass.newInstance();
      Method method = transformation.getClass().getMethod("execute", JsonNode.class);
      method.invoke(transformation, jsonNode);


      DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
      pp.spacesInObjectEntries(false);
      pp.indentArraysWith(new DefaultPrettyPrinter.Lf2SpacesIndenter());
      pp.indentObjectsWith(new DefaultPrettyPrinter.Lf2SpacesIndenter());
      mapper.writer().with(pp).writeValue(outputStream, jsonNode);
    } catch (Exception e) {
      throw new RuntimeException("Unable to transform data using transformationClass = " + transformationClass, e);
    }
  }
}

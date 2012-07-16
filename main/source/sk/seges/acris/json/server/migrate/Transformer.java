/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

/**
 *
 */
package sk.seges.acris.json.server.migrate;

import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class containing logic for every transformer written.
 *
 * @param <T> Represents type of transformation used (e.g. File with
 *            transformation script or String with transformation script class)
 * @author ladislav.gazo
 */
public abstract class Transformer<T> {
  protected final InputStream inputStream;
  protected List<Class<? extends JacksonTransformationScript>> transforms;

  public Transformer(InputStream inputStream) throws ScriptException, FileNotFoundException {
    this.inputStream = inputStream;
    transforms = new ArrayList<Class<? extends JacksonTransformationScript>>();
  }

  public abstract byte[] transform(Class<? extends JacksonTransformationScript> transformationClass, ByteArrayInputStream input);

  public void addTransform(Class<? extends JacksonTransformationScript> clazz) {
    transforms.add(clazz);
  }

  public abstract byte[] process() throws IOException;
}

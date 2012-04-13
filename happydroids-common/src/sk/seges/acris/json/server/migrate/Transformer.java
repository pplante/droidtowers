/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

/**
 *
 */
package sk.seges.acris.json.server.migrate;

import javax.script.ScriptException;
import java.io.*;

/**
 * Class containing logic for every transformer written.
 *
 * @author ladislav.gazo
 *
 * @param <T>
 *            Represents type of transformation used (e.g. File with
 *            transformation script or String with transformation script class)
 */
public abstract class Transformer<T> {
	protected final InputStream inputStream;
	protected final OutputStream outputStream;

	public Transformer(InputStream inputStream, OutputStream outputStream) throws ScriptException, FileNotFoundException {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public abstract void transform(Class<? extends JacksonTransformationScript> transformationClass);
}

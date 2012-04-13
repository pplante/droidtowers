/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.migrations;

import com.happydroids.TestHelper;
import com.happydroids.droidtowers.NonGLTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import sk.seges.acris.json.server.migrate.JacksonTransformer;

import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(NonGLTestRunner.class)
public class Migration_GameSave_UnhappyrobotToDroidTowersTest {
  private File input;
  private File expectedOutput;

  @Before
  public void setUp() throws IOException {
    input = TestHelper.fixture("migrations/gamesave_unhappyrobot_to_happydroids_package_rename_input.json");
    expectedOutput = TestHelper.fixture("migrations/gamesave_unhappyrobot_to_happydroids_package_rename_output.json");
  }

  @Test
  public void process_shouldTranslatePackageNames() throws IOException, ScriptException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JacksonTransformer transformer = new JacksonTransformer(new FileInputStream(input));
//    transformer.transform(Migration_GameSave_UnhappyrobotToDroidTowers.class, input);

    expect(TestHelper.readJson(outputStream.toString())).toEqual(TestHelper.readJson(expectedOutput));
  }
}

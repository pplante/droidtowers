/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.migrations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import sk.seges.acris.json.server.migrate.JacksonTransformationScript;

public class Migration_GameSave_RemoveObjectCounts extends JacksonTransformationScript<ObjectNode> {
  @Override
  protected void process(ObjectNode node, String fileName) {
    ObjectNode gameSaveNode = getGameSaveUnlessFileFormatIsNewer(node, "com.happydroids.droidtowers.gamestate.GameSave", 2);
    if (gameSaveNode == null) {
      return;
    }

    gameSaveNode.remove("objectCounts");
    gameSaveNode.remove("neighborhoodUri");
    gameSaveNode.put("fileFormat", 3);

    node.removeAll();
    node.put("com.happydroids.droidtowers.gamestate.GameSave", gameSaveNode);
  }
}

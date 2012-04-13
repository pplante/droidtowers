/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.migrations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sk.seges.acris.json.server.migrate.JacksonTransformationScript;

public class Migration_GameSave_UnhappyrobotToDroidTowers extends JacksonTransformationScript<ObjectNode> {
  @Override
  protected void process(ObjectNode node) {
    ObjectNode gameSaveNode = (ObjectNode) node.findValue("com.unhappyrobot.gamestate.GameSave");

    if (gameSaveNode == null || gameSaveNode.has("fileFormat")) return;

    ArrayNode gridObjects = gameSaveNode.withArray("gridObjects");
    for (JsonNode gridObjectNode : gridObjects) {
      ObjectNode gridObject = (ObjectNode) gridObjectNode;
      gridObject.put("typeId", transformTypeNameToTypeId(gridObject));
      gridObject.remove("typeClass");
      gridObject.remove("typeName");
    }

    gameSaveNode.remove("objectCounts");

    gameSaveNode.put("gridObjects", gridObjects);

    node.removeAll();

    gameSaveNode.put("fileFormat", 1);

    node.put("GameSave", gameSaveNode);
  }

  private String transformTypeNameToTypeId(ObjectNode gridObject) {
    String typeName = gridObject.get("typeName").asText().replaceAll(" ", "-");

    if (typeName.equalsIgnoreCase("LOBBY-4X1")) {
      typeName = "LOBBY";
    } else if (typeName.equalsIgnoreCase("ELEVATOR-SHAFT")) {
      typeName = "ELEVATOR";
    }

    return typeName.toUpperCase();
  }
}

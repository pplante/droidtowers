/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.migrations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sk.seges.acris.json.server.migrate.JacksonTransformationScript;

public class Migration_GameSave_SwitchToGenericTypeInfo extends JacksonTransformationScript<ObjectNode> {
  @Override
  protected void process(ObjectNode node) {
    ObjectNode gameSaveNode = (ObjectNode) node.findValue("com.happydroids.droidtowers.gamestate.GameSave");
    if (gameSaveNode == null) return;
    JsonNode fileFormat = gameSaveNode.findValue("fileFormat");
    if (fileFormat != null && fileFormat.asInt() >= 2) return;

    ArrayNode gridObjects = gameSaveNode.withArray("gridObjects");
    for (JsonNode gridObjectNode : gridObjects) {
      ObjectNode gridObject = (ObjectNode) gridObjectNode;
      if (gridObject == null) {
        throw new RuntimeException("Error converting: " + gridObject);
      } else if (!gridObject.has("typeId")) {
        gridObject.put("typeId", transformTypeNameToTypeId(gridObject));
        gridObject.remove("typeClass");
        gridObject.remove("typeName");
      }

      String typeId = gridObject.get("typeId").asText();
      if (typeId.equalsIgnoreCase("MAIN-LOBBY") || typeId.equalsIgnoreCase("LOBBY")) {
        gridObject.put("typeId", "GROUND-FLOOR-LOBBY");
      } else if (typeId.equalsIgnoreCase("SUSHI")) {
        gridObject.put("typeId", "SUSHI-PLACE");
      } else {
        gridObject.put("typeId", typeId.replaceAll("_", "-"));
      }
    }

    gameSaveNode.remove("objectCounts");

    gameSaveNode.put("gridObjects", gridObjects);

    node.removeAll();

    gameSaveNode.put("fileFormat", 2);

    node.put("GameSave", gameSaveNode);
  }

  private String transformTypeNameToTypeId(ObjectNode gridObject) {
    String typeName = gridObject.get("typeName").asText().replaceAll(" ", "-");

    if (typeName.equalsIgnoreCase("MAIN-LOBBY")) {
      typeName = "GROUND-FLOOR-LOBBY";
    } else if (typeName.equalsIgnoreCase("ELEVATOR-SHAFT")) {
      typeName = "ELEVATOR";
    }

    return typeName.toUpperCase();
  }
}

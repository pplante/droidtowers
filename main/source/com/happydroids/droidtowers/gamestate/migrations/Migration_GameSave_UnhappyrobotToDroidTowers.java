/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.migrations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.GridObjectTypeFactory;
import sk.seges.acris.json.server.migrate.JacksonTransformationScript;

import java.util.HashMap;
import java.util.Map;

public class Migration_GameSave_UnhappyrobotToDroidTowers extends JacksonTransformationScript<ObjectNode> {
  private static final HashMap<String, String> typeNameMap = new HashMap<String, String>();

  static {
    typeNameMap.put("MAIDS-OFFICE", "MAIDS-CLOSET");
    typeNameMap.put("JANITORS-OFFICE", "JANITORS-CLOSET");
    typeNameMap.put("SUSHI", "SUSHI-PLACE");
    typeNameMap.put("MAIN-LOBBY", "GROUND-FLOOR-LOBBY");
    typeNameMap.put("LOBBY-4X1", "GROUND-FLOOR-LOBBY");
    typeNameMap.put("ELEVATOR-SHAFT", "ELEVATOR");
  }

  @Override
  protected void process(ObjectNode node, String fileName) {
    ObjectNode gameSaveNode = getGameSaveUnlessFileFormatIsNewer(node, "com.unhappyrobot.gamestate.GameSave", 1);
    if (gameSaveNode == null) {
      return;
    }

    ArrayNode gridObjects = gameSaveNode.withArray("gridObjects");
    for (JsonNode gridObjectNode : gridObjects) {
      ObjectNode gridObject = (ObjectNode) gridObjectNode;
      if (gridObject == null) {
        throw new RuntimeException("Error converting: " + gridObject);
      } else if (!gridObject.has("typeId")) {
        String typeName = gridObject.get("typeName").asText();
        String typeId = transformTypeNameToTypeId(typeName);

        gridObject.put("typeId", typeId);
        gridObject.remove("typeClass");
        gridObject.remove("typeName");
      }
    }

    gameSaveNode.remove("objectCounts");

    gameSaveNode.put("gridObjects", gridObjects);

    node.removeAll();

    if (!gameSaveNode.has("baseFilename")) {
      gameSaveNode.put("baseFilename", fileName);
    }

    if (!gameSaveNode.has("towerName")) {
      gameSaveNode.put("towerName", "Untitled Tower");
    }

    gameSaveNode.put("fileFormat", 2);

    node.put("GameSave", gameSaveNode);
  }

  private String transformTypeNameToTypeId(String typeName) {
    typeName = typeName.replaceAll(" ", "-").toUpperCase();

    for (Map.Entry<String, String> entry : typeNameMap.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(typeName)) {
        typeName = entry.getValue();
      }
    }

    GridObjectType objectType = GridObjectTypeFactory.findTypeById(typeName);
    if (objectType == null) {
      throw new RuntimeException("Could not convert: " + typeName);
    }

    return typeName;
  }
}

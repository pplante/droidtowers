/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.migrations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happydroids.droidtowers.DifficultyLevel;
import com.happydroids.droidtowers.gamestate.GameSaveMetadata;
import sk.seges.acris.json.server.migrate.JacksonTransformationScript;

public class Migration_GameSave_MoveMetadata extends JacksonTransformationScript<ObjectNode> {
  @Override
  protected void process(ObjectNode node, String fileName) {
    ObjectNode gameSaveNode = getGameSaveUnlessFileFormatIsNewer(node, "com.happydroids.droidtowers.gamestate.GameSave", 3);
    if (gameSaveNode == null) {
      return;
    }

    GameSaveMetadata metadata = new GameSaveMetadata();
    metadata.towerName = gameSaveNode.remove("towerName").asText();
    metadata.difficultyLevel = DifficultyLevel.valueOf(gameSaveNode.remove("difficultyLevel").asText());
    metadata.cloudSaveUri = gameSaveNode.remove("cloudSaveUri").asText();
    metadata.baseFilename = gameSaveNode.remove("baseFilename").asText();
    metadata.fileGeneration = gameSaveNode.remove("fileGeneration").asInt();

    gameSaveNode.remove("neighborhoodUri");
    gameSaveNode.putPOJO("metadata", metadata);
    gameSaveNode.put("fileFormat", 4);

    node.removeAll();
    node.put("com.happydroids.droidtowers.gamestate.GameSave", gameSaveNode);
  }
}

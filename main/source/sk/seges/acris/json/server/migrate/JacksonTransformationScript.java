/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package sk.seges.acris.json.server.migrate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Arrays;

/**
 * Template class for all Jackson-based transformation scripts. Contains various
 * helper methods to ease writing scripts.
 *
 * @param <T> Root node type
 * @author ladislav.gazo
 */
public abstract class JacksonTransformationScript<T extends JsonNode> {
  protected abstract void process(T node, String fileName);

  /**
   * Renames field in an object node.
   *
   * @param parent   Object node where to rename the field.
   * @param srcField Original field name.
   * @param dstField Desired field name.
   */
  protected void rename(ObjectNode parent, String srcField, String dstField) {
    JsonNode value = parent.path(srcField);
    parent.put(dstField, value);
    parent.remove(srcField);
  }

  /**
   * Adds child node to a parent in case it does not exist already. New child
   * is taken as a childPrototype parameter. In case it exists, nothing
   * happens.
   *
   * @param <N>            Type of the child node.
   * @param parent         Parent node where to look for the field.
   * @param field          Field name in object node.
   * @param childPrototype Prepared instance of a child node in case it doesn't exist.
   * @return If the field exists in the parent, the node for the field is
   *         returned, otherwise childPrototype is returned.
   */
  @SuppressWarnings("unchecked")
  protected <N extends JsonNode> N addNonExistent(ObjectNode parent, String field, N childPrototype) {
    JsonNode child = parent.get(field);
    if (child != null && !childPrototype.getClass().getName().equals(child.getClass().getName())) {
      throw new RuntimeException("Field " + field + " exists and its class " + child.getClass().getName() + " is not same as expected " + childPrototype.getClass().getName() + ", cannot continue");
    } else if (child != null) {
      return (N) child;
    } else if (child == null) {
      parent.put(field, childPrototype);
    }

    return childPrototype;
  }

  /**
   * @see {@link #addNonExistent}
   */
  protected ObjectNode addNonExistentObjectNode(ObjectNode parent, String field) {
    return addNonExistent(parent, field, parent.objectNode());
  }

  /**
   * @see {@link #addNonExistent}
   */
  protected TextNode addNonExistentTextNode(ObjectNode parent, String field, String text) {
    return addNonExistent(parent, field, parent.textNode(text));
  }

  /**
   * @see {@link #addNonExistent}
   */
  protected BooleanNode addNonExistentBooleanNode(ObjectNode parent, String field, boolean b) {
    return addNonExistent(parent, field, parent.booleanNode(b));
  }

  /**
   * @see {@link #addNonExistent}
   */
  protected ArrayNode addNonExistentArrayNode(ObjectNode parent, String field, JsonNode[] items) {
    ArrayNode arrayNode = parent.arrayNode();
    arrayNode.addAll(Arrays.asList(items));
    return addNonExistent(parent, field, arrayNode);
  }

  protected <N extends JsonNode> N removeIfExists(ObjectNode parent, String field) {
    JsonNode formerLayoutParamsField = parent.get(field);

    if (formerLayoutParamsField != null) {
      parent.remove(field);
    }
    return (N) formerLayoutParamsField;
  }

  protected ObjectNode getGameSaveUnlessFileFormatIsNewer(ObjectNode node, String gameSaveFieldName, int fileFormatNumber) {
    ObjectNode gameSaveNode = (ObjectNode) node.findValue(gameSaveFieldName);
    if (gameSaveNode == null) {
      return null;
    }

    JsonNode fileFormat = gameSaveNode.findValue("fileFormat");
    if (fileFormat != null && fileFormat.asInt() > fileFormatNumber) {
      System.out.println("\tSkipping migration.");
      return null;
    }

    return gameSaveNode;
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.designer.types;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidServiceObject;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class DesignerObjectType extends HappyDroidServiceObject {
  protected String typeId;
  protected String name;
  protected String image;
  protected String imageHash;
  protected DesignerObjectCategory category;

  public TextureRegion getRegion() {
//    TODO: Make this more memory efficient.
//    return TowerAssetManager.textureFromAtlas(imageFilename, atlasFilename);
    return null;
  }

  public String getName() {
    return name;
  }

  public DesignerObjectCategory getCategory() {
    return category;
  }

  @Override public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/designerobjecttype/";
  }

  public String getTypeId() {
    return typeId;
  }

  @Override protected boolean requireAuthentication() {
    return false;
  }

  public String getImageUrl() {
    return HappyDroidConsts.HAPPYDROIDS_URI + image;
  }

  public String getImageHash() {
    return imageHash;
  }
}

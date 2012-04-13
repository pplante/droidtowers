/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.collect.Maps;

import java.util.Map;

public class TowerAssetManagerFilesList {
  public static Map<String, Class> files = Maps.newHashMap();

  static {
    files.put("fonts/menlo_14_bold_white.fnt", BitmapFont.class);
files.put("fonts/helvetica_neue_12_white.fnt", BitmapFont.class);
files.put("fonts/bank_gothic_32.fnt", BitmapFont.class);
files.put("hud/menus.txt", TextureAtlas.class);
files.put("hud/buttons.txt", TextureAtlas.class);
files.put("sound/effects/click.wav", Sound.class);
files.put("default-skin.ui", Skin.class);
files.put("decals.png", Texture.class);
files.put("backgrounds/ground-top.png", Texture.class);
files.put("sound/effects/construction-destroy-1.wav", Sound.class);
files.put("sound/effects/construction-placement-1.wav", Sound.class);
files.put("hud/misc.txt", TextureAtlas.class);
files.put("transport.txt", TextureAtlas.class);
files.put("rooms/housing.txt", TextureAtlas.class);
files.put("rooms/generic.txt", TextureAtlas.class);
files.put("characters.txt", TextureAtlas.class);
files.put("hud/test.txt", TextureAtlas.class);
files.put("fonts/bank_gothic_64.fnt", BitmapFont.class);
files.put("fonts/menlo_16.fnt", BitmapFont.class);
files.put("backgrounds/cityscape.txt", TextureAtlas.class);
files.put("rain-drop.png", Texture.class);
files.put("backgrounds/cityscape.png", Texture.class);
files.put("fonts/helvetica_neue_18.fnt", BitmapFont.class);
files.put("backgrounds/clouds.txt", TextureAtlas.class);
files.put("backgrounds/ground-tile.png", Texture.class);
files.put("fonts/helvetica_neue_14_black.fnt", BitmapFont.class);
files.put("backgrounds/clouds.png", Texture.class);
files.put("fonts/hiragino_maru_14_white.fnt", BitmapFont.class);
files.put("fonts/helvetica_neue_10_bold_white.fnt", BitmapFont.class);
files.put("backgrounds/sky-gradient.png", Texture.class);
files.put("fonts/helvetica_neue_18_white.fnt", BitmapFont.class);
  }
}

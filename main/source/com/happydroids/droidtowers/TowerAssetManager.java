/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Logger;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.graphics.ResolutionIndependentAtlas;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.pipeline.AssetList;
import com.happydroids.droidtowers.platform.Display;

import java.io.IOException;
import java.util.Map;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapNearestNearest;

public class TowerAssetManager {
  private static final String TAG = TowerAssetManager.class.getSimpleName();
  private static MemoryTrackingAssetManager assetManager;
  public static final String WHITE_SWATCH = "swatches/swatch-white.png";
  public static final String WHITE_SWATCH_TRIANGLE = "swatches/swatch-white-triangle.png";
  private static AssetList assetList;
  private static Skin customSkin;

  public static MemoryTrackingAssetManager assetManager() {
    if (assetManager == null) {
      assetManager = new MemoryTrackingAssetManager();
      if (HappyDroidConsts.DEBUG) {
        assetManager.getLogger().setLevel(Logger.ERROR);
      }

      Texture.setAssetManager(assetManager);

      try {
        assetList = TowerGameService.instance().getObjectMapper().readValue(Gdx.files.internal("assets.json").read(), AssetList.class);

        addToAssetManager(assetList.preloadFiles, assetList.highDefFiles);
        addToAssetManager(assetList.normalFiles, assetList.highDefFiles);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      TextureLoader.TextureParameter parameter = new TextureLoader.TextureParameter();
      parameter.genMipMaps = true;
      parameter.minFilter = MipMapNearestNearest;
      parameter.magFilter = Linear;
      assetManager.load(checkForHDPI("elevator/shaft.png"), Texture.class, parameter);
      assetManager.load(checkForHDPI("elevator/empty.png"), Texture.class, parameter);

      assetManager.setErrorListener(new AssetErrorListener() {
        public void error(String fileName, Class type, Throwable throwable) {
          throw new RuntimeException("Error loading: " + fileName, throwable);
        }
      });

      makeCustomGUISkin();
    }

    return assetManager;
  }

  private static void makeCustomGUISkin() {
    ResolutionIndependentAtlas skinAtlas = new ResolutionIndependentAtlas(Gdx.files.internal("hud/skin.txt"));

    customSkin = new Skin();

    CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
    checkBoxStyle.checkboxOn = skinAtlas.findRegion("checkbox-on");
    checkBoxStyle.checkboxOff = skinAtlas.findRegion("checkbox-off");
    checkBoxStyle.font = FontManager.Default.getFont();
    checkBoxStyle.fontColor = Color.WHITE;
    customSkin.addStyle("default", checkBoxStyle);

    Slider.SliderStyle sliderStyle = new Slider.SliderStyle(new NinePatch(new Texture(Gdx.files.internal(WHITE_SWATCH)), Color.LIGHT_GRAY), skinAtlas.findRegion("slider-handle"));
    customSkin.addStyle("default", sliderStyle);

    TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
    textButtonStyle.up = new NinePatch(skinAtlas.findRegion("button"), 3, 3, 3, 3);
    textButtonStyle.font = FontManager.Roboto18.getFont();
    textButtonStyle.fontColor = Color.WHITE;
    textButtonStyle.down = new NinePatch(skinAtlas.findRegion("button-down"), 3, 3, 3, 3);
    textButtonStyle.downFontColor = Colors.ALMOST_BLACK;

    customSkin.addStyle("default", textButtonStyle);

    TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
    textFieldStyle.background = new NinePatch(skinAtlas.findRegion("button"), 3, 3, 3, 3);
    textFieldStyle.font = FontManager.Roboto18.getFont();
    textFieldStyle.fontColor = Color.WHITE;
    textFieldStyle.messageFont = FontManager.Roboto18.getFont();
    textFieldStyle.messageFontColor = Color.LIGHT_GRAY;
    textFieldStyle.cursor = new NinePatch(skinAtlas.findRegion("text-cursor"), 3, 3, 3, 3);
    textFieldStyle.selection = skinAtlas.findRegion("text-selection");

    customSkin.addStyle("default", textFieldStyle);
  }

  private static void addToAssetManager(Map<String, Class> preloadFiles, Map<String, String> highDefFiles) {
    for (Map.Entry<String, Class> entry : preloadFiles.entrySet()) {
      String fileName = entry.getKey();
      Class clazz = entry.getValue();
      if (Display.isHDPIMode() && highDefFiles.containsKey(fileName)) {
        fileName = highDefFiles.get(fileName);
      }

      assetManager.load(fileName, clazz);
    }
  }

  public static String checkForHDPI(String fileName) {
    if (Display.isHDPIMode() && assetList.highDefFiles.containsKey(fileName)) {
      return assetList.highDefFiles.get(fileName);
    }

    return fileName;
  }

  public static void dispose() {
    assetManager.dispose();
  }

  public static BitmapFont bitmapFont(String s) {
    return assetManager().get(s, BitmapFont.class);
  }

  public static Sound sound(String s) {
    return assetManager().get(s, Sound.class);
  }

  public static TextureAtlas textureAtlas(String s) {
    return assetManager().get(checkForHDPI(s), TextureAtlas.class);
  }

  public static Texture texture(String s) {
    return assetManager().get(checkForHDPI(s), Texture.class);
  }

  public static TextureAtlas.AtlasRegion textureFromAtlas(String textureName, String atlasName) {
    return assetManager().get(checkForHDPI(atlasName), TextureAtlas.class).findRegion(textureName);
  }

  public static NinePatch ninePatch(String fileName) {
    return ninePatch(fileName, Color.WHITE);
  }

  public static NinePatch ninePatch(String fileName, Color color) {
    return new NinePatch(texture(fileName), color);
  }

  public static Sprite sprite(String fileName) {
    return new Sprite(texture(fileName));
  }

  public static ImageButton imageButton(TextureAtlas.AtlasRegion region) {
    return new ImageButton(region);
  }

  public static Skin getCustomSkin() {
    return customSkin;
  }

  public static boolean hasFilesToPreload() {
    for (String preloadFile : assetList.preloadFiles.keySet()) {
      if (!assetManager().isLoaded(checkForHDPI(preloadFile))) {
        return true;
      }
    }

    return false;
  }

  public static NinePatch ninePatch(String fileName, Color color, Texture.TextureFilter filterA, Texture.TextureFilter filterB) {
    Texture texture = texture(fileName);
    texture.setFilter(filterA, filterB);

    return new NinePatch(texture, color);
  }

  public static Animation animationFromAtlas(String framePrefix, String atlasName, float animationTime) {
    return new Animation(animationTime, assetManager().get(atlasName, TextureAtlas.class).findRegions(framePrefix));
  }
}

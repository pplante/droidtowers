/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Logger;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.disk.FileResolverMultiplexer;
import com.happydroids.droidtowers.events.SafeEventBus;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.graphics.ResolutionIndependentAtlas;
import com.happydroids.droidtowers.gui.ColorizedImageButton;
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
    public static final String WHITE_SWATCH_BLACK_BORDER = "swatches/swatch-white-black-border.png";
    public static final String WHITE_SWATCH_SEMI_BLACK_BORDER = "swatches/swatch-white-semi-black-border.png";
    public static final String WHITE_SWATCH_TRIANGLE = "swatches/swatch-white-triangle.png";
    public static final String WHITE_SWATCH_TRIANGLE_LEFT = "swatches/swatch-white-triangle-left.png";
    private static AssetList assetList;
    private static Skin customSkin;
    private static Skin defaultSkin;
    private static SafeEventBus eventBus = new SafeEventBus(TowerAssetManager.class.getSimpleName());


    public static MemoryTrackingAssetManager assetManager() {
        if (assetManager == null) {
            assetManager = new MemoryTrackingAssetManager(new FileResolverMultiplexer());
            if (HappyDroidConsts.DEBUG) {
                assetManager.getLogger().setLevel(Logger.ERROR);
            }

            Texture.setAssetManager(assetManager);

            try {
                assetList = TowerGameService.instance()
                        .getObjectMapper()
                        .readValue(Gdx.files.internal("assets.json").read(), AssetList.class);

                ensureAssetsAreLoaded();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TextureLoader.TextureParameter parameter = new TextureLoader.TextureParameter();
            parameter.genMipMaps = true;
            parameter.minFilter = MipMapNearestNearest;
            parameter.magFilter = Linear;
            assetManager.load(checkForHDPI("elevator/shaft.png"), Texture.class, parameter);
            assetManager.load(checkForHDPI("elevator/empty.png"), Texture.class, parameter);

            defaultSkin = new Skin(Gdx.files.internal("default-skin.json"));
            makeCustomGUISkin();
        }

        return assetManager;
    }

    protected static void ensureAssetsAreLoaded() {
        addToAssetManager(assetList.preloadFiles, assetList.highDefFiles);
        addToAssetManager(assetList.normalFiles, assetList.highDefFiles);
    }

    private static void makeCustomGUISkin() {
        ResolutionIndependentAtlas skinAtlas = new ResolutionIndependentAtlas(Gdx.files.internal("hud/skin.txt"));

        int size = 4;
        NinePatchDrawable buttonNormal = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("button"), size, size, size, size));
        NinePatchDrawable buttonDown = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("button-down"), size, size, size, size));
        NinePatchDrawable buttonDisabled = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("button"), size, size, size, size));
        buttonDisabled.getPatch().getColor().a = 0.75f;

        customSkin = new Skin();

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOn = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("checkbox-on")));
        checkBoxStyle.checkboxOff = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("checkbox-off")));
        checkBoxStyle.font = FontManager.Default.getFont();
        checkBoxStyle.fontColor = Color.WHITE;
        customSkin.add("default", checkBoxStyle);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(new NinePatchDrawable(new NinePatch(new Texture(WHITE_SWATCH), Color.LIGHT_GRAY)), new NinePatchDrawable(new NinePatch(skinAtlas
                .findRegion("slider-handle"))));
        customSkin.add("default-horizontal", sliderStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = buttonNormal;
        textButtonStyle.font = FontManager.Roboto18.getFont();
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.down = buttonDown;
        textButtonStyle.downFontColor = Color.WHITE;
        textButtonStyle.disabled = buttonDisabled;

        customSkin.add("default", textButtonStyle);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = buttonNormal;
        textButtonStyle.font = FontManager.Roboto18.getFont();
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.down = buttonDown;
        textButtonStyle.downFontColor = Color.WHITE;
        textButtonStyle.checked = buttonDown;
        textButtonStyle.checkedFontColor = Color.WHITE;

        customSkin.add("toggle-button", textButtonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = buttonNormal;
        textFieldStyle.font = FontManager.Roboto18.getFont();
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.messageFont = FontManager.Roboto18.getFont();
        textFieldStyle.messageFontColor = Color.LIGHT_GRAY;
        textFieldStyle.cursor = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("text-cursor"), size, size, size, size));
        textFieldStyle.selection = new NinePatchDrawable(new NinePatch(skinAtlas.findRegion("text-selection")));

        customSkin.add("default", textFieldStyle);

        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.background = buttonNormal;
        selectBoxStyle.font = FontManager.Roboto18.getFont();
        selectBoxStyle.fontColor = Color.WHITE;
        selectBoxStyle.listStyle = new List.ListStyle();
        selectBoxStyle.background = buttonNormal;
        selectBoxStyle.listStyle.selection = buttonDown;

        customSkin.add("default", selectBoxStyle);
    }

    private static void addToAssetManager(Map<String, Class> preloadFiles, Map<String, String> highDefFiles) {
        for (Map.Entry<String, Class> entry : preloadFiles.entrySet()) {
            assetManager().load(checkForHDPI(entry.getKey()), entry.getValue());
        }
    }

    public static String checkForHDPI(String fileName) {
        if (Display.isXHDPIMode() && assetList.highDefFiles.containsKey(fileName)) {
            return assetList.highDefFiles.get(fileName);
        }

        return fileName;
    }

    public static void dispose() {
        assetManager.dispose();
        assetManager = null;
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
        return new ColorizedImageButton(region, Colors.ICS_BLUE);
    }

    public static Skin getCustomSkin() {
        return customSkin;
    }

    public static boolean preloadFinished() {
        for (String preloadFile : assetList.preloadFiles.keySet()) {
            if (!assetManager().isLoaded(checkForHDPI(preloadFile))) {
                return false;
            }
        }

        return true;
    }

    public static NinePatch ninePatch(String fileName, Color color, Texture.TextureFilter filterA, Texture.TextureFilter filterB) {
        Texture texture = texture(fileName);
        texture.setFilter(filterA, filterB);

        return new NinePatch(texture, color);
    }

    public static Animation animationFromAtlas(String framePrefix, String atlasName, float animationTime) {
        return new Animation(animationTime, textureAtlas(atlasName).findRegions(framePrefix));
    }

    public static Image image(String fileName) {
        return new Image(texture(fileName));
    }

    public static Skin getDefaultSkin() {
        return defaultSkin;
    }

    public static NinePatch ninePatch(String fileName, Color color, int left, int right, int top, int bottom) {
        NinePatch ninePatch = new NinePatch(texture(fileName), left, right, top, bottom);
        ninePatch.setColor(color);
        return ninePatch;
    }

    public static boolean isLoaded(String fileName) {
        return assetManager().isLoaded(checkForHDPI(fileName));
    }


    public static AssetList getAssetList() {
        return assetList;
    }

    public static Drawable ninePatchDrawable(String fileName, Color color, int left, int right, int top, int bottom) {
        return new NinePatchDrawable(ninePatch(fileName, color, left, right, top, bottom));
    }

    public static Drawable ninePatchDrawable(String fileName, Color color) {
        return new NinePatchDrawable(ninePatch(fileName, color));
    }

    public static TextureRegionDrawable drawableFromAtlas(String drawableName, String atlasFileName) {
        return new TextureRegionDrawable(textureFromAtlas(drawableName, atlasFileName));
    }

    public static Drawable drawable(String fileName) {
        return new TextureRegionDrawable(new TextureRegion(texture(fileName)));
    }

    public static SafeEventBus events() {
        return eventBus;
    }
}

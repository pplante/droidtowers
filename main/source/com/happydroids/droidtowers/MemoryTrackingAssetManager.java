/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;
import com.google.common.eventbus.EventBus;
import com.happydroids.droidtowers.disk.FileResolverMultiplexer;
import com.happydroids.droidtowers.events.AssetLoadCompleteEvent;
import com.happydroids.droidtowers.events.AssetLoadErrorEvent;
import com.happydroids.droidtowers.events.SafeEventBus;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;

public class MemoryTrackingAssetManager extends AssetManager {
    private int currentMemory;
    private Map<String, Integer> memoryPerFile;
    private final SafeEventBus eventBus;


    public MemoryTrackingAssetManager(FileResolverMultiplexer fileResolverMultiplexer) {
        super(fileResolverMultiplexer);

        currentMemory = 0;
        memoryPerFile = new HashMap<String, Integer>();
        eventBus = new SafeEventBus(MemoryTrackingAssetManager.class.getSimpleName());

        setErrorListener(new AssetErrorListener() {
            @Override
            public void error(AssetDescriptor asset, Throwable throwable) {
                AssetLoadErrorEvent event = Pools.obtain(AssetLoadErrorEvent.class);
                event.setFileName(asset.fileName);
                event.setType(asset.type);
                events().post(event);
                Pools.free(event);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private synchronized int calculateTextureSize(AssetManager assetManager, String fileName) {
        if (memoryPerFile.containsKey(fileName)) {
            return memoryPerFile.get(fileName);
        }

        Texture texture = assetManager.get(fileName, Texture.class);
        TextureData textureData = texture.getTextureData();
        int textureSize = textureData.getWidth() * textureData.getHeight();
        switch (textureData.getFormat()) {
            case RGB565:
                textureSize *= 2;
                break;
            case RGB888:
                textureSize *= 3;
                break;
            case RGBA4444:
                textureSize *= 2;
                break;
            case RGBA8888:
                textureSize *= 4;
                break;
        }

        if (textureData.useMipMaps()) {
            textureSize *= 1.33f;
        }

        memoryPerFile.put(fileName, textureSize);

        return textureSize;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        if (parameter == null) {
            parameter = makeAssetLoaderParameter(fileName, type);
        }
        if (parameter != null) {
            final LoadedCallback prevCallback = parameter.loadedCallback;
            parameter.loadedCallback = new LoadedCallback() {
                @Override
                public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
                    Gdx.app.log(MemoryTrackingAssetManager.class.getSimpleName(), "Loaded: " + fileName);

                    if (type.equals(Texture.class)) {
                        currentMemory += calculateTextureSize(assetManager, fileName);
                    }

                    if (prevCallback != null) {
                        prevCallback.finishedLoading(assetManager, fileName, type);
                    }

                    AssetLoadCompleteEvent e = Pools.obtain(AssetLoadCompleteEvent.class);
                    e.setFileName(fileName);
                    e.setType(type);
                    eventBus.post(e);
                    Pools.free(e);
                }
            };
        }

        super.load(fileName, type, parameter);
    }

    private <T> AssetLoaderParameters<T> makeAssetLoaderParameter(String fileName, Class<T> type) {
        AssetLoaderParameters<T> parameter;
        if (type.equals(Texture.class)) {
            parameter = (AssetLoaderParameters<T>) new TextureLoader.TextureParameter();
        } else if (type.equals(TextureAtlas.class)) {
            parameter = (AssetLoaderParameters<T>) new TextureAtlasLoader.TextureAtlasParameter();
        } else if (type.equals(Sound.class)) {
            parameter = (AssetLoaderParameters<T>) new SoundLoader.SoundParameter();
        } else if (type.equals(BitmapFont.class)) {
            parameter = (AssetLoaderParameters<T>) new BitmapFontLoader.BitmapFontParameter();
        } else if (type.equals(Pixmap.class)) {
            parameter = (AssetLoaderParameters<T>) new PixmapLoader.PixmapParameter();
        } else if (type.equals(Music.class)) {
            parameter = (AssetLoaderParameters<T>) new MusicLoader.MusicParameter();
        } else {
            parameter = null;
        }

        return parameter;
    }

    @Override
    public synchronized void unload(String fileName) {
        try {
            super.unload(fileName);

            if (memoryPerFile.containsKey(fileName)) {
                currentMemory -= memoryPerFile.get(fileName);
            }
        } catch (GdxRuntimeException ignored) {

        }
    }

    public synchronized float getMemoryInMegabytes() {
        return currentMemory / 1024f / 1024f;
    }

    public void invalidateAllTextures() {
        Texture.invalidateAllTextures(Gdx.app);
        finishLoading();
    }

    public EventBus events() {
        return eventBus;
    }
}

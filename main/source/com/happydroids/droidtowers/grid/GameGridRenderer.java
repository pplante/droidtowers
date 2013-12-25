/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.achievements.TutorialEngine;
import com.happydroids.droidtowers.entities.GameLayer;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.events.SwitchToolEvent;
import com.happydroids.droidtowers.graphics.Overlays;
import com.happydroids.droidtowers.graphics.TransitLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import static com.happydroids.droidtowers.TowerConsts.GRID_UNIT_SIZE;
import static com.happydroids.droidtowers.graphics.Overlays.POPULATION_LEVEL;
import static com.happydroids.droidtowers.input.GestureTool.PLACEMENT;

public class GameGridRenderer extends GameLayer {
    protected GameGrid gameGrid;
    protected final OrthographicCamera camera;
    protected boolean shouldRenderGridLines;
    protected final ShapeRenderer shapeRenderer;
    private Overlays activeOverlay;
    private Map<Overlays, Function<GridObject, Float>> overlayFunctions;
    private Function<GridObject, Integer> objectRenderSortFunction;
    private List<GridObject> objectsRenderOrder;
    private List<TransitLine> transitLines;
    private boolean shouldRenderTransitLines;
    protected Color renderTintColor;
    private final SpriteCache spriteCache;

    public GameGridRenderer(GameGrid gameGrid, OrthographicCamera camera) {
        this.gameGrid = gameGrid;
        this.camera = camera;

        renderTintColor = Color.WHITE;
        shouldRenderGridLines = false;
        shapeRenderer = new ShapeRenderer();

        transitLines = Lists.newArrayList();

        activeOverlay = null;

        makeOverlayFunctions();
        spriteCache = new SpriteCache();

        gameGrid.events().register(this);
    }

    @Override
    public void render(SpriteBatch spriteBatch, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(this.camera.combined);
        if (shouldRenderGridLines) {
            renderGridLines();
        }

        renderGridObjects(spriteBatch);

        Gdx.graphics.getGLCommon().glEnable(GL10.GL_BLEND);
        if (activeOverlay == Overlays.NOISE_LEVEL) {
            renderNoiseLevelOverlay();
        } else if (activeOverlay != null) {
            renderGenericOverlay(activeOverlay);
        }

        if (shouldRenderTransitLines && transitLines.size() > 0) {
            for (TransitLine transitLine : transitLines) {
                transitLine.render(shapeRenderer);
            }
        }

        Gdx.graphics.getGLCommon().glDisable(GL10.GL_BLEND);
    }

    private void makeOverlayFunctions() {
        overlayFunctions = new HashMap<Overlays, Function<GridObject, Float>>();

        overlayFunctions.put(Overlays.CRIME_LEVEL, Overlays.CRIME_LEVEL.getMethod());
        overlayFunctions.put(Overlays.EMPLOYMENT_LEVEL, Overlays.EMPLOYMENT_LEVEL.getMethod());
        overlayFunctions.put(Overlays.POPULATION_LEVEL, Overlays.POPULATION_LEVEL.getMethod());
        overlayFunctions.put(Overlays.DESIRABILITY_LEVEL, Overlays.DESIRABILITY_LEVEL.getMethod());
        overlayFunctions.put(Overlays.DIRT_LEVEL, Overlays.DIRT_LEVEL.getMethod());
    }

    private void renderGenericOverlay(Overlays overlay) {
        Function<GridObject, Float> function = overlayFunctions.get(overlay);
        Color baseColor = overlay.getColor(1f);

        shapeRenderer.begin(ShapeType.Filled);

        Array<GridObject> objects = gameGrid.getObjects();
        for (int i = 0, objectsSize = objects.size; i < objectsSize; i++) {
            GridObject gridObject = objects.get(i);
            tmp.set(gridObject.getWorldCenter().x, gridObject.getWorldCenter().y, 0);
            if (camera.frustum
                    .sphereInFrustum(tmp, Math.max(gridObject.getWorldBounds().width, gridObject.getWorldBounds().height))) {
                Float returnValue = function.apply(gridObject);
                if (returnValue != null) {
                    baseColor.a = returnValue;
                    shapeRenderer.setColor(baseColor);
                    shapeRenderer.rect(
                            gridObject.getPosition().getWorldX(),
                            gridObject.getPosition().getWorldY(),
                            gridObject.getSize().getWorldX(),
                            gridObject.getSize().getWorldY()
                    );
                }
            }
        }

        shapeRenderer.end();
    }

    private void renderNoiseLevelOverlay() {
        shapeRenderer.begin(ShapeType.Filled);

        for (int x = 0; x < gameGrid.gridSize.x; x++) {
            for (int y = 0; y < gameGrid.gridSize.y; y++) {
                GridPosition position = gameGrid.positionCache().getPosition(x, y);

                if (position.getNoiseLevel() > 0.01f) {
                    tmp.set(position.worldPoint().x, position.worldPoint().y, 0);
                    if (camera.frustum.sphereInFrustum(tmp, TowerConsts.GRID_UNIT_SIZE)) {
                        shapeRenderer.setColor(Overlays.NOISE_LEVEL.getColor(position.getNoiseLevel()));
                        shapeRenderer.rect(x * GRID_UNIT_SIZE, y * GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);
                    }
                }
            }
        }

        shapeRenderer.end();
    }

    private void renderGridObjects(SpriteBatch spriteBatch) {
        Gdx.gl.glEnable(GL10.GL_BLEND);
        spriteCache.setProjectionMatrix(camera.combined);
        spriteCache.begin();

        for (GridObject gridObject : gameGrid.getObjects()) {
            if (gridObject.shouldUseSpriteCache() && gridObject.getSpriteCacheId() != -1) {
                tmp.set(gridObject.getWorldCenter().x, gridObject.getWorldCenter().y, 0);
                if (camera.frustum
                        .sphereInFrustum(tmp, Math.max(gridObject.getWorldBounds().width, gridObject.getWorldBounds().height))) {
                    spriteCache.draw(gridObject.getSpriteCacheId());
                }
            }
        }

        spriteCache.end();


        spriteBatch.begin();

        for (GridObject gridObject : gameGrid.getObjects()) {
            tmp.set(gridObject.getWorldCenter().x, gridObject.getWorldCenter().y, 0);
            if (camera.frustum
                    .sphereInFrustum(tmp, Math.max(gridObject.getWorldBounds().width, gridObject.getWorldBounds().height))) {
                if (!gridObject.shouldUseSpriteCache() || gridObject.getSpriteCacheId() == -1) {
                    gridObject.render(spriteBatch, spriteCache, renderTintColor);
                }

                if (gridObject.hasDecals()) {
                    gridObject.renderDecals(spriteBatch);
                }
            }
        }

        spriteBatch.end();
    }

    private void renderGridLines() {
        GLCommon gl = Gdx.graphics.getGLCommon();
        gl.glEnable(GL10.GL_BLEND);

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 0.15f);

        for (int i = 0; i <= gameGrid.getGridSize().x; i++) {
            shapeRenderer.line(i * GRID_UNIT_SIZE, 0, i * GRID_UNIT_SIZE, gameGrid.getGridSize().y * GRID_UNIT_SIZE);
        }

        for (int i = 0; i <= gameGrid.getGridSize().y; i++) {
            shapeRenderer.line(0, i * GRID_UNIT_SIZE, gameGrid.getGridSize().x * GRID_UNIT_SIZE, i * GRID_UNIT_SIZE);
        }

        shapeRenderer.end();

        gl.glDisable(GL10.GL_BLEND);
    }

    public void toggleGridLines() {
        shouldRenderGridLines = !shouldRenderGridLines;
    }

    public void setActiveOverlay(Overlays overlay) {
        if (activeOverlay == null && overlay == POPULATION_LEVEL) {
            TutorialEngine.instance().moveToStepWhenReady("tutorial-turn-off-population-overlay");
        }

        if (activeOverlay == POPULATION_LEVEL && overlay == null) {
            TutorialEngine.instance().moveToStepWhenReady("tutorial-finished");
        }

        activeOverlay = overlay;
    }

    public void removeTransitLine(TransitLine transitLine) {
        transitLines.remove(transitLine);
    }

    public void addTransitLine(TransitLine transitLine) {
        transitLines.add(transitLine);
    }

    public void toggleTransitLines() {
        shouldRenderTransitLines = !shouldRenderTransitLines;
    }


    public void setRenderTintColor(Color renderTintColor) {
        this.renderTintColor = renderTintColor;
    }

    public Color getRenderTintColor() {
        return renderTintColor;
    }

    @Subscribe
    public void InputSystem_onSwitchTool(SwitchToolEvent event) {
        shouldRenderGridLines = event.selectedTool == PLACEMENT;
    }
}

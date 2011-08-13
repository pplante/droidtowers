package com.unhappyrobot;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.TextureDict;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.unhappyrobot.entities.Asteroid;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class Game implements ApplicationListener {
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private List<GameLayer> layers;

    public void create() {
        Random.init();

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        layers = new ArrayList<GameLayer>();
        spriteBatch = new SpriteBatch(10);
        font = new BitmapFont(Gdx.files.internal("fonts/24/ocr_a.fnt"), Gdx.files.internal("fonts/24/ocr_a.png"), false);

        GameLayer gameLayer = new GameLayer();
        addLayer(gameLayer);

        for (int i = 0; i < 500; i++) {
            Asteroid asteroid = new Asteroid(Random.randomInt(800), Random.randomInt(600), Random.randomFloat(), Random.randomFloat());
            gameLayer.addChild(asteroid);
        }
    }

    private void addLayer(GameLayer gameLayer) {
        layers.add(gameLayer);
    }

    public void render() {
        GL10 gl = Gdx.graphics.getGL10();
        gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);

        camera.update();
        camera.apply(gl);

        gl.glColor4f(1, 1, 1, 1);
        spriteBatch.begin();
        for (GameLayer layer : layers) {
            layer.render(spriteBatch);
        }
        spriteBatch.end();

        update();

        spriteBatch.begin();
		spriteBatch.setBlendFunction(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + ", delta:" + Gdx.graphics.getDeltaTime(), 10, 40);
		spriteBatch.end();
    }

    public void update() {
        float timeDelta = Gdx.graphics.getDeltaTime();

        WorldManager.update(timeDelta);

        for (GameLayer layer : layers) {
            layer.update(timeDelta);
        }
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
        TextureDict.unloadAll();
        spriteBatch.dispose();
        font.dispose();
    }
}

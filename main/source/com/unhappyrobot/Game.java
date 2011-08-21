package com.unhappyrobot;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.TextureDict;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.unhappyrobot.entities.Asteroid;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.PlayerShip;
import com.unhappyrobot.input.InputManager;
import com.unhappyrobot.layers.WorldContactPointLayer;
import com.unhappyrobot.mods.ModList;
import com.unhappyrobot.mods.ModListItem;
import com.unhappyrobot.scripting.ScriptScope;
import com.unhappyrobot.utils.IO;
import com.unhappyrobot.utils.Random;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.unhappyrobot.HttpRequest.REQUEST_TYPE.GET;

public class Game implements ApplicationListener {
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private static List<GameLayer> layers;
    private float totalTime;
    private static final float CAMERA_SPEED = 250.0f;
    private ScriptScope scriptScope;

    public void create() {
        Random.init();
        totalTime = 0.0f;

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        layers = new ArrayList<GameLayer>();
        spriteBatch = new SpriteBatch(10);
        font = new BitmapFont(Gdx.files.internal("fonts/16/ocr_a.fnt"), Gdx.files.internal("fonts/16/ocr_a.png"), false);

        GameLayer gameLayer = new GameLayer();
        addLayer(gameLayer);

        addLayer(new WorldContactPointLayer());

        PlayerShip playerShip = new PlayerShip(100, 100);
        gameLayer.addChild(playerShip);

        for (int i = 0; i < 50; i++) {
            Asteroid asteroid = new Asteroid(Random.randomInt(800), Random.randomInt(600), 0.5f + Math.min(0.5f, Random.randomFloat()));
            gameLayer.addChild(asteroid);
        }


        gameLayer.setupPhysics();

        DeferredManager.onGameThread().runAsync(new Runnable() {
            public void run() {
                try {
                    runjstest();
                    lookForMods();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void runjstest() throws IOException, UnsupportedEncodingException {
        String js = IO.readTextFile("../mod-test/testship.js");

//        scriptScope = new ScriptScope();
//        scriptScope.parseScript(js);
    }

    private ScriptScope loadScriptedGameObject(String jsCode) {
        ScriptScope scriptScope = new ScriptScope();
        scriptScope.parseScript(jsCode);

        return scriptScope;
    }

    public void lookForMods() throws IOException {
        HttpRequest.HttpResponse response = HttpRequest.makeRequest(GET, new URL("http://static.local/mods/mods.yaml"));

        Constructor modsListConstructor = new Constructor(ModList.class);
        TypeDescription modsListTypeDes = new TypeDescription(ModList.class);
        modsListTypeDes.putListPropertyType("mods", ModListItem.class);
        modsListConstructor.addTypeDescription(modsListTypeDes);

        Yaml yaml = new Yaml(modsListConstructor);
        ModList modList = (ModList) yaml.load(response.getBodyString());

        for (ModListItem mod : modList.mods) {
            System.out.println("found mod: " + mod.name + "\n" + mod.description);
        }

    }

    private void addLayer(GameLayer gameLayer) {
        layers.add(gameLayer);
    }

    public void render() {
        GL10 gl = Gdx.graphics.getGL10();
        gl.glViewport(0, 0, (int) camera.viewportWidth, (int) camera.viewportHeight);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        camera.update();
        camera.apply(gl);

        gl.glColor4f(1, 1, 1, 1);
        for (GameLayer layer : layers) {
            if (layer.isVisible()) {
                layer.render(spriteBatch, camera);
            }
        }

        spriteBatch.setTransformMatrix(new Matrix4().idt());
        spriteBatch.begin();
        spriteBatch.setBlendFunction(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        font.draw(spriteBatch, String.format("fps: %d, camera(%.1f, %.1f, %.1f), phys: %d", Gdx.graphics.getFramesPerSecond(), camera.position.x, camera.position.y, camera.zoom, WorldManager.getWorldInstance().getContactCount()), 10, 40);
        spriteBatch.end();

        update();
    }

    public void update() {
        float timeDelta = Gdx.graphics.getDeltaTime();
        totalTime += timeDelta;

        if (scriptScope != null)
            scriptScope.call("update", timeDelta);

        WorldManager.update();
        DeferredManager.onGameThread().update(timeDelta);

        for (GameLayer layer : layers) {
            layer.update(timeDelta);
        }

        InputManager.update(timeDelta);
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

    public static List<GameLayer> getLayers() {
        return layers;
    }
}

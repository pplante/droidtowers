package com.unhappyrobot.scripting;

import com.badlogic.gdx.math.Vector2;
import com.sun.javaws.jnl.XMLFormat;
import com.unhappyrobot.DeferredManager;
import com.unhappyrobot.Game;
import com.unhappyrobot.entities.GameObject;
import org.mozilla.javascript.ScriptableObject;

public class ScriptedGameObject extends ScriptableObject {
    public GameObject getGameObject() {
        return gameObject;
    }

    private GameObject gameObject;

    public ScriptedGameObject() {
        gameObject = new GameObject(0.0f, 0.0f);


    }

    public void jsFunction_useTexture(final String filename) {
        DeferredManager.onGameThread().runOnce(new Runnable() {
            public void run() {
                gameObject.useTexture(filename);
                Game.getLayers().get(0).addChild(gameObject);
                gameObject.setupPhysics();
            }
        });
    }

    public void jsFunction_setPosition(double x, double y) {
        gameObject.setPosition((float)x, (float)y);
    }

    public void jsFunction_setVelocity(double x, double y) {
        gameObject.setLinearVelocity(new Vector2((float)x, (float)y));
    }

    @Override
    public String getClassName() {
        return "ScriptedGameObject";
    }
}

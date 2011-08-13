package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class GameLayer {
    private List<GameObject> gameObjects;

    public GameLayer() {
		gameObjects = new ArrayList<GameObject>();
	}

	public void addChild(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void render(SpriteBatch spriteBatch) {
        for (GameObject gameObject : gameObjects) {
            gameObject.render(spriteBatch);
        }
    }

    public void update(float timeDelta) {
        for (GameObject gameObject : gameObjects) {
            gameObject.update(timeDelta);
        }
    }
}

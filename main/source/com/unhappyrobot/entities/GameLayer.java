package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class GameLayer {
    private Vector2 position;
    private Vector2 size;

    private List<GameObject> gameObjects;
    protected Matrix4 matrix;
    private boolean visible;

    public GameLayer() {
        gameObjects = new ArrayList<GameObject>();
        matrix = new Matrix4();
        visible = true;
    }

    public void addChild(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void render(SpriteBatch spriteBatch, Camera camera) {
        matrix.idt();
		matrix.setToTranslation(camera.position.x, camera.position.y, 0);

		spriteBatch.setTransformMatrix(matrix);
        spriteBatch.begin();
        spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        for (GameObject gameObject : gameObjects) {
            gameObject.render(spriteBatch);
        }

        spriteBatch.end();
    }

    public void update(float timeDelta) {
        for (GameObject gameObject : gameObjects) {
            gameObject.update(timeDelta);
        }
    }

    public void setupPhysics() {
        for (GameObject gameObject : gameObjects) {
            gameObject.setupPhysics();
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    protected void toggleVisibility() {
        visible = !visible;
    }
}

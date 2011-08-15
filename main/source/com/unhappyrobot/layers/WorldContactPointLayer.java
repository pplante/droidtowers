package com.unhappyrobot.layers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.TextureDict;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.unhappyrobot.WorldManager;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.entities.GameObject;

public class WorldContactPointLayer extends GameLayer {

    private Sprite contactPointSprite;

    public WorldContactPointLayer() {
        super();

        contactPointSprite = new Sprite(TextureDict.loadTexture("contact-point.png").get());
    }

    @Override
    public void render(SpriteBatch spriteBatch, Camera camera) {
        matrix.idt();
        matrix.setToTranslation(camera.position.x, camera.position.y, 0);

        spriteBatch.setTransformMatrix(matrix);
        spriteBatch.begin();

        World world = WorldManager.getWorldInstance();
        for (Contact contact : world.getContactList()) {
            if(contact.isTouching()) {
                for (Vector2 point : contact.getWorldManifold().getPoints()) {
                    spriteBatch.draw(contactPointSprite, point.x, point.y);
                }
            }
        }

        spriteBatch.end();
    }
}

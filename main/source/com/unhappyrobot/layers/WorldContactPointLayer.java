package com.unhappyrobot.layers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.TextureDict;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.unhappyrobot.WorldManager;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.input.Action;
import com.unhappyrobot.input.InputManager;

public class WorldContactPointLayer extends GameLayer {

    private Sprite contactPointSprite;
    private Box2DDebugRenderer box2DDebugRenderer;

    public WorldContactPointLayer() {
        super();

        contactPointSprite = new Sprite(TextureDict.loadTexture("contact-point.png").get());
        box2DDebugRenderer = new Box2DDebugRenderer();

        InputManager.bind(InputManager.Keys.V, new Action() {
            public void run(float timeDelta) {
                WorldContactPointLayer.this.toggleVisibility();
            }
        });
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
        box2DDebugRenderer.batch = spriteBatch;
        box2DDebugRenderer.render(world);
    }
}

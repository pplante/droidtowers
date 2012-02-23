package com.unhappyrobot.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;

public class AchievementNotification extends Table {
  public AchievementNotification() {
    Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGB565);
    pixmap.setColor(Color.DARK_GRAY);
    pixmap.drawLine(1, 1, 2, 1);
    pixmap.setColor(Color.BLACK);
    pixmap.drawLine(1, 2, 2, 2);

    defaults();
    pad(5);
    add(new Image(new Texture(Gdx.files.internal("hud/trophy.png")), Scaling.none)).minWidth(64).pad(5);
    add(new Label("This is a test label.", HeadsUpDisplay.getInstance().getGuiSkin()));
    row();
    add();
    add(new Label("You unlocked THE WORLD.", HeadsUpDisplay.getInstance().getGuiSkin()));
    pack();

    Texture texture = new Texture(pixmap);
    texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    TextureRegion textureRegion = new TextureRegion(texture, 0, 0, width, height);
    textureRegion.setV(0);
    textureRegion.setV2(height / MathUtils.nextPowerOfTwo((int) height));

    NinePatch ninePatch = new NinePatch(textureRegion);
    setBackground(ninePatch);
  }
}

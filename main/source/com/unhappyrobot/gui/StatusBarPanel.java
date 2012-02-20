package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.unhappyrobot.entities.Player;

import java.text.NumberFormat;

public class StatusBarPanel extends Table {
  private final Skin guiSkin;
  private final Label moneyLabel;
  private final Label experienceLabel;
  private float lastUpdated;
  private static final float UPDATE_FREQUENCY = 1f;

  public StatusBarPanel(Skin guiSkin) {
    this.guiSkin = guiSkin;
    Label.LabelStyle helvetica_neue_10_bold_white = this.guiSkin.getStyle("helvetica_neue_10_bold_white", Label.LabelStyle.class);

    moneyLabel = makeLabel("0", null);
    experienceLabel = makeLabel("0", null);

    setBackground(guiSkin.getPatch("default-round"));

    defaults();
    top().left();

    row().center();
    add(makeLabel("EXPERIENCE", helvetica_neue_10_bold_white)).minWidth(150);
    add(makeLabel("COINS", helvetica_neue_10_bold_white)).minWidth(150);

    row().center();
    add(experienceLabel);
    add(moneyLabel);

    pack();
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    lastUpdated += delta;

    if (lastUpdated >= UPDATE_FREQUENCY) {
      lastUpdated = 0f;
      Player player = Player.getInstance();
      moneyLabel.setText("¢ " + NumberFormat.getInstance().format(player.getCoins()));
      experienceLabel.setText(NumberFormat.getInstance().format(player.getExperience()));

      pack();
    }
  }

  private Label makeLabel(String text, Label.LabelStyle labelStyle) {
    Label label = new Label(text, guiSkin);
    label.setAlignment(Align.CENTER);

    if (labelStyle != null) {
      label.setStyle(labelStyle);
    }

    return label;
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.achievements.Requirement;

import java.util.List;

import static com.happydroids.droidtowers.gui.FontManager.Roboto18;
import static com.happydroids.droidtowers.gui.FontManager.RobotoBold18;

class RequirementsTable extends Table {
  public RequirementsTable(List<Requirement> requirements) {
    defaults().top().left().space(8).fillX();

    add(FontManager.Default.makeLabel("requirement", Color.GRAY)).expandX();
    add(FontManager.Default.makeLabel("amount", Color.GRAY, Align.CENTER));
    add(FontManager.Default.makeLabel("your progress", Color.GRAY, Align.CENTER)).center();

    row();
    add(new HorizontalRule(Color.DARK_GRAY, 1)).colspan(3);

    for (Requirement requirement : requirements) {
      addRequirement(requirement);
    }
  }

  @SuppressWarnings("unchecked")
  private void addRequirement(Requirement requirement) {
    row().fillX();
    Label label = Roboto18.makeLabel(requirement.displayString());
    label.setWrap(true);
    add(label).expandX().pad(12, 0, 12, 0);

    Cell amountCell = add();
    if (requirement.getAmount() > 0) {
      Label amountValue = RobotoBold18.makeLabel(String.format("%d/%d", requirement.getCurrentWeight(), requirement.getAmount()));
      amountValue.setAlignment(Align.CENTER);
      amountCell.setWidget(amountValue);
    }

    ProgressBar progressBar = new ProgressBar(requirement.getProgress());
    add(progressBar)
            .width(200)
            .right()
            .top();

    row();
    add(new HorizontalRule(Color.DARK_GRAY, 1)).colspan(3);
  }
}

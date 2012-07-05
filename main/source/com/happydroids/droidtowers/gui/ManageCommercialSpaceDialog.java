/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.generators.NameGenerator;

import static com.happydroids.droidtowers.platform.Display.scale;

public class ManageCommercialSpaceDialog extends Dialog {
  private final CommercialSpace commercialSpace;
  private TextField textField;

  public ManageCommercialSpaceDialog(final CommercialSpace commercialSpace) {
    this.commercialSpace = commercialSpace;

    textField = FontManager.Roboto18.makeTextField(commercialSpace.getName(), "");

    setTitle("Manage: " + commercialSpace.getName());
    setView(makeContentView());

    addButton("Save", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dismiss();

        commercialSpace.setName(textField.getText());
      }
    });

    addButton("Discard", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dismiss();
      }
    });
  }

  private Actor makeContentView() {
    Table content = new Table();
    content.defaults().pad(scale(4));

    content.row().fillX();
    content.add(FontManager.Roboto18.makeLabel("Name of " + commercialSpace.getGridObjectType().getName())).expandX().colspan(2);

    content.row();
    content.add(textField).width(400);
    content.add(makeRandomNameButton());

    content.row();
    content.add(FontManager.Roboto18.makeLabel("Current Employees"));

    content.row();
    content.add(FontManager.Roboto18.makeLabel(commercialSpace.getJobsFilled() + "/" + commercialSpace.getJobsProvided()));

    return content;
  }

  private TextButton makeRandomNameButton() {
    TextButton randomNameButton = FontManager.Roboto12.makeTextButton("Random Name");
    randomNameButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        textField.setText(NameGenerator.randomCorporationName());
      }
    });

    return randomNameButton;
  }
}

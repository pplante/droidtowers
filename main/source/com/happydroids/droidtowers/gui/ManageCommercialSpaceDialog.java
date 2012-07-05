/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.entities.CommercialSpace;

public class ManageCommercialSpaceDialog extends Dialog {
  private final CommercialSpace commercialSpace;

  public ManageCommercialSpaceDialog(CommercialSpace commercialSpace) {
    this.commercialSpace = commercialSpace;

    defaults().top().left();

    setTitle("Manage: " + commercialSpace.getName());

    Table content = new Table();

    content.row().fillX();
    content.add(FontManager.Roboto18.makeLabel("Name of " + commercialSpace.getGridObjectType().getName())).expandX();

    content.row().fillX();
    TextField textField = FontManager.Roboto18.makeTextField(commercialSpace.getName(), "");
    content.add(textField).width(400).expandX();

    setView(content);
  }
}

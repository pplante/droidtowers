/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.happydroids.droidtowers.entities.HotelRoom;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.platform.Display;


public class ManageHotelRoomDialog extends Dialog {
  private final HotelRoom hotelRoom;
  private TextField textField;

  public ManageHotelRoomDialog(final HotelRoom hotelRoom) {
    this.hotelRoom = hotelRoom;

    textField = FontManager.Roboto18.makeTextField(hotelRoom.getName(), "");

    setTitle("Manage: " + hotelRoom.getName());
    setView(makeContentView());

    addButton("Save", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dismiss();

        hotelRoom.setName(textField.getText());
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
    content.defaults().pad(Display.devicePixel(4));

    content.row().fillX();
    content.add(FontManager.Roboto18.makeLabel("Name of " + hotelRoom.getGridObjectType().getName()))
            .expandX()
            .colspan(2);

    content.row();
    content.add(textField).width(400);
    content.add(makeRandomNameButton());

    content.row();
    content.add(FontManager.Roboto18.makeLabel("Uses between Cleanings")).colspan(2);

    content.row();
    content.add(FontManager.Roboto18.makeLabel(String.valueOf(hotelRoom.getNumVisitors()))).colspan(2);

    return content;
  }

  private TextButton makeRandomNameButton() {
    TextButton randomNameButton = FontManager.Roboto12.makeTextButton("Random Name");
    randomNameButton.addListener(new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        textField.setText(NameGenerator.randomCorporationName());
      }
    });

    return randomNameButton;
  }
}

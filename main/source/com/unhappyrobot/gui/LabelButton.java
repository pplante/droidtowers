package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelButton extends Button {

  private final Label label;
  private ResponseType responseType;

  public LabelButton(Skin uiSkin, String labelText) {
    super(uiSkin);
    defaults();
    label = new Label(labelText, uiSkin);
    add(label);
  }

  public void setText(String newText) {
    label.setText(newText);
    pack();
  }

  public ResponseType getResponseType() {
    return responseType;
  }

  public void setResponseType(ResponseType responseType) {
    this.responseType = responseType;
  }

  @Override
  public void validate() {
    pack();
    super.validate();
  }
}

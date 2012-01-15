package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class LabelButton extends TextButton {
  private ResponseType responseType;

  public LabelButton(Skin uiSkin, String labelText) {
    super(labelText, uiSkin);
    defaults();
    getLabelCell().pad(4, 8, 4, 8);
    invalidate();
  }

  public ResponseType getResponseType() {
    return responseType;
  }

  public void setResponseType(ResponseType responseType) {
    this.responseType = responseType;
  }
}

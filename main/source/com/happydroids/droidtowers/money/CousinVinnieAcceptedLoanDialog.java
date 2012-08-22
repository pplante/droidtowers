/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.money;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.collect.Iterables;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.platform.Display;

public class CousinVinnieAcceptedLoanDialog extends Dialog {
  public CousinVinnieAcceptedLoanDialog(GameGrid gameGrid, int amountToLoan) {
    super();

    if (!gameGrid.isEmpty()) {
      int numGridObjects = gameGrid.getObjects().size - 1;
      GridObject gridObject;
      gridObject = Iterables.get(gameGrid.getObjects(), MathUtils.random(numGridObjects));
      while (gridObject.getAmountLoanedFromCousinVinnie() > 0) {
        gridObject = Iterables.get(gameGrid.getObjects(), MathUtils.random(numGridObjects));
      }
      gridObject.addLoanFromCousinVinnie(amountToLoan);

      Player.instance().addCurrency(amountToLoan);

      TextureAtlas.AtlasRegion cousinVinnieTexture = TowerAssetManager.textureFromAtlas("droid-cousin-vinnie", "hud/menus.txt");
      Image cousinVinnieImage = new Image(cousinVinnieTexture);

      Table c = new Table();
      c.pad(Display.devicePixel(4));
      c.defaults().top().left();

      c.row();
      c.add(FontManager.Roboto18.makeLabel("I am glad you made the right choice.\n\nI have selected my new hideout:"));
      c.add(cousinVinnieImage).spaceLeft(Display.devicePixel(20)).top().width(Display.devicePixel(96));

      c.row();
      c.add(new Image(gridObject.getSprite()));
      c.add();

      c.row().spaceTop(Display.devicePixel(20));
      c.add(FontManager.Roboto18
                    .makeLabel("Remember, we're business partners now. So keep\nthose security guards away from me."))
              .colspan(2);

      setView(c);
    } else {
      dismiss();
      new Dialog()
              .setMessage("Cousin Vinnie was unable to find a suitable hideout.")
              .show();
    }
  }
}

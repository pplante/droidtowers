/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.entities.Janitor;
import com.happydroids.droidtowers.entities.Maid;
import com.happydroids.droidtowers.entities.SecurityGuard;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.types.CommercialType;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.RoomType;
import com.happydroids.droidtowers.utils.StringUtils;

import java.util.List;

import static com.happydroids.droidtowers.types.ProviderType.COMMERCIAL;
import static com.happydroids.droidtowers.types.ProviderType.HOUSING;

class GridObjectPurchaseItem extends Table {
  private final TextButton buyButton;
  private final GridObjectType gridObjectType;

  public GridObjectPurchaseItem(final GridObjectType gridObjectType) {
    this.gridObjectType = gridObjectType;

    Image gridObjectImage = new Image(new TextureRegionDrawable(gridObjectType.getTextureRegion(0)), Scaling.fit, Align.left | Align.top);
    Label nameLabel = FontManager.RobotoBold18.makeLabel(gridObjectType.getName());
    Label priceLabel = FontManager.RobotoBold18
                               .makeLabel(StringUtils.currencyFormat(gridObjectType.getCoins()), Color.WHITE, Align.right);
    buyButton = FontManager.RobotoBold18.makeTextButton(gridObjectType.isLocked() ? "How to\nunlock" : "Buy");

    defaults().top().left().space(Display.devicePixel(8));

    Table left = new Table();
    left.defaults().space(Display.devicePixel(8));
    left.setWidth(Display.devicePixel(200));
    left.row().fillX();
    left.add(nameLabel).expandX();
    left.row().fillX();
    left.add(gridObjectImage).height(Display.devicePixel(40)).expand();

    Table center = new Table();
    center.defaults().space(Display.devicePixel(8));
    center.row().fillX();
    if (gridObjectType.hasDescription()) {
      Label label = FontManager.Roboto18.makeLabel(StringUtils.wrap(gridObjectType.getDescription(), 35));
//      label.setWrap(true);
      center.add(label).expandX();
    }

    if (gridObjectType.hasStatsLine()) {
      center.row().fill();
      center.add(makeGridObjectInfo()).expand().bottom();
    }

    Table right = new Table();
    right.defaults().right().width(Display.devicePixel(130)).space(Display.devicePixel(8));
    right.row();
    right.add(priceLabel);
    right.row();
    right.add(buyButton);

    row().fill();
    add(left).width(Display.devicePixel(200));
    add(center).expand();
    add(right);
  }

  private Actor makeGridObjectInfo() {
    Label descriptionLabel = FontManager.Default.makeLabel("");
    String statsLine = gridObjectType.getStatsLine();

    int maxIncome = 0;
    if (gridObjectType.provides(HOUSING)) {
      maxIncome = ((RoomType) gridObjectType).getPopulationMax() * gridObjectType.getCoinsEarned();
      statsLine = statsLine.replace("{maxResidents}", "" + ((RoomType) gridObjectType).getPopulationMax());
    } else if (gridObjectType.provides(COMMERCIAL)) {
      maxIncome = ((RoomType) gridObjectType).getPopulationMax() * gridObjectType.getCoinsEarned();
      statsLine = statsLine.replace("{maxEmployees}", "" + ((CommercialType) gridObjectType).getJobsProvided());
    }

    statsLine = statsLine.replace("{maxIncome}", StringUtils.currencyFormat(maxIncome));

    if (statsLine.contains("{servicedBy}")) {
      List<String> servicedBy = Lists.newArrayList();
      if (gridObjectType.provides(Janitor.JANITOR_SERVICES_PROVIDER_TYPES)) {
        servicedBy.add("Janitors");
      }

      if (gridObjectType.provides(Maid.MAID_SERVICES_PROVIDER_TYPES)) {
        servicedBy.add("Maids");
      }

      if (gridObjectType.provides(SecurityGuard.SECURITY_GUARD_SERVICE_PROVIDER_TYPES)) {
        servicedBy.add("Security Guards");
      }

      statsLine = statsLine.replace("{servicedBy}", StringUtils.join(servicedBy, ", "));
    }

    descriptionLabel.setText(statsLine);
//    descriptionLabel.setWrap(true);

//    c.debug();
    return descriptionLabel;
  }

  public void setBuyClickListener(ClickListener clickListener) {
    if (gridObjectType.isLocked()) {
      getColor().a = 0.65f;
      buyButton.addListener(new GridObjectTypeLockedClickListener(gridObjectType));
    } else {
      buyButton.addListener(clickListener);
    }
  }
}

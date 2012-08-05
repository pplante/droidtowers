/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.Player;

public class TowerRatingPopOver extends PopOver {
  private final RatingBar budgetRatingBar;
  private final RatingBar populationRatingBar;
  private final RatingBar employmentRatingBar;
  private final RatingBar desirabilityRatingBar;
  private float timeUntilUpdate;


  public TowerRatingPopOver() {
    super();

    budgetRatingBar = new RatingBar();
    populationRatingBar = new RatingBar();
    employmentRatingBar = new RatingBar();
    desirabilityRatingBar = new RatingBar();

    row();
    add(FontManager.Roboto12.makeLabel("Monthly Budget", Color.WHITE));
    row();
    add(budgetRatingBar);
    row();
    add(FontManager.Roboto12.makeLabel("Population", Color.WHITE));
    row();
    add(populationRatingBar);
    row();
    add(FontManager.Roboto12.makeLabel("Employment", Color.WHITE));
    row();
    add(employmentRatingBar);
    row();
    add(FontManager.Roboto12.makeLabel("Desirability", Color.WHITE));
    row();
    add(desirabilityRatingBar);
    pack();
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    timeUntilUpdate -= delta;

    if (timeUntilUpdate <= 0) {
      timeUntilUpdate = TowerConsts.HUD_UPDATE_FREQUENCY;

      Player player = Player.instance();

      budgetRatingBar.setValue(player.getBudgetRating() * 5f);
      populationRatingBar.setValue(player.getPopulationRating() * 5f);
      employmentRatingBar.setValue(player.getEmploymentRating() * 5f);
      desirabilityRatingBar.setValue(player.getDesirabilityRating() * 5f);

      pack();
    }
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.generators.NameGenerator;

import static com.happydroids.droidtowers.platform.Display.scale;

public class ManageCommercialSpaceDialog extends ScrollableTowerWindow {
  private final CommercialSpace commercialSpace;
  private TextField textField;

  public ManageCommercialSpaceDialog(final CommercialSpace commercialSpace) {
    super("Manage " + commercialSpace.getName(), DroidTowersGame.getRootUiStage());
    this.commercialSpace = commercialSpace;

    textField = FontManager.Roboto24.makeTextField(commercialSpace.getName(), "");

    defaults().pad(scale(4));

    setStaticHeader(makeNameHeader());

    addLabel("Current Employees", FontManager.Roboto24);

    addHorizontalRule(Colors.ICS_BLUE, 2, 2);

    JobCandidateListView candidateListView = new JobCandidateListView();
//    candidateListView.setBackground(TowerAssetManager.ninePatch(TowerAssetManager.WHITE_SWATCH, Color.DARK_GRAY));
    candidateListView.pad(scale(20));
    candidateListView.setCountLabelSuffix("employees");
    candidateListView.setCandidates(Lists.newArrayList(this.commercialSpace.getEmployees()));

    row();
    add(candidateListView).spaceBottom(scale(20));

    TextButton hireButton = FontManager.Roboto18.makeTextButton("Employee Search");
    hireButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new AvailableJobCandidateDialog(ManageCommercialSpaceDialog.this.commercialSpace).show();
      }
    });

    TextButton fireButton = FontManager.Roboto18.makeTextButton("Fire Employee");


    row();
    add(fireButton).padLeft(scale(70));
    add(hireButton);

    shoveContentUp();
  }

  private Actor makeNameHeader() {
    Table c = new Table();
    c.pad(scale(22));
    c.defaults().space(scale(4)).top().left();

    c.row();
    c.add(FontManager.Roboto18.makeLabel("Name of " + this.commercialSpace.getGridObjectType().getName())).colspan(2);

    c.row();
    c.add(textField).minWidth(400);
    c.add(makeRandomNameButton()).fill();

    return c;
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

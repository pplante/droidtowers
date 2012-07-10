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
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.gui.events.OnChangeCandidateCallback;

import static com.happydroids.droidtowers.platform.Display.scale;

public class ManageCommercialSpaceDialog extends ScrollableTowerWindow {
  private final CommercialSpace commercialSpace;
  private TextField textField;
  private final JobCandidateListView candidateListView;
  private final TextButton hireButton;
  private final TextButton fireButton;

  public ManageCommercialSpaceDialog(final CommercialSpace commercialSpace) {
    super("Manage " + commercialSpace.getName(), DroidTowersGame.getRootUiStage());
    this.commercialSpace = commercialSpace;

    textField = FontManager.Roboto24.makeTextField(commercialSpace.getName(), "");

    hireButton = FontManager.Default.makeTextButton("Employee Search");
    hireButton.setClickListener(new HireCandidateDialogOpener());

    fireButton = FontManager.Roboto18.makeTextButton("Fire Employee");
    fireButton.visible = false;
    fireButton.setClickListener(new FireCandidateClickListener());

    candidateListView = new JobCandidateListView();
    candidateListView.pad(scale(20));
    candidateListView.setCountLabelSuffix("employees");
    candidateListView.setOnChangeCandidateListener(new OnChangeCandidateCallback() {
      @Override
      public void change(JobCandidate candidate) {
        fireButton.visible = candidate != null;
      }
    });
    candidateListView.setCandidates(Lists.newArrayList(this.commercialSpace.getEmployees()));

    defaults().pad(scale(4));

    setStaticHeader(makeNameHeader());

    addLabel("Current Employees", FontManager.Roboto24).bottom();
    add(hireButton).fillY();

    addHorizontalRule(Colors.ICS_BLUE, 2, 2);

    row();
    add(candidateListView).spaceBottom(scale(20));

    row();
    add(fireButton).padLeft(scale(70));

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

  private class FireCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(Actor actor, float x, float y) {
      final JobCandidate selectedCandidate = candidateListView.getSelectedCandidate();
      if (selectedCandidate == null) {
        return;
      }

      new Dialog()
              .setTitle("Are you sure?")
              .setMessage("Are you sure you want to fire " + selectedCandidate.getName() + "?")
              .addButton("Yes", new OnClickCallback() {
                @Override
                public void onClick(Dialog dialog) {
                  dialog.dismiss();
                  commercialSpace.removeEmployee(selectedCandidate);
                  candidateListView.removeCandidate(selectedCandidate);
                }
              })
              .addButton("No", new OnClickCallback() {
                @Override
                public void onClick(Dialog dialog) {
                  dialog.dismiss();
                }
              })
              .show();
    }
  }

  private class HireCandidateDialogOpener extends VibrateClickListener {
    @Override
    public void onClick(Actor actor, float x, float y) {
      new AvailableJobCandidateDialog(ManageCommercialSpaceDialog.this.commercialSpace)
              .setDismissCallback(new Runnable() {
                @Override
                public void run() {
                  candidateListView.setCandidates(Lists.newArrayList(commercialSpace.getEmployees()));
                }
              })
              .show();
    }
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.generators.JobCandidateGenerator;

import java.text.NumberFormat;
import java.util.List;
import java.util.ListIterator;

import static com.happydroids.droidtowers.platform.Display.scale;

public class AvailableJobCandidateDialog extends Dialog {
  private final List<JobCandidate> jobCandidates;
  private final ListIterator<JobCandidate> candidateIterator;
  private JobCandidate selectedCandidate;
  private final Label nameLabel;
  private final Label candidateCountLabel;
  private final Label salaryLabel;
  private final StarRatingBar workEthicRating;
  private final StarRatingBar experienceRating;


  public AvailableJobCandidateDialog() {
    super();

    nameLabel = FontManager.Roboto18.makeLabel("");
    salaryLabel = FontManager.RobotoBold18.makeLabel("");
    workEthicRating = new StarRatingBar(0, 5);
    experienceRating = new StarRatingBar(0, 5);
    candidateCountLabel = FontManager.Roboto12.makeLabel("");

    jobCandidates = JobCandidateGenerator.generate(5);
    candidateIterator = jobCandidates.listIterator();
    selectedCandidate = candidateIterator.next();
    updateCandidate();

    addButton("< Prev", new PreviousCandidateClickListener());
    addButton("Close", new CloseClickListener());
    addButton("Hire", new HireCandidateClickListener());
    addButton("Next >", new NextCandidateClickListener());

    Table c = new Table();
    c.width(300);
    c.defaults().top().left().pad(scale(4));

    c.row();
    c.add(nameLabel).colspan(2);

    c.row().fillX();
    c.add(new HorizontalRule()).fillX().colspan(2);

    c.row();
    c.add(FontManager.Default.makeLabel("Experience", Color.LIGHT_GRAY));
    c.add(FontManager.Default.makeLabel("Efficiency", Color.LIGHT_GRAY));
    c.row();
    c.add(experienceRating);
    c.add(workEthicRating);

    c.row().spaceTop(scale(16));
    c.add(FontManager.Default.makeLabel("Salary", Color.LIGHT_GRAY));
    c.row();
    c.add(salaryLabel).left();

    c.row();
    c.add(candidateCountLabel).padBottom(scale(-10)).padTop(scale(20)).colspan(2).center();

    setView(c);
  }

  private void updateCandidate() {
    nameLabel.setText(selectedCandidate.getName());

    experienceRating.setValue(selectedCandidate.getExperienceLevel());
    workEthicRating.setValue(selectedCandidate.getWorkEthic());

    salaryLabel.setText(NumberFormat.getCurrencyInstance().format(selectedCandidate.getSalary()));

    int candidateNum = jobCandidates.indexOf(selectedCandidate) + 1;
    candidateCountLabel.setText(candidateNum + " of " + jobCandidates.size() + " candidates");
  }

  private class PreviousCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(Actor actor, float x, float y) {
      if (candidateIterator.hasPrevious()) {
        selectedCandidate = candidateIterator.previous();
        updateCandidate();
      }
    }
  }

  private class NextCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(Actor actor, float x, float y) {
      if (candidateIterator.hasNext()) {
        selectedCandidate = candidateIterator.next();
        updateCandidate();
      }
    }
  }

  private class HireCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(Actor actor, float x, float y) {

    }
  }

  private class CloseClickListener extends OnClickCallback {
    @Override
    public void onClick(Dialog dialog) {
      dialog.dismiss();
    }
  }
}

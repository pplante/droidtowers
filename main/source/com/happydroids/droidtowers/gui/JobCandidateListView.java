/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.gui.events.OnChangeCandidateCallback;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.ListIterator;

import static com.happydroids.droidtowers.gui.FontManager.Roboto24;
import static com.happydroids.droidtowers.platform.Display.scale;

public class JobCandidateListView extends Table {
  private List<JobCandidate> candidates;
  private ListIterator<JobCandidate> candidateIterator;
  private JobCandidate selectedCandidate;

  private final Label nameLabel;
  private final Label salaryLabel;
  private final StarRatingBar workEthicRating;
  private final StarRatingBar experienceRating;
  private final Label candidateCountLabel;
  private String countLabelSuffix;
  private boolean shownNoCanidatesText;
  private OnChangeCandidateCallback onChangeCandidateListener;
  private final Table withCandidatesView;
  private final Table withoutCandidatesView;
  private final Label noCandidatesFoundLabel;


  public JobCandidateListView() {
    super();
    countLabelSuffix = "candidates";

    nameLabel = FontManager.Roboto18.makeLabel("");
    salaryLabel = FontManager.RobotoBold18.makeLabel("");
    workEthicRating = new StarRatingBar(0, 5);
    experienceRating = new StarRatingBar(0, 5);
    candidateCountLabel = FontManager.Roboto12.makeLabel("");

    ColorizedImageButton prevButton = new ColorizedImageButton(TowerAssetManager.textureFromAtlas("large-left-arrow", "hud/menus.txt"), Colors.ICS_BLUE);
    prevButton.setClickListener(new PreviousCandidateClickListener());

    ColorizedImageButton nextButton = new ColorizedImageButton(TowerAssetManager.textureFromAtlas("large-right-arrow", "hud/menus.txt"), Colors.ICS_BLUE);
    nextButton.setClickListener(new NextCandidateClickListener());

    withCandidatesView = newTable();
    withCandidatesView.defaults().space(scale(32));
    withCandidatesView.add(prevButton).center();
    withCandidatesView.add(makeInnerContentView());
    withCandidatesView.add(nextButton).center();

    noCandidatesFoundLabel = Roboto24.makeLabel("No " + StringUtils.capitalize(countLabelSuffix) + " found.");
    withoutCandidatesView = newTable();
    withoutCandidatesView.add(noCandidatesFoundLabel).center();
  }

  private Actor makeInnerContentView() {
    Table c = new Table("inner-content-view");
    c.defaults().top().left().expandX().fillX();

    c.row().minWidth(400);
    c.add(nameLabel).colspan(2);

    c.row().fillX();
    c.add(new HorizontalRule(Color.GRAY, 1)).fillX().colspan(2);

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

    c.row().fillX();
    c.add(candidateCountLabel).padTop(scale(20)).colspan(2).right().fillX();

    return c;
  }

  public void setCandidates(List<JobCandidate> candidates) {
    this.candidates = candidates;
    regenerateIterator(candidates);
  }

  private void regenerateIterator(List<JobCandidate> candidates) {
    candidateIterator = candidates.listIterator();
    if (candidateIterator.hasNext()) {
      selectedCandidate = candidateIterator.next();
    } else {
      selectedCandidate = null;
    }

    updateView();
  }

  public JobCandidate getSelectedCandidate() {
    return selectedCandidate;
  }

  private void updateView() {
    clear();

    if (selectedCandidate != null) {
      add(withCandidatesView);

      nameLabel.setText(selectedCandidate.getName());

      experienceRating.setValue(selectedCandidate.getExperienceLevel());
      workEthicRating.setValue(selectedCandidate.getWorkEthic());

      salaryLabel.setText(NumberFormat.getCurrencyInstance().format(selectedCandidate.getSalary()));

      int candidateNum = candidates.indexOf(selectedCandidate) + 1;
      candidateCountLabel.setText(candidateNum + " of " + candidates.size() + " " + countLabelSuffix);
    } else {
      add(withoutCandidatesView);
    }

    invalidateHierarchy();
    layout();

    if (onChangeCandidateListener != null) {
      onChangeCandidateListener.change(selectedCandidate);
    }
  }

  public void removeCandidate(JobCandidate candidate) {
    candidates.remove(candidate);

    regenerateIterator(candidates);
  }

  public void setOnChangeCandidateListener(OnChangeCandidateCallback onChangeCandidateListener) {
    this.onChangeCandidateListener = onChangeCandidateListener;
  }


  private class PreviousCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(Actor actor, float x, float y) {
      if (candidateIterator.hasPrevious()) {
        selectedCandidate = candidateIterator.previous();
        updateView();
      }
    }
  }

  private class NextCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(Actor actor, float x, float y) {
      if (candidateIterator.hasNext()) {
        selectedCandidate = candidateIterator.next();
        updateView();
      }
    }
  }

  public void setCountLabelSuffix(String countLabelSuffix) {
    this.countLabelSuffix = countLabelSuffix;

    noCandidatesFoundLabel.setText("No " + StringUtils.capitalize(countLabelSuffix) + " found.");
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.gui.events.OnChangeCandidateCallback;
import com.happydroids.droidtowers.platform.Display;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.ListIterator;

import static com.happydroids.droidtowers.gui.FontManager.Roboto24;

public class JobCandidateListView extends Table {
  private List<JobCandidate> candidates;
  private ListIterator<JobCandidate> candidateIterator;
  private JobCandidate selectedCandidate;

  private Label nameLabel;
  private Label salaryLabel;
  private RatingBar workEthicRating;
  private RatingBar experienceRating;
  private Label candidateCountLabel;
  private String countLabelSuffix;
  private boolean shownNoCandidatesText;
  private OnChangeCandidateCallback onChangeCandidateListener;
  private Table withCandidatesView;
  private Table withoutCandidatesView;
  private Label noCandidatesFoundLabel;
  private List<TextButton> candidateButtons;
  private ColorizedImageButton prevButton;
  private ColorizedImageButton nextButton;


  public JobCandidateListView() {
    super();
    countLabelSuffix = "candidates";
    nameLabel = FontManager.Roboto18.makeLabel("");
    salaryLabel = FontManager.RobotoBold18.makeLabel("");
    workEthicRating = new RatingBar(0, 5);
    experienceRating = new RatingBar(0, 5);
    candidateCountLabel = FontManager.Roboto12.makeLabel("");
    candidateButtons = Lists.newArrayList();

    prevButton = new ColorizedImageButton(TowerAssetManager.textureFromAtlas("large-left-arrow", "hud/menus.txt"), Colors.ICS_BLUE);
    prevButton.addListener(new PreviousCandidateClickListener());

    nextButton = new ColorizedImageButton(TowerAssetManager.textureFromAtlas("large-right-arrow", "hud/menus.txt"), Colors.ICS_BLUE);
    nextButton.addListener(new NextCandidateClickListener());

    withCandidatesView = new Table();
    withCandidatesView.defaults().fill();

    noCandidatesFoundLabel = Roboto24.makeLabel("No " + StringUtils.capitalize(countLabelSuffix) + " found.");
    withoutCandidatesView = new Table();
    withoutCandidatesView.add(noCandidatesFoundLabel).expand().center();
  }

  private Actor makeInnerContentView() {
    Table c = new Table();
    c.defaults().top().left().expandX().fillX().pad(Display.devicePixel(2));

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

    Table left = new Table();
    left.defaults().left().expandX();
    left.add(FontManager.Default.makeLabel("Salary", Color.LIGHT_GRAY));
    left.row();
    left.add(salaryLabel).expandX();

    c.row().spaceTop(Display.devicePixel(16));
    c.add(left);
    for (TextButton button : candidateButtons) {
      c.add(button).width(Display.devicePixel(80));
    }

    c.row().fillX();
    c.add(candidateCountLabel).padTop(Display.devicePixel(20)).colspan(2).right().fillX();

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
      withCandidatesView.clear();
      withCandidatesView.add(prevButton).width(Display.getScaledDensity() * 64).minHeight(128).expand().left();
      withCandidatesView.add(makeInnerContentView());
      withCandidatesView.add(nextButton).width(Display.getScaledDensity() * 64).minHeight(128).expand().right();

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

  public void addCandidateButton(String buttonLabelText, ClickListener listener) {
    TextButton button = FontManager.Default.makeTextButton(buttonLabelText);
    button.addListener(listener);
    candidateButtons.add(button);
  }


  private class PreviousCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(InputEvent event, float x, float y) {
      if (candidateIterator.hasPrevious()) {
        selectedCandidate = candidateIterator.previous();
        updateView();
      }
    }
  }

  private class NextCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(InputEvent event, float x, float y) {
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

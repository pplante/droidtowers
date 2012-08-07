/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.entities.CommercialSpace;
import com.happydroids.droidtowers.generators.JobCandidateGenerator;

public class AvailableJobCandidateDialog extends Dialog {
  private final CommercialSpace commercialSpace;
  private final JobCandidateListView candidateListView;

  public AvailableJobCandidateDialog(CommercialSpace commercialSpace) {
    super();
    this.commercialSpace = commercialSpace;

    candidateListView = new JobCandidateListView();
    candidateListView.setCandidates(JobCandidateGenerator.generate(MathUtils.random(1, 4)));

    addButton("Close", new CloseClickListener());
    addButton("Hire", new HireCandidateClickListener());

    setView(candidateListView);
  }

  private class HireCandidateClickListener extends VibrateClickListener {
    @Override
    public void onClick(InputEvent event, float x, float y) {
      if (commercialSpace.getEmploymentLevel() == 1f) {
        new Dialog()
                .setMessage("There are no more positions available.")
                .show();
      } else {
        JobCandidate selectedCandidate = candidateListView.getSelectedCandidate();
        if (selectedCandidate != null) {
          candidateListView.removeCandidate(selectedCandidate);
          commercialSpace.addEmployee(selectedCandidate);
        }
      }
    }
  }

  private class CloseClickListener extends OnClickCallback {
    @Override
    public void onClick(Dialog dialog) {
      dialog.dismiss();
    }
  }
}

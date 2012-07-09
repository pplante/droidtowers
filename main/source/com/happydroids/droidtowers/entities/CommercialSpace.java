/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.math.MathUtils;
import com.google.common.collect.Sets;
import com.happydroids.droidtowers.employee.JobCandidate;
import com.happydroids.droidtowers.events.EmployeeHiredEvent;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.CommercialSpacePopOver;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.types.CommercialType;
import com.happydroids.droidtowers.utils.Random;

import java.util.Set;

public class CommercialSpace extends Room {
  private int attractedPopulation;
  private int jobsFilled;
  private long lastJobUpdateTime;
  protected Set<JobCandidate> employees;

  public CommercialSpace(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);
    employees = Sets.newHashSet();
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return new CommercialSpacePopOver(this);
  }

  public void updateJobs() {
    jobsFilled = 0;
    if (isConnectedToTransport()) {
      CommercialType commercialType = (CommercialType) getGridObjectType();

      if (Player.instance().getTotalPopulation() > commercialType.getPopulationRequired()) {
        int jobsProvided = commercialType.getJobsProvided();
        if (jobsProvided > 0) {
          jobsFilled = Random.randomInt(jobsProvided / 2, jobsProvided);
        }
      }
    }
  }

  public int getJobsFilled() {
    return employees.size();
  }

  public int getAttractedPopulation() {
    return attractedPopulation;
  }

  @Override
  public float getNoiseLevel() {
    if (jobsFilled > 0) {
      return gridObjectType.getNoiseLevel() * ((float) jobsFilled / ((CommercialType) gridObjectType).getJobsProvided());
    }

    return 0;
  }

  @Override
  public int getCoinsEarned() {
    if (jobsFilled > 0 && isConnectedToTransport()) {
      return (gridObjectType.getCoinsEarned()) * attractedPopulation;
    }

    return 0;
  }

  @Override
  public int getUpkeepCost() {
    if (employees.isEmpty()) {
      return 0;
    }

    int totalSalaries = 0;
    for (JobCandidate employee : employees) {
      totalSalaries += employee.getSalary();
    }

    return totalSalaries;
  }

  @Override
  public float getDesirability() {
    if (canEmployDroids() && getEmployees().isEmpty()) {
      return 0f;
    }

    return MathUtils.clamp(super.getDesirability() - (0.1f * getNumVisitors()), 0f, 1f);
  }

  public float getEmploymentLevel() {
    int jobsProvided = ((CommercialType) gridObjectType).getJobsProvided();

    if (jobsProvided > 0) {
      return employees.size() / (float) jobsProvided;
    }

    return 0;
  }

  public int getJobsProvided() {
    return ((CommercialType) gridObjectType).getJobsProvided();
  }

  public float getAttractedPopulationLevel() {
    int populationAttraction = ((CommercialType) gridObjectType).getPopulationAttraction();
    if (populationAttraction == 0) {
      return 0.0f;
    }

    return attractedPopulation / populationAttraction;
  }

  @Override
  public void update(float deltaTime) {
    super.update(deltaTime);

    if (canEmployDroids()) {
      if (employees.size() == 0) {
        decalsToDraw.add(DECAL_NEEDS_DROIDS);
      } else {
        decalsToDraw.remove(DECAL_NEEDS_DROIDS);
      }
    }
  }

  protected boolean canEmployDroids() {
    return true;
  }

  public void addEmployee(JobCandidate selectedCandidate) {
    employees.add(selectedCandidate);
    gameGrid.events().post(new EmployeeHiredEvent(this, selectedCandidate));
  }

  public Set<JobCandidate> getEmployees() {
    return employees;
  }

  public void setEmployees(Set<JobCandidate> employees) {
    this.employees.clear();

    for (JobCandidate employee : employees) {
      addEmployee(employee);
    }
  }
}

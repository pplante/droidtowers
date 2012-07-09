/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
  @JsonIgnore
  private static Player instance;

  private long coins;
  private long experience;
  private int jobsFilled;
  private int jobsMax;
  private int populationAttracted;
  private int maxPopulation;
  private int populationMax;
  private int supportedResidency;
  private int populationResidency;
  private int currentIncome;
  private int currentExpenses;
  private float starRating;
  private float budgetRating;
  private float employmentRating;
  private float populationRating;
  private float desirabilityRating;

  public static Player instance() {
    if (instance == null) {
      instance = new Player();
    }

    return instance;
  }

  private Player() {
    coins = 4000;
  }

  public Player(int startingMoney) {
    coins = startingMoney;
  }

  public static void setInstance(Player newInstance) {
    Player.instance = newInstance;
  }

  public long getCoins() {
    return coins;
  }

  public void subtractCurrency(long coins) {
    this.coins -= coins;
  }

  public void addCurrency(long coins) {
    this.coins += coins;
  }

  public void addExperience(int exp) {
    experience += exp;
  }

  public long getExperience() {
    return experience;
  }

  public int getPopulationResidency() {
    return populationResidency;
  }

  public int getPopulationAttracted() {
    return populationAttracted;
  }

  public int getJobsMax() {
    return this.jobsMax;
  }

  public int getJobsFilled() {
    return jobsFilled;
  }

  @JsonIgnore
  public int getTotalPopulation() {
    return populationResidency;
  }

  public int getMaxPopulation() {
    return populationMax;
  }

  public void setPopulationMax(int populationMax) {
    this.populationMax = populationMax;
  }

  public void setPopulationResidency(int populationResidency) {
    this.populationResidency = populationResidency;
  }

  public void setJobsMax(int jobsMax) {
    this.jobsMax = jobsMax;
  }

  public void setPopulationAttracted(int populationAttracted) {
    this.populationAttracted = populationAttracted;
  }

  public void setJobsFilled(int jobsFilled) {
    this.jobsFilled = jobsFilled;
  }

  public void setCurrentIncome(int currentIncome) {
    this.currentIncome = currentIncome;
  }

  public void setCurrentExpenses(int currentUpkeep) {
    this.currentExpenses = currentUpkeep;
  }

  public int getCurrentIncome() {
    return currentIncome;
  }

  public int getCurrentExpenses() {
    return currentExpenses;
  }

  public float getStarRating() {
    return starRating;
  }

  public void setStarRating(float starRating) {
    this.starRating = starRating;
  }

  public float getBudgetRating() {
    return budgetRating;
  }

  public void setBudgetRating(float budgetRating) {
    this.budgetRating = budgetRating;
  }


  public void setEmploymentRating(float employmentRating) {
    this.employmentRating = employmentRating;
  }

  public void setPopulationRating(float populationRating) {
    this.populationRating = populationRating;
  }


  public void setDesirabilityRating(float desirabilityRating) {
    this.desirabilityRating = desirabilityRating;
  }

  public float getDesirabilityRating() {
    return desirabilityRating;
  }

  public float getEmploymentRating() {
    return employmentRating;
  }

  public float getPopulationRating() {
    return populationRating;
  }

  public void setSupportedResidency(int supportedResidency) {
    this.supportedResidency = supportedResidency;
  }

  public int getSupportedResidency() {
    return supportedResidency;
  }
}

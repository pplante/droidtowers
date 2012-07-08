/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.employee;

import com.badlogic.gdx.math.MathUtils;

public class JobCandidate {
  private String name;
  private Gender gender;
  private double workEthic;
  private double experienceLevel;
  private double salaryRequired;


  public void setName(String name) {
    this.name = name;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public String getName() {
    return name;
  }

  public void randomizeAttributes() {
    workEthic = MathUtils.random(5f);
    experienceLevel = MathUtils.random(5f);

    salaryRequired = 2500f * ((workEthic + experienceLevel) / 10f);
  }

  public double getExperienceLevel() {
    return experienceLevel;
  }

  public double getWorkEthic() {
    return workEthic;
  }

  public double getSalary() {
    return salaryRequired;
  }
}

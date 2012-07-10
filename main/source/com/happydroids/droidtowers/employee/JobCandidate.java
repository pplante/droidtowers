/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.employee;

import com.badlogic.gdx.math.MathUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.happydroids.droidtowers.entities.Avatar;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCandidate {
  protected String name;
  protected Gender gender;
  protected double workEthic;
  protected double experienceLevel;
  protected double salary;
  private Avatar avatar;

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

    salary = 2500f * ((workEthic + experienceLevel) / 10f);
  }

  public double getExperienceLevel() {
    return experienceLevel;
  }

  public double getWorkEthic() {
    return workEthic;
  }

  public double getSalary() {
    return salary;
  }

  @JsonIgnore
  public void setAvatar(Avatar avatar) {
    this.avatar = avatar;
  }

  public Avatar getAvatar() {
    return avatar;
  }

  public boolean hasAvatar() {
    return avatar != null;
  }

  public Gender getGender() {
    return gender;
  }
}

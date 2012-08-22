/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PlayerProfile extends TowerGameServiceObject {
  private String firstName;
  private String lastName;

  @Override
  public String getBaseResourceUri() {
    throw new RuntimeException("PlayerProfile cannot be directly accessed!");
  }

  @Override
  protected boolean requireAuthentication() {
    return true;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public String toString() {
    return "PlayerProfile{" +
                   "firstName='" + firstName + '\'' +
                   ", lastName='" + lastName + '\'' +
                   '}';
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }
}

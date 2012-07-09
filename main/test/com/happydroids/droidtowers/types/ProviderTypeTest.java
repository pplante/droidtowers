/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.happydroids.droidtowers.NonGLTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.happydroids.droidtowers.Expect.expect;

@RunWith(NonGLTestRunner.class)
public class ProviderTypeTest {
  @Test
  public void matches_shouldMatchExactTypes() {
    expect(ProviderType.APARTMENT.matches(ProviderType.APARTMENT)).toBeTrue();
    expect(ProviderType.APARTMENT.matches(ProviderType.OFFICE_SERVICES)).toBeFalse();
  }

  @Test
  public void matches_shouldMatchSubTypes() {
    expect(ProviderType.HOUSING.matches(ProviderType.APARTMENT)).toBeTrue();
    expect(ProviderType.HOUSING.matches(ProviderType.OFFICE_SERVICES)).toBeFalse();
  }
}

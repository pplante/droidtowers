/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.generators;

import com.badlogic.gdx.math.MathUtils;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.employee.Gender;
import com.happydroids.droidtowers.employee.JobCandidate;

import java.util.List;

public class JobCandidateGenerator {
  public static List<JobCandidate> generate(int numToGenerate) {
    List<JobCandidate> candidates = Lists.newArrayList();

    for (int i = 0; i < numToGenerate; i++) {
      JobCandidate candidate = new JobCandidate();
      candidate.setGender(MathUtils.random(10) > 5 ? Gender.FEMALE : Gender.MALE);
      candidate.setName(generateUniqueName(candidates, candidate.getGender()));
      candidate.randomizeAttributes();

      candidates.add(candidate);
    }

    return candidates;
  }

  private static String generateUniqueName(List<JobCandidate> candidates, Gender gender) {
    boolean hasDupes = true;
    while (hasDupes) {
      String name = gender.equals(Gender.FEMALE) ? NameGenerator.randomFemaleName() : NameGenerator.randomMaleName();
      hasDupes = false;
      for (JobCandidate candidate : candidates) {
        if (candidate.getName().equalsIgnoreCase(name)) {
          hasDupes = true;
        }
      }

      if (!hasDupes) {
        return name;
      }
    }

    return null;
  }
}

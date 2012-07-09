/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.generators;

import com.google.common.collect.Lists;
import com.happydroids.droidtowers.employee.Gender;
import com.happydroids.droidtowers.employee.JobCandidate;

import java.util.List;

public class JobCandidateGenerator {
  public static List<JobCandidate> generate(int numToGenerate) {
    List<JobCandidate> candidates = Lists.newArrayList();

    for (int i = 0; i < numToGenerate; i++) {
      JobCandidate candidate = new JobCandidate();
      candidate.setGender(Gender.FEMALE);
      candidate.setName(generateUniqueName(candidates));
      candidate.randomizeAttributes();

      candidates.add(candidate);
    }

    return candidates;
  }

  private static String generateUniqueName(List<JobCandidate> candidates) {
    boolean hasDupes = true;
    while (hasDupes) {
      String name = NameGenerator.randomFemaleName();
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

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

public enum ProviderType {
  NONE,
  LOBBY,
  SKY_LOBBY,
  FOOD,
  ENTERTAINMENT,
  HOTEL_ROOMS,
  OFFICE_SERVICES,
  JANITORS,
  MAIDS,
  SECURITY,
  APARTMENT,
  CONDO,
  PENTHOUSE,
  STAIRS,
  ELEVATOR,
  SERVICE_ELEVATOR,
  RESTROOM,
  PARKING_RAMP,
  PARKING_SPACE,

  HOUSING(APARTMENT, CONDO, PENTHOUSE),
  COMMERCIAL(ENTERTAINMENT, FOOD, OFFICE_SERVICES, HOTEL_ROOMS),
  TRANSPORT(STAIRS, ELEVATOR),
  PARKING(PARKING_RAMP, PARKING_SPACE),
  SERVICE(MAIDS, JANITORS, SECURITY, RESTROOM, PARKING);

  private final ProviderType[] subTypes;

  ProviderType(ProviderType... subTypes) {
    this.subTypes = subTypes;
  }

  ProviderType() {
    subTypes = null;
  }

  public boolean hasSubTypes() {
    return subTypes != null;
  }

  public boolean matchesSubType(ProviderType... otherTypes) {
    if (subTypes != null) {
      for (int i = 0, subTypesLength = subTypes.length; i < subTypesLength; i++) {
        ProviderType subType = subTypes[i];
        for (int i1 = 0, otherTypesLength = otherTypes.length; i1 < otherTypesLength; i1++) {
          ProviderType otherType = otherTypes[i1];
          if (otherType.equals(subType)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  public boolean matches(ProviderType... types) {
    for (int i = 0, typesLength = types.length; i < typesLength; i++) {
      ProviderType type = types[i];
      if (type.equals(this) || type.matchesSubType(this)) {
        return true;
      }
    }

    return matchesSubType(types);
  }
}

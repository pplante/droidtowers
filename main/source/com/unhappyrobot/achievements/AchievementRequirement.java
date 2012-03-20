package com.unhappyrobot.achievements;

import com.unhappyrobot.entities.Player;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class AchievementRequirement {
  private RequirementType type;
  private AchievementThing thing;
  private double amount;

  public boolean isCompleted() {
    switch (type) {
      case POPULATION:
        return Player.instance().getTotalPopulation() >= amount;

      case BUILD:
//        return handleBuildRequirement();

      default:
        assert false;
        break;
    }

    return false;
  }

//  private boolean handleBuildRequirement() {
//    if (thing == null) {
//      throw new RuntimeException(String.format("AchievementRequirement %s does not contain 'thing' parameter.", type));
//    }
//
//    GuavaSet<GridObject> gridObjects = null;
//    Predicate<GridObject> gridObjectPredicate = null;
//
//    switch (thing) {
//      case HOTEL_ROOM:
//        gridObjects = GameScreen.getGameGrid().getInstancesOf(Room.class, CommercialSpace.class);
//        gridObjectPredicate = new Predicate<GridObject>() {
//          public boolean apply(@Nullable GridObject gridObject) {
//            return gridObject instanceof Room && ((RoomType) gridObject.getGridObjectType()).provides() == ProviderType.HOTEL_ROOMS;
//          }
//        };
//        break;
//
//      case COMMERCIAL_SPACE:
//        gridObjects = GameScreen.getGameGrid().getInstancesOf(CommercialSpace.class);
//        gridObjectPredicate = new Predicate<GridObject>() {
//          public boolean apply(@Nullable GridObject gridObject) {
//            ProviderType providerType = ((CommercialType) gridObject.getGridObjectType()).provides();
//            return gridObject instanceof CommercialSpace && (providerType == ProviderType.OFFICE_SERVICES || providerType == ProviderType.FOOD);
//          }
//        };
//        break;
//    }
//
//    return gridObjects != null && gridObjectPredicate != null && gridObjects.filterBy(gridObjectPredicate).size() >= amount;
//  }

  @Override
  public String toString() {
    return "AchievementRequirement{" +
                   "amount=" + amount +
                   ", type=" + type +
                   ", thing=" + thing +
                   '}';
  }
}

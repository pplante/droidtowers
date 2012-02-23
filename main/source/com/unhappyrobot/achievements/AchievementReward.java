package com.unhappyrobot.achievements;

import com.unhappyrobot.entities.Player;
import com.unhappyrobot.types.ProviderType;
import com.unhappyrobot.types.ServiceRoomType;
import com.unhappyrobot.types.ServiceRoomTypeFactory;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AchievementReward {
  private RewardType type;
  private AchievementThing thing;
  private double amount;

  public void give() {
    switch (type) {
      case GIVE:
        handleGiveReward();
        break;
      case UNLOCK:
        handleUnlockReward();
        break;
    }
  }

  private void handleUnlockReward() {
    if (thing == null) {
      throw new RuntimeException(String.format("AchievementReward %s does not contain 'thing' parameter.", type));
    }
    System.out.println("thing = " + thing);
    switch (thing) {
      case MAID_CLOSET:
        for (ServiceRoomType serviceRoomType : ServiceRoomTypeFactory.getInstance().all()) {
          if (serviceRoomType.provides() == ProviderType.MAIDS) {
            serviceRoomType.setLocked(false);
          }
        }
        break;
    }
  }

  private void handleGiveReward() {
    if (thing == null) {
      throw new RuntimeException(String.format("AchievementReward %s does not contain 'thing' parameter.", type));
    }

    switch (thing) {
      case MONEY:
        Player.instance().addCurrency((int) amount);
        break;
    }
  }

  @Override
  public String toString() {
    return "AchievementReward{" +
                   "amount=" + amount +
                   ", type=" + type +
                   ", thing=" + thing +
                   '}';
  }


  public RewardType getType() {
    return type;
  }

  public AchievementThing getThing() {
    return thing;
  }
}

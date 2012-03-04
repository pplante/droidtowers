package com.unhappyrobot.achievements;

import com.google.common.collect.Lists;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.types.ProviderType;
import com.unhappyrobot.types.ServiceRoomType;
import com.unhappyrobot.types.ServiceRoomTypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AchievementReward {
  private RewardType type;
  private AchievementThing thing;
  private double amount;

  public AchievementReward() {

  }

  public AchievementReward(RewardType type, AchievementThing thing) {
    this(type, thing, 0);
  }

  public AchievementReward(RewardType type, AchievementThing thing, int amount) {
    this.type = type;
    this.thing = thing;
    this.amount = amount;
  }

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

    switch (thing) {
      case MAIDS_OFFICE:
        for (ServiceRoomType serviceRoomType : ServiceRoomTypeFactory.instance().all()) {
          if (serviceRoomType.provides() == ProviderType.MAIDS) {
            serviceRoomType.setLocked(false);
          }
        }
        break;
      case JANITORS_CLOSET:
        for (ServiceRoomType serviceRoomType : ServiceRoomTypeFactory.instance().all()) {
          if (serviceRoomType.provides() == ProviderType.JANITORS) {
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

  public double getAmount() {
    return amount;
  }

  public String getFormattedString() {
    List<String> parts = Lists.newArrayList();
    if (type != null) {
      parts.add(type.displayString);
    }

    if (thing != null) {
      parts.add(thing.displayString);
    }

    if (amount > 0) {
      parts.add("" + (int) amount);
    }

    return StringUtils.join(parts, " ") + "\n";
  }
}

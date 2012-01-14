package com.unhappyrobot.events;

import java.util.EventObject;

public abstract class EventListener {
  public void receiveEvent(EventObject event) {
    // thrown away by default
  }
}

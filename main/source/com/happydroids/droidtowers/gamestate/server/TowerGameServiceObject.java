/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.happydroids.server.ApiRunnable;
import com.happydroids.server.HappyDroidServiceObject;
import com.happydroids.server.HttpStatusCode;

@SuppressWarnings("unchecked")
public abstract class TowerGameServiceObject extends HappyDroidServiceObject {
  @Override
  protected boolean beforeSaveValidation(ApiRunnable afterSave) {
    if (!super.beforeSaveValidation(afterSave)) {
      return false;
    }

    if (requireAuthentication() && !TowerGameService.instance().isAuthenticated()) {
      afterSave.onError(null, HttpStatusCode.NetworkAuthenticationRequired, this);
      return false;
    }

    return true;
  }
}

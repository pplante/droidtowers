/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import net.kencochrane.sentry.RavenClient;
import net.kencochrane.sentry.RavenUtils;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.json.simple.JSONObject;

public class RavenReportSender implements ReportSender {
  @Override public void send(CrashReportData data) throws ReportSenderException {
    RavenClient ravenClient = new RavenClient(HappyDroidConsts.SENTRY_DSN);
    JSONObject extra = new JSONObject();
    extra.put("app_version", data.getProperty(ReportField.APP_VERSION_CODE));
    extra.put("android_version", data.getProperty(ReportField.ANDROID_VERSION));
    extra.put("available_mem_size", data.getProperty(ReportField.AVAILABLE_MEM_SIZE));
    extra.put("total_mem_size", data.getProperty(ReportField.TOTAL_MEM_SIZE));
    extra.put("user_email", data.getProperty(ReportField.USER_EMAIL));
    extra.put("user_comment", data.getProperty(ReportField.USER_COMMENT));
    extra.put("display", data.getProperty(ReportField.DISPLAY));
    if (TowerGameService.hasBeenInitialised()) {
      extra.put("device_id", TowerGameService.instance().getDeviceId());
    }
    extra.put("device_build", data.getProperty(ReportField.BUILD));
    extra.put("device_brand", data.getProperty(ReportField.BRAND));
    extra.put("device_product", data.getProperty(ReportField.PRODUCT));
    extra.put("device_model", data.getProperty(ReportField.PHONE_MODEL));

    ravenClient.captureException(data.getProperty(ReportField.STACK_TRACE), RavenUtils.getTimestampLong(), "root", 50, null, null, extra);
  }
}

/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import android.app.Application;
import com.happydroids.android.R;
import com.happydroids.platform.RavenReportSender;
import org.acra.*;
import org.acra.annotation.ReportsCrashes;

import static org.acra.ReportField.*;

@ReportsCrashes(formKey = "")
public class DroidTowersAndroidApplication extends Application {
  @Override
  public void onCreate() {
    // The following line triggers the initialization of ACRA
    ACRA.init(this);
    ACRA.getErrorReporter().setReportSender(new RavenReportSender());

    ACRAConfiguration conf = ACRA.getNewDefaultConfig();
    try {
      conf.setCustomReportContent(new ReportField[]{DISPLAY, USER_COMMENT, USER_EMAIL, TOTAL_MEM_SIZE, AVAILABLE_MEM_SIZE});
      conf.setResDialogCommentPrompt(R.string.crash_dialog_comment_prompt);
      conf.setResToastText(R.string.crash_toast_text);
      conf.setResDialogText(R.string.crash_dialog_text);
      conf.setResDialogEmailPrompt(R.string.crash_dialog_email_prompt);
      conf.setResDialogTitle(R.string.crash_dialog_title);
      conf.setResDialogOkToast(R.string.crash_dialog_ok_toast);
      conf.setMode(ReportingInteractionMode.DIALOG);
    } catch (ACRAConfigurationException e) {
      e.printStackTrace();
    }

    ACRA.setConfig(conf);

    super.onCreate();
  }

}

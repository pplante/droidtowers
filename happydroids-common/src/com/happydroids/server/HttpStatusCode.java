/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

public class HttpStatusCode {
  public static int Success = 200;
  public static int ClientClosedRequest = 499;
  public static int ClientValidationFailed = 600;
  public static int NetworkAuthenticationRequired = 511;

  public static int Unknown = 100000;
}

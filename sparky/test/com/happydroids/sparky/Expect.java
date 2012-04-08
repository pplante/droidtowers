/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

import com.pivotallabs.greatexpectations.matchers.*;

import java.io.InputStream;

import static com.pivotallabs.greatexpectations.GreatExpectations.wrapped;

@SuppressWarnings("unchecked")
public class Expect {
  public static <T, M extends ObjectMatcher<T, M>> ObjectMatcher<T, ?> expect(T actual) {
    return wrapped(ObjectMatcher.class, actual);
  }

  public static BooleanMatcher<Boolean, ?> expect(boolean actual) {
    return wrapped(BooleanMatcher.class, actual);
  }

  public static <T extends Boolean, M extends BooleanMatcher<T, M>> BooleanMatcher<T, ?> expect(T actual) {
    return wrapped(BooleanMatcher.class, actual);
  }

  public static <T extends Comparable, M extends ComparableMatcher<T, M>> ComparableMatcher<T, ?> expect(T actual) {
    return wrapped(ComparableMatcher.class, actual);
  }

  public static <T extends java.util.Date, M extends DateMatcher<T, M>> DateMatcher<T, ?> expect(T actual) {
    return wrapped(DateMatcher.class, actual);
  }

  public static <T extends Iterable<X>, X, M extends IterableMatcher<T, X, M>> IterableMatcher<T, X, ?> expect(T actual) {
    return wrapped(IterableMatcher.class, actual);
  }

  public static <T extends String, M extends StringMatcher<T, M>> StringMatcher<T, ?> expect(T actual) {
    return wrapped(StringMatcher.class, actual);
  }

  public static <T extends InputStream, M extends InputStreamMatcher<T, M>> InputStreamMatcher<T, ?> expect(T actual) {
    return wrapped(InputStreamMatcher.class, actual);
  }
}

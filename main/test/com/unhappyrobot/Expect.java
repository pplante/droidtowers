package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.pivotallabs.greatexpectations.matchers.*;

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

  public static <T extends Vector2, M extends Vector2Matcher<T, M>> Vector2Matcher<T, ?> expect(T actual) {
    return wrapped(Vector2Matcher.class, actual);
  }

  public static <T extends Actor, M> ActorMatcher<T, ?> expect(T actual) {
    return wrapped(ActorMatcher.class, actual);
  }

  public static <T extends Group, M> GroupMatcher<T, ?> expect(T actual) {
    return wrapped(GroupMatcher.class, actual);
  }
}

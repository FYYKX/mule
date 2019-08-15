/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.internal.policy;

import org.mule.runtime.api.component.execution.CompletableCallback;
import org.mule.runtime.api.util.Reference;
import org.mule.runtime.api.util.concurrent.Latch;

import java.util.function.Consumer;

public final class SourcePolicyTestUtils {
  private SourcePolicyTestUtils(){}

  public static <T> T block(Consumer<CompletableCallback<T>> callbackConsumer) throws Throwable {
    Reference<T> valueReference = new Reference<>();
    Reference<Throwable> exceptionReference = new Reference<>();
    Latch latch = new Latch();

    CompletableCallback<T> callback = new CompletableCallback<T>() {

      @Override
      public void complete(T value) {
        valueReference.set(value);
        latch.release();
      }

      @Override
      public void error(Throwable e) {
        exceptionReference.set(e);
        latch.release();
      }
    };

    callbackConsumer.accept(callback);
    latch.await();
    if (valueReference.get() != null) {
      return valueReference.get();
    }

    throw exceptionReference.get();
  }
}

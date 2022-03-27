// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.intellij.sdk.project.model;

import static org.intellij.sdk.project.model.XDebuggerTestUtil.print;

import com.intellij.openapi.util.Pair;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.SmartList;
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.function.BiFunction;

public class XTestContainer<T> {
  private final List<T> myChildren = new CopyOnWriteArrayList<>();
  private String myErrorMessage;
      private final Semaphore myFinished = new Semaphore(0);
//    private final CountDownLatch latch = new CountDownLatch(1);

  public void addChildren(List<? extends T> children, boolean last) {
    myChildren.addAll(children);
    print("Children: " + children);
    if (last) {
        print("Releasing in addChildren..." + myFinished);
        myFinished.release();
        print("Is Released in addChildren" + myFinished);
    }
  }

  public void tooManyChildren(int remaining) {
//      print("Releasing on too many addChildren..." + myFinished);
//      myFinished.release();
//      print("Released on too many addChildren..." + myFinished);
  }

  public void setMessage(@NotNull String message, Icon icon, @NotNull final SimpleTextAttributes attributes, @Nullable XDebuggerTreeNodeHyperlink link) {
  }

  public void setErrorMessage(@NotNull String message, @Nullable XDebuggerTreeNodeHyperlink link) {
    setErrorMessage(message);
  }

  public void setErrorMessage(@NotNull String errorMessage) {
    myErrorMessage = errorMessage;
    print("Releasing on set error Message..."+ myFinished);
    myFinished.release();
    print("Released on set error Message..."+ myFinished);
  }

  @NotNull
  public Pair<List<T>, String> waitFor(long timeoutMs) {
//    return waitFor(timeoutMs, (semaphore, timeout) -> XDebuggerTestUtil.waitFor(myFinished, timeout));
      return Pair.create(null, null);
  }

  @NotNull
  public Pair<List<T>, String> waitFor(long timeoutMs, BiFunction<? super Semaphore, ? super Long, Boolean> waitFunction) {
      print("Try acquire for "+ myFinished);
      if (!waitFunction.apply(myFinished, timeoutMs)) {
          throw new AssertionError("Waiting timed out" + this);
      }
//      try {
//          latch.await();
//      } catch (InterruptedException e) {
//          print("interrupted excpetion" + e);
//      }

    return Pair.create(myChildren, myErrorMessage);
  }

    @NotNull
    public Pair<List<T>, String> getChildren() {
        return Pair.create(myChildren, myErrorMessage);
    }
}

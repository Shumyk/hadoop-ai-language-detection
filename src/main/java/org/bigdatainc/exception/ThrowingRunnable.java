package org.bigdatainc.exception;

@FunctionalInterface
public interface ThrowingRunnable {
  void run() throws Exception;
}

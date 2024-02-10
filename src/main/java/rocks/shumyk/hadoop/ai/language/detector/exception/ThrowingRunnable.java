package rocks.shumyk.hadoop.ai.language.detector.exception;

@FunctionalInterface
public interface ThrowingRunnable {
  void run() throws Exception;
}

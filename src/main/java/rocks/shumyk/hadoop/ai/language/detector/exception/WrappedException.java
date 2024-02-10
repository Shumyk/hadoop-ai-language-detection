package rocks.shumyk.hadoop.ai.language.detector.exception;

public class WrappedException extends RuntimeException {
  public WrappedException(final Exception e) {
    super(e);
  }

  public static void wrap(final ThrowingRunnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      throw new WrappedException(e);
    }
  }
}

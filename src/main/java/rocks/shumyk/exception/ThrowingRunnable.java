package rocks.shumyk.exception;

@FunctionalInterface
public interface ThrowingRunnable {
  void run() throws Exception;
}

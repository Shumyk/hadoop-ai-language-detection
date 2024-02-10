package rocks.shumyk.hadoop.ai.language.detector.model.enums;

import java.util.Arrays;

public enum RunMode {
  TRAIN, DETECT;

  public static RunMode resolve(final String[] args) {
    return switch (args.length) {
      case 2 -> DETECT; // default
      case 3 -> {
        final String modeArg = args[2].toUpperCase();
        yield Arrays.stream(RunMode.values())
            .filter(mode -> mode.name().equals(modeArg))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Illegal Run Mode argument (third). Possible options: train, detect")
            );
      }
      default -> throw new IllegalArgumentException(
          "Unexpected number of arguments: %s. Expected two or three arguments provided.".formatted(Arrays.toString(args))
      );
    };
  }
}

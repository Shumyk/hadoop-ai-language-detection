package rocks.shumyk.hadoop.ai.language.detector.detection.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class DetectionStats implements Writable {
  private static final String STAT_HEADER = "\n\t\t\tN. Probability : Input Text\n";
  private static final String STAT_SPLITTER = "\t\t\t---------------------------\n";
  private static final String STAT_LINE = "\t%5d. %s\n";

  private final AtomicLong lineTracker = new AtomicLong(1);
  private final List<String> stats = new ArrayList<>();

  public void add(final Text line) {
    stats.add(STAT_LINE.formatted(lineTracker.getAndIncrement(), line.toString()));
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeUTF(STAT_HEADER);
    out.writeUTF(STAT_SPLITTER);
    for (final String stat : stats)
      out.writeUTF(stat);
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    in.readLine();
    in.readLine();
    String nextLine = in.readLine();
    while (null != nextLine) {
      stats.add(nextLine);
      nextLine = in.readLine();
    }
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder(STAT_HEADER).append(STAT_SPLITTER);
    for (final String stat : stats)
      result.append(stat);
    return result.toString();
  }
}

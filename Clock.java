import java.text.DecimalFormat;
import java.util.stream.IntStream;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * Clock class for keeping track of time in the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 2.0
 * @since 1.0
 */
class Clock extends SwingWorker<Integer, Integer> {
  private JLabel display;

  public static class Seed {
    public int time = 0;
  }

  private Seed seed;
  // Time is 10 minute
  private int time = 0;
  private int _hour = 0;
  private final int[] morningRush = {7, 8, 9};
  private final int[] eveningRush = {17, 18, 19};
  private final DecimalFormat df = new DecimalFormat("00");

  /**
   * Constructor
   *
   * @param label (required) label to output info too
   * @param seed (required) a seed to begin the clock with and keep state
   */
  public Clock(JLabel label, Seed seed) {
    this.time = seed.time % 60;
    this._hour = (seed.time - this.time) / 60;
    this.seed = seed;
    this.display = label;
  }

  /**
   * Checks the minutes passed
   *
   * @return int of minutes passed
   */
  public int getMinutes() {
    return (this.time % 60);
  }

  /**
   * Checks the hours passed
   *
   * @return int of hours passed
   */
  public int getHour() {
    return this._hour % 24;
  }

  /**
   * Check if it is the morning Rush
   *
   * @return true if it is the morning rush
   */
  public boolean isMorningRush() {
    return IntStream.of(this.morningRush).anyMatch(x -> x == this.getHour());
  }

  /**
   * Check if it is the evening Rush
   *
   * @return true if it is the evening rush
   */
  public boolean isEveningRush() {
    return IntStream.of(this.eveningRush).anyMatch(x -> x == this.getHour());
  }

  /**
   * Override the workers doInBackground method Clock to keep track of time in car park.
   *
   * <p>Simulating 10 minutes every 1 second.
   */
  @Override
  protected Integer doInBackground() throws Exception {
    while (!this.isCancelled()) {
      this.time += 1;
      if (this.time % 60 == 0) {
        this._hour += 1;
      }
      this.seed.time = (this._hour * 60) + this.time;
      try {
        this.display.setText(
            "The time in the CarPark is "
                + df.format(this.getHour())
                + ":"
                + df.format((this.getMinutes())));
        // 100 is .1 second real time
        // 1 second real time is 10 min in simulation
        Thread.sleep((100));
      } catch (InterruptedException e) {
      }
    }
    return (this._hour * 60) + this.time;
  }
}

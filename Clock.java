import java.text.DecimalFormat;
import java.util.stream.IntStream;
import javax.swing.JLabel;

/**
 * Clock class for keeping track of time in the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Clock extends Thread {
  private JLabel display;
  private boolean start = false;
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
   */
  public Clock(JLabel label) {
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

  /** stop the process in the thread */
  public void kill() {
    this.start = false;
  }

  /**
   * Override the threads run method Clock to keep track of time in car park.
   *
   * <p>Simulating 10 minutes every 1 second.
   */
  @Override
  public void run() {
    this.start = true;
    while (this.start) {
      this.time += 1;
      if (this.time % 60 == 0) {
        this._hour += 1;
      }
      try {
        this.display.setText(
            "The time in the CarPark is "
                + df.format(this.getHour())
                + ":"
                + df.format((this.getMinutes())));
        // 1000 is 1 second real time
        // 1 second real time is 10 min in simulation
        sleep((100));
      } catch (InterruptedException e) {
      }
    }
  }
}

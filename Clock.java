import java.text.DecimalFormat;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.SwingWorker;

/**
 * Clock class for keeping track of time in the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 2.0
 * @since 1.0
 */
class Clock extends SwingWorker<Integer, String> {
  private JLabel display;
  private static DecimalFormat df = new DecimalFormat("00");
  // Time is 10 minute
  private int time = 0;
  private int hour = 0;
  private int[] morningRush = {7, 8, 9};
  private int[] eveningRush = {17, 18, 19};

  /** Constructor */
  public Clock(JLabel label) {
    this.display = label;
  }

  /**
   * Checks the minutes passed
   *
   * @return int of minutes passed
   */
  public int getMinutes() {
    return (this.time % 6) * 10;
  }

  /**
   * Checks the hours passed
   *
   * @return int of hours passed
   */
  public int getHour() {
    return this.hour % 24;
  }

  /**
   * Check if it is the morning Rush
   *
   * @return true if it is the morning rush
   */
  public boolean isMorningRush() {
    return IntStream.of(this.morningRush).anyMatch(x -> x == this.hour);
  }

  /**
   * Check if it is the evening Rush
   *
   * @return true if it is the evening rush
   */
  public boolean isEveningRush() {
    return IntStream.of(this.eveningRush).anyMatch(x -> x == this.hour);
  }

  /**
   * Override the workers doInBackground method Clock to keep track of time in car park.
   *
   * <p>Simulating 10 minutes every 1 second.
   */
  @Override
  protected Integer doInBackground() throws Exception {
    while (true) {
      this.time += 1;
      if (this.time % 60 == 0) {
        this.hour++;
      }
      try {
        display.setText(
            "The time in the CarPark is "
                + df.format(this.hour % 24)
                + ":"
                + df.format((this.time % 60)));
        // 100 is .1 second real time
        // 1 second real time is 10 min in simulation
        Thread.sleep((100));
      } catch (InterruptedException e) {
      }
    }
  }
}

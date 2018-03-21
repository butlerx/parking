import java.util.*;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * Exit class for letting cars leave the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 2.0
 * @since 1.0
 */
class ParkExit extends SwingWorker<Integer, String> {
  private JLabel display;
  private Valet valet;
  private Clock clock;
  private int number;

  /**
   * Constructor
   *
   * @param v (required) The Valet to remove the car
   * @param i (required) The exit number should be unique
   * @param cl (required) The shared clock between all the threads
   */
  public ParkExit(Valet v, int i, Clock cl, JLabel label) {
    this.display = label;
    this.valet = v;
    this.clock = cl;
    this.number = i;
  }

  /**
   * Override the workers doInBackground method
   *
   * <p>Lets cars leave the carpark
   *
   * <p>Randomly delays a car leaving carpark
   *
   * <p>Increases Number of cars leaving during evening rush
   *
   * <p>Decreases Number of cars leaving during Morning rush
   */
  @Override
  protected Integer doInBackground() throws Exception {
    Random delay = new Random();
    while (!this.isCancelled()) {
      // Signal valet to remove a car
      this.valet.leave();
      try {
        if (delay.nextInt(50) == 21) {
          // Car is delayed, check for how long
          this.display.setText("exit " + this.number + "obstructed");
          Thread.sleep(delay.nextInt(5000));
        }
        Thread.sleep(
            Math.abs(
                (clock.isEveningRush())
                    // Increase number of cars trying to leave during the Evening rush
                    ? (delay.nextInt(150) + 1)
                    : (clock.isMorningRush())
                        ?
                        // Less Cars will be trying to leave during the morning rush
                        1000 * (delay.nextInt(24 - clock.getHour()) + 1)
                        : 100 * (delay.nextInt(24 - clock.getHour()) + 1)));
      } catch (InterruptedException e) {
      }
    }
    return 0;
  }
}

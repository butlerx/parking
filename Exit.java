import java.util.*;
import javax.swing.JLabel;

/**
 * Exit class for letting cars leave the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Exit extends Thread {
  private JLabel display;
  private CarPark carPark;
  private Clock clock;
  private int number;
  private boolean obstruction = false;
  private boolean start;

  /**
   * Constructor
   *
   * @param c (required) The car park to remove cars from
   * @param i (required) The exit number should be unique
   * @param cl (required) The shared clock between all the threads
   * @param label (required) label to output too
   */
  public Exit(CarPark c, int i, Clock cl, JLabel label) {
    this.carPark = c;
    this.number = i;
    this.clock = cl;
    this.display = label;
  }

  /**
   * Check if there is a delay at the exit
   *
   * @return true if there is a delay at the exit
   */
  public boolean checkObstruction() {
    return this.obstruction;
  }

  /** stop the process in the thread */
  public void kill() {
    this.start = false;
  }

  /**
   * Override the threads run method
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
  public void run() {
    this.start = true;
    Random delay = new Random();
    while (this.start) {
      this.carPark.leave();
      try {
        if (delay.nextInt(50) == 21) {
          // Car is delayed, check for how long
          this.display.setText("exit " + this.number + "obstructed");
          Thread.sleep(delay.nextInt(5000));
          this.display.setText("No obstructions at Exit " + this.number);
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
  }
}

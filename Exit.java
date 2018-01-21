import java.util.*;

/**
 * Exit class for letting cars leave the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Exit extends Thread {
  private CarPark carPark;
  private Clock clock;
  private int number;
  private boolean obstruction;
  private boolean start;

  /**
   * Constructor
   *
   * @param c (required) The car park to remove cars from
   * @param i (required) The exit number should be unique
   * @param cl (required) The shared clock between all the threads
   */
  public Exit(CarPark c, int i, Clock cl) {
    carPark = c;
    this.number = i;
    clock = cl;
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
    while (this.start) {
      carPark.leave();
      Random delay = new Random();
      int check = delay.nextInt(50);
      if (check == 25) {
        // Car is delayed, check for how long
        int delayTime = delay.nextInt(5000);
        try {
          this.obstruction = true;
          sleep(delayTime);
        } catch (InterruptedException e) {
        }
      }
      this.obstruction = false;
      int sleep;
      if (clock.isEveningRush()) {
        // Increase number of cars trying to leave during the Evening rush
        sleep = (delay.nextInt(150) + 1);
      } else if (clock.isMorningRush()) {
        // Less Cars will be trying to leave during the morning rush
        sleep = 1000 * (delay.nextInt(24 - clock.getHour()) + 1);
      } else {
        sleep = 100 * (delay.nextInt(24 - clock.getHour()) + 1);
      }
      try {
        sleep(sleep);
      } catch (InterruptedException e) {
      }
    }
  }
}

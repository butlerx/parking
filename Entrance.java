import java.util.*;

/**
 * Entrance class for letting cars in to the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Entrance extends Thread {
  private CarPark carPark;
  private Clock clock;
  private int number;
  private boolean start;

  /**
   * Constructor
   *
   * @param c (required) The car park to put cars in
   * @param i (required) The entrance number should be unique
   * @param cl (required) The shared clock between all the threads
   */
  public Entrance(CarPark c, int i, Clock cl) {
    carPark = c;
    clock = cl;
    this.number = i;
  }

  /** stop the process in the thread */
  public void kill() {
    this.start = false;
  }

  /**
   * Override the threads run method
   *
   * <p>Lets cars in to carpark if its not full
   *
   * <p>Tracks overflow decreasing chance of entry the larger the overflow
   *
   * <p>Increases Number of cars entering during Morning rush
   *
   * <p>Decreases Number of cars entering during evening rush
   */
  @Override
  public void run() {
    this.start = true;
    while (this.start) {
      if (carPark.getTotalCars() > carPark.getSize()) {
        // More cars than spaces
        int overflow = carPark.getTotalCars() - carPark.getSize();
        float entryChance = 1 / overflow;
        Random gate = new Random();
        if (entryChance > gate.nextFloat()) {
          // Chance of entry decreases the more overflow there is
          carPark.lookForSpace();
        }
      } else {
        carPark.lookForSpace();
      }
      Random rand = new Random();
      int sleep;
      if (clock.isMorningRush()) {
        // Increase number of cars trying to leave during the morning rush
        sleep = (rand.nextInt(150) + 1);
      } else if (clock.isEveningRush()) {
        // Less cars will try to enter during evening rush
        sleep = 1000 * (rand.nextInt(clock.getHour() + 1) + 1);
      } else {
        sleep = 100 * (rand.nextInt(clock.getHour() + 1) + 1);
      }
      try {
        sleep(Math.abs(sleep));
      } catch (InterruptedException e) {
      }
    }
  }
}

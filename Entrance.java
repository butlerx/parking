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
  private boolean start = false;

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
    Random rand = new Random();
    while (this.start) {
      int overflow = carPark.getTotalCars() - carPark.getSize();
      if (overflow > 0) {
        // More cars than spaces
        float entryChance = 1 / overflow;
        if (entryChance > rand.nextFloat()) {
          // Chance of entry decreases the more overflow there is
          this.carPark.lookForSpace(new Car(21 != rand.nextInt(50)));
        }
      } else {
        this.carPark.lookForSpace(new Car(21 != rand.nextInt(50)));
      }
      try {
        Thread.sleep(
            Math.abs(
                (clock.isMorningRush())
                    // Increase number of cars trying to enter during the morning rush
                    ? (rand.nextInt(150) + 1)
                    : (clock.isEveningRush())
                        ?
                        // Less cars will try to enter during evening rush
                        1000 * (rand.nextInt(clock.getHour() + 1) + 1)
                        : 100 * (rand.nextInt(clock.getHour() + 1) + 1)));
      } catch (InterruptedException e) {
      }
    }
  }
}

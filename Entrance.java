import java.util.*;
import javax.swing.SwingWorker;

/**
 * Entrance class for letting cars in to the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 2.0
 * @since 1.0
 */
class Entrance extends SwingWorker<Integer, String> {
  private Valet valet;
  private Clock clock;
  private int number;

  /**
   * Constructor
   *
   * @param v (required) The Valet to park the car
   * @param i (required) The entrance number should be unique
   * @param cl (required) The shared clock between all the threads
   */
  public Entrance(Valet v, int i, Clock cl) {
    this.valet = v;
    this.clock = cl;
    this.number = i;
  }

  /**
   * Override the workers doInBackground method
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
  protected Integer doInBackground() throws Exception {
    Random rand = new Random();
    while (!this.isCancelled()) {
      int overflow = valet.totalCars() - valet.carParkSize();
      if (overflow > 0) {
        // More cars than spaces
        float entryChance = 1 / overflow;
        if (entryChance > rand.nextFloat()) {
          // Chance of entry decreases the more overflow there is
          this.valet.park(new Car(21 != rand.nextInt(50)));
        }
      } else {
        this.valet.park(new Car(21 != rand.nextInt(50)));
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
    return 0;
  }
}

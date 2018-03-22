import java.util.*;

/**
 * Wait Manager class for queueing cars
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class WaitManager {
  private ArrayList<Car> waiting = new ArrayList<Car>();;

  /**
   * Add car to queue
   *
   * @param visitor (required) Car to be parked
   */
  public synchronized void addCar(Car visitor) {
    waiting.add(visitor);
  }

  /**
   * Remove Car at the front of the queue
   *
   * @return Car if there is a car in the queue
   */
  public synchronized Car removeCar() {
    return waiting.remove(0);
  }

  /**
   * Check number of cars in the queue
   *
   * @return int number of cars currently in queue
   */
  public synchronized int getNumWaiting() {
    return waiting.size();
  }
}

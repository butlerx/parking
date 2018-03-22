import java.util.*;
import java.util.concurrent.*;

/**
 * CarPark class for storing cars
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class CarPark {
  private ArrayList<Car> spaces = new ArrayList<Car>();;
  public LinkedBlockingQueue<Car> queue = new LinkedBlockingQueue<Car>();;
  private int occupied = 0;
  public final int parkSize = 1000;

  /** Constructor */
  public CarPark() {}

  /**
   * Check number of cars the carpark can have
   *
   * @return int size of the carpark
   */
  public int size() {
    return this.parkSize;
  }

  /**
   * Remove a random Car from the carpark.
   *
   * <p>Decrease a number of spaces occupied.
   */
  public void removeCar() {
    Random rand = new Random();
    int index = rand.nextInt(occupied);
    Car leaver = spaces.get(index);
    if (!leaver.isDoubleParked()) {
      spaces.remove(index);
      occupied--;
    } else {
      // Driver is occupying two spaces
      spaces.remove(index);
      // We've freed one of the spaces they occupied, we must free a second
      spaces.remove(findAsshole());
      occupied -= 2;
    }
  }

  /**
   * Used to find a second space when someone is parked over two spaces
   *
   * @return int of postion of a random double parked space
   */
  private int findAsshole() {
    for (int i = 0; i < spaces.size(); i++) {
      Car check = spaces.get(i);
      if (check.isDoubleParked()) return i;
    }
    // We've somehow managed to get to the end without finding the pair,
    // remove the first car in the car park
    return 0;
  }

  /**
   * check if carpark is empty
   *
   * @return true if car is empty
   */
  public boolean empty() {
    return this.occupied == 0;
  }

  /**
   * Add car to array of spaces
   *
   * @param visitor (required) car to be added to the spaces array
   */
  public void addCar(Car visitor) {
    if (visitor != null) {
      if (visitor.isDoubleParked()) {
        // Driver is parked across two spaces
        spaces.add(visitor);
        spaces.add(visitor);
        occupied += 2;
      } else {
        spaces.add(visitor);
        occupied++;
      }
    }
  }

  /**
   * Gets number of occupied spaces in the carpark
   *
   * @return int of number occipied spaces
   */
  public synchronized int getOccupied() {
    return this.occupied;
  }

  /**
   * Gets number of free spaces in the carpark
   *
   * @return int of number free spaces
   */
  public synchronized int getSpaces() {
    if (this.parkSize - this.occupied < 0) {
      return 0;
    } else {
      return this.parkSize - this.occupied;
    }
  }

  /**
   * Gets number of car in the carpark
   *
   * @return int of number of cars in the carpark
   */
  public synchronized int getParkedCars() {
    int doubleParked = 0;
    for (int i = 0; i < spaces.size(); i++) {
      Car check = spaces.get(i);
      if (check.isDoubleParked()) {
        doubleParked++;
      }
    }
    return this.occupied - (doubleParked / 2);
  }

  /**
   * Check if the CarPark is full
   *
   * @return boolean true if carpark is full
   */
  public synchronized boolean full() {
    return this.occupied >= this.parkSize;
  }
}

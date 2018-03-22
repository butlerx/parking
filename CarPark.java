import java.util.*;

/**
 * CarPark class for storing cars
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class CarPark {
  private ArrayList<Car> spaces = new ArrayList<Car>();
  private WaitManager queue = new WaitManager();
  private int occupied = 0;
  private int parkSize;

  /**
   * Constructor
   *
   * @param size (required) capacity of carpark
   */
  public CarPark(int size) {
    this.parkSize = size;
  }

  /**
   * Check number of cars the carpark can have
   *
   * @return int size of the carpark
   */
  public int getSize() {
    return this.parkSize;
  }

  /**
   * Gets the queue for the carpark
   *
   * @return WaitManger of the carpark
   */
  public WaitManager getQueue() {
    return this.queue;
  }

  /**
   * Remove a random Car from the carpark.
   *
   * <p>Decrease a number of spaces occupied.
   */
  private void removeCar() {
    Random rand = new Random();
    int index = rand.nextInt(occupied);
    Car leaver = spaces.get(index);
    if (leaver.getConsiderate()) {
      spaces.remove(index);
      occupied--;
    } else {
      // Driver is occupying two spaces
      spaces.remove(index);
      // We've freed one of the spaces they occupied, we must free a second
      index = findAsshole();
      spaces.remove(index);
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
      if (!check.getConsiderate()) return i;
    }
    // We've somehow managed to get to the end without finding the pair,
    // remove the first car in the car park
    return 0;
  }

  /** remove a car from the carpark if its not empty */
  public synchronized void leave() {
    while (occupied == 0) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }
    removeCar();
    notifyAll();
  }

  /**
   * Look for a free space to put a car
   *
   * <p>randomly double parks car
   */
  public synchronized void lookForSpace() {
    Random generator = new Random();
    int check = generator.nextInt(50);
    if (check == 25) {
      queue.addCar(new Car(false));
    } else {
      queue.addCar(new Car(true));
    }
  }

  /** Park a car if the carark isnt full and add to the queue if it is */
  public synchronized void park() {}

  /**
   * Add car to array of spaces
   *
   * @param visitor (required) car to be added to the spaces array
   */
  private void addCar(Car visitor) {
    if (!visitor.getConsiderate()) {
      // Driver is parked across two spaces
      spaces.add(visitor);
      spaces.add(visitor);
      occupied += 2;
    } else {
      spaces.add(visitor);
      occupied++;
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
      if (!check.getConsiderate()) {
        doubleParked++;
      }
    }
    return this.occupied - (doubleParked / 2);
  }

  /**
   * Gets total number of car in the carpark and queue
   *
   * @return int of number of cars in the carpark and queue
   */
  public synchronized int getTotalCars() {
    return getParkedCars() + queue.getNumWaiting();
  }
}

import java.util.*;

public class Parking {

  public static void main (String [] args) {
    CarPark multiStory = new CarPark(1000);
    Clock clock = new Clock(multiStory);
    clock.start();
    Entrance in1 = new Entrance(multiStory, 1);
    Entrance in2 = new Entrance(multiStory, 2);
    Entrance in3 = new Entrance(multiStory, 3);
    in1.start();
    in2.start();
    in3.start();
    Exit out1 = new Exit(multiStory, 1);
    Exit out2 = new Exit(multiStory, 2);
    Exit out3 = new Exit(multiStory, 3);
    out1.start();
    out2.start();
    out3.start();
    (new Thread(new Dashboard(multiStory))).start();
  }
}

class Car {
  private boolean considerate;
  private int time;

  public Car (boolean isConsiderate) {
    this.considerate = isConsiderate;
  }

  public boolean getConsiderate () {
    return this.considerate;
  }
}

class WaitManager {
  private ArrayList<Car> waiting;
  public WaitManager() {
    this.waiting = new ArrayList<Car>();
  }
  public synchronized void addCar(Car visitor) {
    waiting.add(visitor);
  }
  public synchronized Car removeCar() {
    return waiting.remove(0);
  }
  public synchronized int getNumWaiting() {
    return waiting.size();
  }
}

class CarPark {
  private ArrayList<Car> spaces;
  private WaitManager queue;
  private int occupied;
  private int parkSize;
  // Time is 10 minute
  private int time = 1;
  private int hour = 0;

  public CarPark (int size) {
    this.spaces = new ArrayList<Car>();
    this.queue = new WaitManager();
    this.occupied = 0;
    this.parkSize = size;
  }

  public int getTime () {
    return this.time % 6;
  }

  public int getHour () {
    return this.hour % 24;
  }

  public WaitManager getQueue() {
    return queue;
  }

  public void passTime () {
    this.time++;
    if (this.time % 6 == 0) {
      this.hour++;
    }
  }

  private void removeCar () {
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

  // Used to find a second space when someone is parked over two spaces
  private int findAsshole () {
    for(int i = 0; i < spaces.size(); i++){
      Car check = spaces.get(i);
      if(!check.getConsiderate()){
        return i;
      }
    }
    // We've somehow managed to get to the end without finding the pair,
    // remove the first car in the car park
    return 0;
  }

  public synchronized void leave () {
    while (occupied == 0) {
      try {
        wait();
      } catch (InterruptedException e) {}
    }
    removeCar();
    notifyAll();
  }

  public void lookForSpace (){
    Random generator = new Random();
    int check = generator.nextInt(50);
    if (check == 25) {
      queue.addCar(new Car(false));
    } else {
      queue.addCar(new Car(true));
    }
  }

  public synchronized void park () {
    while (occupied >= this.parkSize) {
      try {
        wait();
      } catch (InterruptedException e) {}
    }
    addCar(queue.removeCar());
    notifyAll();
  }

  private void addCar (Car visitor) {
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

  public synchronized int getOccupied () {
    return this.occupied;
  }

  public synchronized int getSpaces () {
    return this.parkSize - this.occupied;
  }

  public synchronized int getNumCars () {
    int doubleParked = 0;
    for(int i = 0; i < spaces.size(); i++){
      Car check = spaces.get(i);
      if(!check.getConsiderate()){
        doubleParked++;
      }
    }
    return this.occupied - (doubleParked / 2);
  }
}

class Entrance extends Thread {
  private CarPark carPark;
  private int number;

  public Entrance (CarPark c, int i) {
    carPark = c;
    this.number = i;
  }

  public void run () {
    while (true) {
      carPark.lookForSpace();
      carPark.park();
      Random rand = new Random();
      try {
        sleep((100 * (rand.nextInt(carPark.getHour() + 1) + 1)));
      } catch (InterruptedException e) { }
    }
  }
}
class Clock extends Thread {
  private CarPark carPark;

  public Clock (CarPark c) {
    carPark = c;
  }

  public void run () {
    while (true) {
      carPark.passTime();
      try {
        // 1000 is 1 second real time
        // 1 second real time is 10 min in simulation
        sleep((1000));
      } catch (InterruptedException e) { }
    }
  }
}

class Exit extends Thread {
  private CarPark carPark;
  private int number;

  public Exit (CarPark c, int i) {
    carPark = c;
    this.number = i;
  }

  public void run () {
    while (true) {
      carPark.leave();
      Random delay = new Random();
      int check = delay.nextInt(50);
      if (check == 25) {
        // Car is delayed, check for how long
        int delayTime = delay.nextInt(5000);
        try {
          sleep(delayTime);
        } catch (InterruptedException e) { }
      }
      try {
        sleep((100 * (delay.nextInt(24 - carPark.getHour()) + 1)));
      } catch (InterruptedException e) { }
    }
  }
}

class Dashboard extends Thread {
  private CarPark carPark;

  public Dashboard (CarPark c) {
    carPark = c;
  }

  public void run () {
    while (true) {
      System.out.print("\033[H\033[2J");
      System.out.flush();
      System.out.printf("The time is %02d:%02d%n", carPark.getHour(), carPark.getTime() * 10);
      System.out.println("There are currently " + carPark.getNumCars() + " Cars in the Carpark");
      System.out.println("There are currently " + carPark.getSpaces() + " Spaces in the Carpark");
      try {
        Thread.sleep(1000);
      } catch(InterruptedException e) {}
    }
  }
}

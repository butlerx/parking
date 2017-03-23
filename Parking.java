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

  public int getTime () {
    return this.time;
  }

  public void timePassed (int passed) {
    this.time -= passed;
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
  private int time = 1;

  public CarPark (int size) {
    this.spaces = new ArrayList<Car>();
    this.queue = new WaitManager();
    this.occupied = 0;
    this.parkSize = size;
  }

  public int getTime () {
    return this.time % 24;
  }

  public WaitManager getQueue() {
    return queue;
  }

  public void passTime () {
    this.time++;
  }

  private void removeCar () {
    int index = (int) Math.random() * occupied;
    Car leaver = spaces.get(index);
    if (leaver.getConsiderate()) {
      spaces.remove(index);
      occupied--;
    } else { //driver is occupying two spaces
      spaces.remove(index);
      index = findAsshole(); //we've freed one of the spaces they occupied, we must free a second
      spaces.remove(index);
      occupied -= 2;
    }
  }

  private int findAsshole () { //used to find a second space when someone is parked over two spaces
    for(int i = 0; i < spaces.size(); i++){
      Car check = spaces.get(i);
      if(!check.getConsiderate()){
        return i;
      }
    }
    return 0; //we've somehow managed to get to the end without finding the pair, remove the first car in the car park
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
      spaces.add(visitor);
      spaces.add(visitor); //driver is parked across two spaces
      occupied += 2;
    } else {
      spaces.add(visitor);
      occupied++;
    }
  }

  public synchronized int getOccupied() {
    return this.occupied;
  }

  public synchronized int getSpaces() {
    return this.parkSize - this.occupied;
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
      System.out.println("Entrance #" + this.number + ", cars in car park: " + carPark.getOccupied());
      try {
        sleep((int)(Math.random() * 100 * carPark.getTime()));
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
      System.out.println("The time is " + carPark.getTime());
      try {
        sleep((int)(Math.random() * 1000));
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
      if (check == 25) { //car is delayed, check for how long
        int delayTime = delay.nextInt(5000);
        try {
          System.out.println("Exit #" + this.number + " delayed by " + (((int) delayTime/1000) + 1) + " seconds");
          sleep(delayTime);
        } catch (InterruptedException e) { }
      }
      System.out.println("Exit #" + this.number + ",     cars in car park: " + carPark.getOccupied());
      try {
        sleep((int)(Math.random() * 100 * (24 - carPark.getTime())));
      } catch (InterruptedException e) { }
    }
  }
}

import java.util.*;

public class Parking {

  public static void main (String [] args) {
    CarPark multiStory = new CarPark(1000);
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

class Car{
  private boolean considerate;

  public Car(boolean isConsiderate){
    this.considerate = isConsiderate;
  }

  public boolean getConsiderate(){
    return considerate;
  }
}

class CarPark {
  private ArrayList<Car> spaces;
  private int occupied;
  private int length;

  public CarPark(int size){
    this.spaces = new ArrayList<Car>();
    this.occupied = 0;
    this.length = size;
  }

  private void removeCar(){
    int index = (int) Math.random() * occupied;
    Car leaver = spaces.get(index);
    if(leaver.getConsiderate()){
      spaces.remove(index);
      occupied--;
    } else { //driver is occupying two spaces
      spaces.remove(index);
      index = findAsshole(); //we've freed one of the spaces they occupied, we must free a second
      spaces.remove(index);
      occupied -= 2;
    }
  }

  private int findAsshole(){
    for(int i = 0; i < spaces.size(); i++){
      Car check = spaces.get(i);
      if(!check.getConsiderate()){
        return i;
      }
    }
    return 0;
  }

  public synchronized void leave() {
    while (occupied == 0) {
      try {
        wait();
      } catch (InterruptedException e) {}
    }
    removeCar();
    notifyAll();
  }

  public synchronized void park() {
    while (occupied == this.length) {
      try {
        wait();
      } catch (InterruptedException e) {}
    }
    addCar();
    notifyAll();
  }

  private void addCar(){
    int generator = (int) Math.random() * 50;
    if(generator == 50){
      spaces.add(new Car(false));
      occupied += 2;
    } else {
      spaces.add(new Car(true));
      occupied++;
    }
  }

  public synchronized int getSpaces(){
    return this.length - this.occupied;
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
      carPark.park();
      System.out.println("Entrance #" + this.number + " spaces: " + carPark.getSpaces());
      try {
        sleep((int)(Math.random() * 100));
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
      System.out.println("Exit #" + this.number + " spaces: " + carPark.getSpaces());
		}
  }
}
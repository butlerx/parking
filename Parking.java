public class Parking {

  public static void main (String [] args) {
    CarPark multiStory = new CarPark(1000);
    Entrance in = new Entrance(multiStory, 1);
    in.start();
    Exit out = new Exit(multiStory, 1);
    out.start();
  }
}

class CarPark {
  private int spaces;
  private boolean available;

  public CarPark(int spaces){
    this.spaces = spaces;
    available = true;
  }

  public synchronized int leave() {
    while (available == false) {
      try {
        wait();
      } catch (InterruptedException e) {}
    }
    available = false;
    notifyAll();
    return spaces;
  }

  public synchronized void park(int value) {
    while (available == true) {
      try {
        wait();
      } catch (InterruptedException e) {}
    }
    spaces = value;
    available = true;
    notifyAll();
  }

  public synchronized int getSpaces(){
    return spaces;
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
    for (int i = 0; i < 10; i++) {
      carPark.park(i);
      System.out.println("Entrance #" + this.number + " put: " + i);
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
    int value = 0;
    for (int i = 0; i < 10; i++) {
      value = carPark.leave();
      System.out.println("Exit #" + this.number + " got: " + value);
    }
  }
}
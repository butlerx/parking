import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Parking {

  private JFrame mainFrame;
  private JLabel headerLabel;
  private JLabel spacesLabel;
  private JLabel carsLabel;
  private JPanel controlPanel;
  private CarPark multiStory = new CarPark(1000);

  public Parking () {
    prepareGUI();
  }

  public static void main (String [] args) {
    Parking swingControlDemo = new Parking();
    swingControlDemo.showDashboard();
  }

  private void prepareGUI () {
    mainFrame = new JFrame("Carpark");
    mainFrame.setSize(400,400);
    mainFrame.setLayout(new GridLayout(4, 1));

    headerLabel = new JLabel("CarPark",JLabel.CENTER );
    carsLabel = new JLabel("",JLabel.CENTER);
    spacesLabel = new JLabel("",JLabel.CENTER);
    spacesLabel.setSize(350,100);
    carsLabel.setSize(350,100);

    mainFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent){
        System.exit(0);
      }
    });
    controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout());

    mainFrame.add(headerLabel);
    mainFrame.add(controlPanel);
    mainFrame.add(spacesLabel);
    mainFrame.add(carsLabel);
    mainFrame.setVisible(true);
  }

  private void showDashboard () {
    JButton runButton = new JButton("Run");
    JButton stopButton = new JButton("Stop");
    runButton.setActionCommand("Run");
    stopButton.setActionCommand("Stop");
    runButton.addActionListener(new ButtonClickListener());
    stopButton.addActionListener(new ButtonClickListener());
    controlPanel.add(runButton);
    controlPanel.add(stopButton);
    mainFrame.setVisible(true);
    stats();
  }

  private void stats () {
    while(true) {
      //System.out.printf("The time is %02d:%02d%n", multiStory.getHour(), multiStory.getTime() * 10);
      carsLabel.setText("There are currently " + multiStory.getNumCars() + " Cars in the Carpark");
      spacesLabel.setText("There are currently " + multiStory.getSpaces() + " Spaces in the Carpark");
    }
  }

  private class ButtonClickListener implements ActionListener {
    public void actionPerformed (ActionEvent e) {
      Clock clock = new Clock(multiStory);
      Entrance in1 = new Entrance(multiStory, 1);
      Entrance in2 = new Entrance(multiStory, 2);
      Entrance in3 = new Entrance(multiStory, 3);
      Exit out1 = new Exit(multiStory, 1);
      Exit out2 = new Exit(multiStory, 2);
      Exit out3 = new Exit(multiStory, 3);
      Parker wait1 = new Parker(multiStory.getQueue(), multiStory);
      Parker wait2 = new Parker(multiStory.getQueue(), multiStory);
      Parker wait3 = new Parker(multiStory.getQueue(), multiStory);
      String command = e.getActionCommand();
      if( command.equals( "Run" ))  {
        clock.start();
        in1.start();
        in2.start();
        in3.start();
        wait1.start();
        wait2.start();
        wait3.start();
        out1.start();
        out2.start();
        out3.start();
      } else {
        System.out.print("i tried to stop");
        clock.kill();
        in1.kill();
        in2.kill();
        in3.kill();
        out1.kill();
        out2.kill();
        out3.kill();
      }
    }
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
    System.err.println("Adding car to waitlist");
    waiting.add(visitor);
  }
  public synchronized Car removeCar() {
    System.err.println("Found a space");
    return waiting.remove(0);
  }
  public synchronized int getNumWaiting() {
    return waiting.size();
  }
}

class Parker extends Thread {
  private WaitManager queue;
  private CarPark park;
  public Parker(WaitManager queue, CarPark park) {
    this.queue = queue;
    this.park = park;
  }
  public void run() {
    while(true) {
      park.park();
    }
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
    return this.queue;
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

  public synchronized void lookForSpace (){
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
        System.err.println("Waiting for a space to become free");
        wait();
      } catch (InterruptedException e) {}
    }
    if(queue.getNumWaiting() > 0) {
      addCar(queue.removeCar());
    }
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
  private boolean start = true;

  public Entrance (CarPark c, int i) {
    carPark = c;
    this.number = i;
  }

  public void kill () {
    this.start = false;
  }

  public void run () {
    this.start = true;
    while (this.start) {
      carPark.lookForSpace();
      //carPark.park();
      Random rand = new Random();
      try {
        sleep((100 * (rand.nextInt(carPark.getHour() + 1) + 1)));
      } catch (InterruptedException e) { }
    }
  }
}
class Clock extends Thread {
  private CarPark carPark;
  private boolean start;

  public Clock (CarPark c) {
    carPark = c;
  }
  public void kill () {
    this.start = false;
  }

  public void run () {
    this.start = true;
    while (this.start) {
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
  private boolean start;

  public Exit (CarPark c, int i) {
    carPark = c;
    this.number = i;
  }

  public void kill () {
    this.start = false;
  }

  public void run () {
    this.start = true;
    while (this.start) {
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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;
import javax.swing.Timer;
import java.util.stream.IntStream;

/**
 * Car park simulator
 *
 * <P> Car park simulator with multiple queued entrances and exits
 *
 * @author      Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version     1.0
 * @since       1.0
 */
public class Parking {

  private JFrame mainFrame;
  private JLabel headerLabel;
  private JLabel spacesLabel;
  private JLabel carsLabel;
  private JLabel parkedLabel;
  private JLabel queueLabel;
  private JPanel controlPanel;
  private CarPark multiStory = new CarPark(1000);

  private Clock clock = new Clock(multiStory);
  private Entrance in1 = new Entrance(multiStory, 1);
  private Entrance in2 = new Entrance(multiStory, 2);
  private Entrance in3 = new Entrance(multiStory, 3);
  private Exit out1 = new Exit(multiStory, 1);
  private Exit out2 = new Exit(multiStory, 2);
  private Exit out3 = new Exit(multiStory, 3);
  private Parker wait1 = new Parker(multiStory.getQueue(), multiStory);
  private Parker wait2 = new Parker(multiStory.getQueue(), multiStory);
  private Parker wait3 = new Parker(multiStory.getQueue(), multiStory);

  private static DecimalFormat df = new DecimalFormat("00");

  /**
   * Constructor.
   */
  public Parking () {
    prepareGUI();
  }

  /**
   * main method
   */
  public static void main (String [] args) {
    Parking carParkOverView = new Parking();
    carParkOverView.showDashboard();
  }

  /**
   * Prepare the User dashboard of information and controls
   *
   * @see javax.swing
   **/
  private void prepareGUI () {
    mainFrame = new JFrame("Carpark");
    mainFrame.setSize(400,400);
    mainFrame.setLayout(new GridLayout(4, 1));

    headerLabel = new JLabel("CarPark",JLabel.CENTER );
    carsLabel = new JLabel("",JLabel.CENTER);
    spacesLabel = new JLabel("",JLabel.CENTER);
    queueLabel = new JLabel("",JLabel.CENTER);
    parkedLabel = new JLabel("",JLabel.CENTER);
    spacesLabel.setSize(350,100);
    carsLabel.setSize(350,100);
    queueLabel.setSize(350,100);
    parkedLabel.setSize(350,100);

    mainFrame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent){
        System.exit(0);
      }
    });
    controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout());

    mainFrame.add(headerLabel);
    mainFrame.add(spacesLabel);
    mainFrame.add(carsLabel);
    mainFrame.add(queueLabel);
    mainFrame.add(parkedLabel);
    mainFrame.add(controlPanel);
    mainFrame.setVisible(true);
  }

  /**
   * Show Dashboard to user
   */
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
    stats.start();
  }

  /**
   * Timer to refresh Dashboard every second with current information from
   * the carpark
   */
  Timer stats = new Timer(1000, new ActionListener () {
    public void actionPerformed(ActionEvent e) {
      headerLabel.setText("The time in the CarPark is " +
        df.format(multiStory.getHour()) + ":" + df.format(multiStory.getTime())
      );
      carsLabel.setText("There are currently " +
        multiStory.getTotalCars() + " Cars in the Carpark");
      spacesLabel.setText("There are currently " +
        multiStory.getSpaces() + " Spaces in the Carpark");
      parkedLabel.setText("There are currently " +
        multiStory.getParkedCars() + " Cars parked");
      queueLabel.setText("There are currently " +
        multiStory.getQueue().getNumWaiting() + " Cars searching for a space");
    }
  });

  /**
   * Listen for button click to start and stop threads
   */
  private class ButtonClickListener implements ActionListener {
    public void actionPerformed (ActionEvent e) {
      String command = e.getActionCommand();
      if(command.equals("Run"))  {
        if (clock.isAlive()) {
          clock.restart();
        } else {
          clock.start();
        }
        if (in1.isAlive()) {
          in1.restart();
        } else {
          in1.start();
        }
        if (in2.isAlive()) {
          in2.restart();
        } else {
          in2.start();
        }
        if (clock.isAlive()) {
          clock.restart();
        } else {
          in3.start();
        }
        if (wait1.isAlive()) {
          wait1.restart();
        } else {
          wait1.start();
        }
        if (wait2.isAlive()) {
          wait2.restart();
        } else {
          wait2.start();
        }
        if (wait3.isAlive()) {
          wait3.restart();
        } else {
          wait3.start();
        }
        if (out1.isAlive()) {
          out1.restart();
        } else {
          out1.start();
        }
        if (out2.isAlive()) {
          out2.restart();
        } else {
          out2.start();
        }
        if (out3.isAlive()) {
          out3.restart();
        } else {
          out3.start();
        }
      } else {
        clock.kill();
        in1.kill();
        in2.kill();
        in3.kill();
        wait1.kill();
        wait2.kill();
        wait3.kill();
        out1.kill();
        out2.kill();
        out3.kill();
      }
    }
  }
}

/**
 * Car class
 *
 * @version     1.0
 * @since       1.0
 */
class Car {
  private boolean considerate;

  /**
   * Constructor
   *
   * @param isConsiderate (required) if the driver will double park
   */
  public Car (boolean isConsiderate) {
    this.considerate = isConsiderate;
  }

  /**
   * Check if the Car is double parked
   *
   *  @return <tt>false</tt> only if the car is double parked
   */
  public boolean getConsiderate () {
    return this.considerate;
  }
}

class WaitManager {
  private ArrayList<Car> waiting;

  /**
   * Construtor
   */
  public WaitManager() {
    this.waiting = new ArrayList<Car>();
  }

  /**
   * Add car to queue
   *
   * @param visitor (required) Car to be parked
   */
  public synchronized void addCar (Car visitor) {
    waiting.add(visitor);
  }

  /**
   * Remove Car at the front of the queue
   *
   * @return Car if there is a car in the queue
   */
  public synchronized Car removeCar () {
    return waiting.remove(0);
  }

  /**
   * Check number of cars in the queue
   *
   * @return int number of cars currently in queue
   */
  public synchronized int getNumWaiting () {
    return waiting.size();
  }
}

class Parker extends Thread {
  private WaitManager queue;
  private boolean start;
  private CarPark park;

  public Parker (WaitManager queue, CarPark park) {
    this.queue = queue;
    this.park = park;
  }

  public void restart () {
    this.start = true;
  }

  public void kill () {
    this.start = false;
  }

  public void run () {
    this.start = true;
    while (true) {
      System.out.println("Cars are Parking " + this.start);
      if (this.start) {
        park.park();
      }
    }
  }
}

class CarPark {
  private ArrayList<Car> spaces;
  private WaitManager queue;
  private int occupied;
  private int parkSize;
  // Time is 10 minute
  private int time = 0;
  private int hour = 0;
  private int[] morningRush = {7, 8, 9};
  private int[] eveningRush = {17, 18, 19};

  public CarPark (int size) {
    this.spaces = new ArrayList<Car>();
    this.queue = new WaitManager();
    this.occupied = 0;
    this.parkSize = size;
  }

  public int getSize () {
    return this.parkSize;
  }

  public int getTime () {
    return (this.time % 6) * 10;
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

  public boolean isMorningRush () {
    return IntStream.of(this.morningRush).anyMatch(x -> x == this.hour);
  }

  public boolean isEveningRush () {
    return IntStream.of(this.eveningRush).anyMatch(x -> x == this.hour);
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
    for (int i = 0; i < spaces.size(); i++) {
      Car check = spaces.get(i);
      if (!check.getConsiderate()) {
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
        wait();
      } catch (InterruptedException e) {}
    }
    if (queue.getNumWaiting() > 0) {
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
    if (this.parkSize - this.occupied < 0) {
      return 0;
    } else {
      return this.parkSize - this.occupied;
    }
  }

  public synchronized int getParkedCars () {
    int doubleParked = 0;
    for(int i = 0; i < spaces.size(); i++) {
      Car check = spaces.get(i);
      if (!check.getConsiderate()) {
        doubleParked++;
      }
    }
    return this.occupied - (doubleParked / 2);
  }

  public synchronized int getTotalCars () {
    return getParkedCars() + queue.getNumWaiting();
  }
}

class Entrance extends Thread {
  private CarPark carPark;
  private int number;
  private boolean start;

  public Entrance (CarPark c, int i) {
    carPark = c;
    this.number = i;
  }

  public void restart () {
    this.start = true;
  }

  public void kill () {
    this.start = false;
  }

  public void run () {
    this.start = true;
    while (true) {
      System.out.println("Cars are entering " + this.start);
      if (this.start) {
        if (carPark.getTotalCars() > carPark.getSize()) {
          // More cars than spaces
          int overflow = carPark.getTotalCars() - carPark.getSize();
          float entryChance = 1 / overflow;
          Random gate = new Random();
          if(entryChance > gate.nextFloat()) {
            // Chance of entry decreases the more overflow there is
            carPark.lookForSpace();
          }
        } else {
          carPark.lookForSpace();
        }
        Random rand = new Random();
        int sleep;
        if (carPark.isMorningRush()) {
          // Increase number of cars trying to leave during the morning rush
          sleep = (rand.nextInt(150) + 1);
        } else if (carPark.isEveningRush()) {
          // Less cars will try to enter during evening rush
          sleep = 1000 * (rand.nextInt(carPark.getHour() + 1) + 1);
        } else {
          sleep = 100 * (rand.nextInt(carPark.getHour() + 1) + 1);
        }
        try {
          sleep(Math.abs(sleep));
        } catch (InterruptedException e) { }
      }
    }
  }
}

class Clock extends Thread {
  private CarPark carPark;
  private boolean start;

  public Clock (CarPark c) {
    carPark = c;
  }

  public void restart () {
    this.start = true;
  }

  public void kill () {
    this.start = false;
  }

  public void run () {
    this.start = true;
    while (true) {
      System.out.println("Is time passing " + this.start);
      if (this.start) {
        carPark.passTime();
        try {
          // 1000 is 1 second real time
          // 1 second real time is 10 min in simulation
          sleep((1000));
        } catch (InterruptedException e) { }
      }
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

  public void restart () {
    this.start = true;
  }

  public void kill () {
    this.start = false;
  }

  public void run () {
    this.start = true;
    while (true) {
      System.out.println("Cars are leaving " + this.start);
      if (this.start) {
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
        int sleep;
        if (carPark.isEveningRush()) {
          // Increase number of cars trying to leave during the Evening rush
          sleep = (delay.nextInt(150) + 1);
        } else if (carPark.isMorningRush()) {
          // Less Cars will be trying to leave during the morning rush
          sleep = 1000 * (delay.nextInt(24 - carPark.getHour()) + 1);
        } else {
          sleep = 100 * (delay.nextInt(24 - carPark.getHour()) + 1);
        }
        try {
          sleep(sleep);
        } catch (InterruptedException e) { }
      }
    }
  }
}

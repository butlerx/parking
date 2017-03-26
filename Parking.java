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
    JButton exitButton = new JButton("Exit");
    runButton.setActionCommand("Run");
    stopButton.setActionCommand("Stop");
    exitButton.setActionCommand("Exit");
    runButton.addActionListener(new ButtonClickListener());
    stopButton.addActionListener(new ButtonClickListener());
    exitButton.addActionListener(new ButtonClickListener());
    controlPanel.add(runButton);
    controlPanel.add(stopButton);
    controlPanel.add(exitButton);
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
      if (command.equals("Run"))  {
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
      } else if (command.equals("Stop")) {
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
      } else {
        System.exit(0);
      }
    }
  }
}

/**
 * Car class
 *
 * <P> Class to simulate car
 *
 * @author      Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
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

/**
 * Wait Manager class for queueing cars
 *
 * @author      Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version     1.0
 * @since       1.0
 */
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

/**
 * Parker class for removeing cars from queue and parking them int the car Park
 *
 * @author    Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version   1.0
 * @since     1.0
 */
class Parker extends Thread {
  private WaitManager queue;
  private boolean start;
  private CarPark park;

  /**
   * Constructor
   *
   * @param queue (required) the queue to take cars from
   * @param park (required) the Car park to park in
   */
  public Parker (WaitManager queue, CarPark park) {
    this.queue = queue;
    this.park = park;
  }

  /**
   * stop the process in the thread
   */
  public void kill () {
    this.start = false;
  }

  /**
   * Override the threads run method
   * Park car from queue
   */
  @Override
  public void run () {
    this.start = true;
    while (this.start) {
      park.park();
    }
  }
}

/**
 * Clock class for keeping track of time in the carpark
 *
 * @author      Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version   1.0
 * @since     1.0
 */
class Clock extends Thread {
  private CarPark carPark;
  private boolean start;

  /**
   * Constructor
   *
   * @param c (required) The car park to track time in
   */
  public Clock (CarPark c) {
    carPark = c;
  }

  /**
   * stop the process in the thread
   */
  public void kill () {
    this.start = false;
  }

  /**
   * Override the threads run method
   * Clock to keep track of time in car park
   * Simulating 10 minutes every 1 second
   */
  @Override
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

/**
 * Entrance class for letting cars in to the carpark
 *
 * @author    Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version   1.0
 * @since     1.0
 */
class Entrance extends Thread {
  private CarPark carPark;
  private int number;
  private boolean start;

  /**
   * Constructor
   *
   * @param c (required) The car park to put cars in
   * @param i (required) The entrance number should be unique
   */
  public Entrance (CarPark c, int i) {
    carPark = c;
    this.number = i;
  }

  /**
   * stop the process in the thread
   */
  public void kill () {
    this.start = false;
  }

  /**
   * Override the threads run method
   * Lets cars in to carpark if its not full
   * Tracks overflow decreasing chance of entry the larger the overflow
   * Increases Number of cars entering during Morning rush
   * Decreases Number of cars entering during evening rush
   */
  @Override
  public void run () {
    this.start = true;
    while (this.start) {
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

/**
 * Exit class for letting cars leave the carpark
 *
 * @author    Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version   1.0
 * @since     1.0
 */
class Exit extends Thread {
  private CarPark carPark;
  private int number;
  private boolean start;

  /**
   * Constructor
   *
   * @param c (required) The car park to remove cars from
   * @param i (required) The exit number should be unique
   */
  public Exit (CarPark c, int i) {
    carPark = c;
    this.number = i;
  }

  /**
   * stop the process in the thread
   */
  public void kill () {
    this.start = false;
  }

  /**
   * Override the threads run method
   * Lets cars leave the carpark
   * randomly delays car leaving carpark
   * Increases Number of cars leaving during evening rush
   * Decreases Number of cars leaving during Morning rush
   */
  @Override
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

/**
 * CarPark class for storing cars
 *
 * @author    Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version   1.0
 * @since     1.0
 */
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

  /**
   * Constructor
   *
   * @param size (required) capacity of carpark
   */
  public CarPark (int size) {
    this.spaces = new ArrayList<Car>();
    this.queue = new WaitManager();
    this.occupied = 0;
    this.parkSize = size;
  }

  /**
   * Check number of cars the carpark can have
   *
   * @return int size of the carpark
   */
  public int getSize () {
    return this.parkSize;
  }

  /**
   * Checks the minutes passed
   *
   * @return int of minutes passed
   */
  public int getTime () {
    return (this.time % 6) * 10;
  }

  /**
   * Checks the hours passed
   *
   * @return int of hours passed
   */
  public int getHour () {
    return this.hour % 24;
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
   * increment the time by 10 minutes
   */
  public void passTime () {
    this.time++;
    if (this.time % 6 == 0) {
      this.hour++;
    }
  }

  /**
   * Check if it is the morning Rush
   *
   * @return true if it is the morning rush
   */
  public boolean isMorningRush () {
    return IntStream.of(this.morningRush).anyMatch(x -> x == this.hour);
  }

  /**
   * Check if it is the evening Rush
   *
   * @return true if it is the evening rush
   */
  public boolean isEveningRush () {
    return IntStream.of(this.eveningRush).anyMatch(x -> x == this.hour);
  }

  /**
   * Remove a random Car from the carpark
   * Decrease a number of spaces occupied
   */
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

  /**
   * Used to find a second space when someone is parked over two spaces
   *
   * @return int of postion of a random double parked space
   */
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

  /**
   * remove a car from the carpark if its not empty
   */
  public synchronized void leave () {
    while (occupied == 0) {
      try {
        wait();
      } catch (InterruptedException e) {}
    }
    removeCar();
    notifyAll();
  }

  /**
   * Look for a free space to put a car
   * randomly double parks car
   */
  public synchronized void lookForSpace (){
    Random generator = new Random();
    int check = generator.nextInt(50);
    if (check == 25) {
      queue.addCar(new Car(false));
    } else {
      queue.addCar(new Car(true));
    }
  }

  /**
   * Park a car if the carark isnt full and add to the queue if it is
   */
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

  /**
   * Add car to array of spaces
   *
   * @param visitor (required) car to be added to the spaces array
   */
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

  /**
   * Gets number of occupied spaces in the carpark
   *
   * @return int of number occipied spaces
   */
  public synchronized int getOccupied () {
    return this.occupied;
  }

  /**
   * Gets number of free spaces in the carpark
   *
   * @return int of number free spaces
   */
  public synchronized int getSpaces () {
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

  /**
   * Gets total number of car in the carpark and queue
   *
   * @return int of number of cars in the carpark and queue
   */
  public synchronized int getTotalCars () {
    return getParkedCars() + queue.getNumWaiting();
  }
}

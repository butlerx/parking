import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * Parker class for removeing cars from queue and parking them int the car Park
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 2.0
 * @since 1.0
 */
class Valet extends SwingWorker<Integer, Void> {
  private boolean start;
  private CarPark park;
  private JLabel carsLabel;
  private JLabel spacesLabel;
  private JLabel parkedLabel;
  private JLabel queueLabel;

  /**
   * Constructor
   *
   * @param park (required) the Car park to park in
   */
  public Valet(
      CarPark park, JLabel carsLabel, JLabel spacesLabel, JLabel parkedLabel, JLabel queueLabel) {
    this.park = park;
    this.carsLabel = carsLabel;
    this.spacesLabel = spacesLabel;
    this.parkedLabel = parkedLabel;
    this.queueLabel = queueLabel;
  }

  /**
   * Add car to array of spaces
   *
   * @param visitor (required) car to be added to the spaces array
   */
  public void park(Car c) {
    try {
      this.park.queue.put(c);
    } catch (InterruptedException e) {
    }
  }

  /** remove a car from the carpark if its not empty */
  public void leave() {
    while (this.park.empty()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }
    this.park.removeCar();
  }

  /** update the interface */
  @Override
  protected Integer doInBackground() throws Exception {
    while (!this.isCancelled()) {
      this.refresh();
      while (this.park.full()) {
        this.refresh();
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
      }
      if (this.park.queue.size() > 0) {
        this.park.addCar(this.park.queue.poll());
      }
    }
    return 0;
  }

  /** fresh the ui with new stats */
  private void refresh() {
    this.carsLabel.setText("There are currently " + this.totalCars() + " Cars in the Carpark");
    this.spacesLabel.setText(
        "There are currently " + this.park.getSpaces() + " Spaces in the Carpark");
    this.parkedLabel.setText("There are currently " + this.park.getParkedCars() + " Cars parked");
    this.queueLabel.setText(
        "There are currently " + this.park.queue.size() + " Cars searching for a space");
  }

  /**
   * Check number of cars the carpark can have
   *
   * @return int size of the carpark
   */
  public synchronized int carParkSize() {
    return this.park.size();
  }

  /**
   * Gets total number of car in the carpark and queue
   *
   * @return int of number of cars in the carpark and queue
   */
  public synchronized int totalCars() {
    return this.park.getParkedCars() + this.park.queue.size();
  }
}

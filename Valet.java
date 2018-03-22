import javax.swing.JLabel;

/**
 * Valet class for removing cars from queue and parking them int the car Park
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Valet extends Thread {
  private WaitManager queue;
  private boolean start = false;
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
    this.queue = park.getQueue();
    this.park = park;
    this.carsLabel = carsLabel;
    this.spacesLabel = spacesLabel;
    this.parkedLabel = parkedLabel;
    this.queueLabel = queueLabel;
  }

  /** stop the process in the thread */
  public void kill() {
    this.start = false;
  }

  /** Override the threads run method Park car from queue */
  @Override
  public void run() {
    this.start = true;
    while (this.start) {
      park.park();
      this.carsLabel.setText(
          "There are currently " + this.park.getTotalCars() + " Cars in the Carpark");
      this.spacesLabel.setText(
          "There are currently " + this.park.getSpaces() + " Spaces in the Carpark");
      this.parkedLabel.setText("There are currently " + this.park.getParkedCars() + " Cars parked");
      this.queueLabel.setText(
          "There are currently " + this.queue.getNumWaiting() + " Cars searching for a space");
    }
  }
}

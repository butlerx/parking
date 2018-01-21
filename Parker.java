/**
 * Parker class for removeing cars from queue and parking them int the car Park
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
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
  public Parker(WaitManager queue, CarPark park) {
    this.queue = queue;
    this.park = park;
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
    }
  }
}

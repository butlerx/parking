/**
 * Valet class for removing cars from queue and parking them int the car Park
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Valet extends Thread {
  private WaitManager queue;
  private boolean start;
  private CarPark park;

  /**
   * Constructor
   *
   * @param park (required) the Car park to park in
   */
  public Valet(CarPark park) {
    this.queue = park.getQueue();
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
    while (this.start && occupied >= this.parkSize) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }
    if (queue.getNumWaiting() > 0) {
      addCar(queue.removeCar());
    }
    notifyAll();
  }
}

import java.util.stream.IntStream;

/**
 * Clock class for keeping track of time in the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Clock extends Thread {
  private CarPark carPark;
  private boolean start;
  // Time is 10 minute
  private int time = 0;
  private int hour = 0;
  private int[] morningRush = {7, 8, 9};
  private int[] eveningRush = {17, 18, 19};

  /**
   * Constructor
   *
   * @param c (required) The car park to track time in
   */
  public Clock(CarPark c) {
    carPark = c;
  }

  /**
   * Checks the minutes passed
   *
   * @return int of minutes passed
   */
  public int getTime() {
    return (this.time % 6) * 10;
  }

  /**
   * Checks the hours passed
   *
   * @return int of hours passed
   */
  public int getHour() {
    return this.hour % 24;
  }

  /** increment the time by 10 minutes */
  public void passTime() {
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
  public boolean isMorningRush() {
    return IntStream.of(this.morningRush).anyMatch(x -> x == this.hour);
  }

  /**
   * Check if it is the evening Rush
   *
   * @return true if it is the evening rush
   */
  public boolean isEveningRush() {
    return IntStream.of(this.eveningRush).anyMatch(x -> x == this.hour);
  }

  /** stop the process in the thread */
  public void kill() {
    this.start = false;
  }

  /**
   * Override the threads run method Clock to keep track of time in car park.
   *
   * <p>Simulating 10 minutes every 1 second.
   */
  @Override
  public void run() {
    this.start = true;
    while (this.start) {
      passTime();
      try {
        // 1000 is 1 second real time
        // 1 second real time is 10 min in simulation
        sleep((1000));
      } catch (InterruptedException e) {
      }
    }
  }
}

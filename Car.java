/**
 * Car class
 *
 * <p>Class to simulate car
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
class Car {
  private boolean considerate;

  /**
   * Constructor
   *
   * @param isConsiderate (required) if the driver will double park
   */
  public Car(boolean isConsiderate) {
    this.considerate = isConsiderate;
  }

  /**
   * Check if the Car is double parked
   *
   * @return <tt>false</tt> only if the car is double parked
   */
  public boolean getConsiderate() {
    return this.considerate;
  }
}

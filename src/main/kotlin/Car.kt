package parking

/**
 * # Car class
 *
 * Class to simulate car
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 3.0
 * @since 1.0
 * @param considerate (required) if the driver will double park
 */
internal class Car (private val considerate: Boolean) {

    /**
     * Check if the Car is double parked
     *
     * @return true only if the car is double parked
     */
    val doubleParked: Boolean
        get() = !this.considerate
}
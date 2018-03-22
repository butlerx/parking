package parking

import java.util.Random
import javax.swing.SwingWorker

/**
 * Entrance class for letting cars in to the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 3.0
 * @since 1.0
 * @param valet (required) The Valet to park the car
 * @param number (required) The entrance number should be unique
 * @param clock (required) The shared clock between all the threads
 */
internal class Entrance (
    private val valet: Valet,
    private val number: Int,
    private val clock: Clock
) : SwingWorker<Boolean, Void>() {

    /**
     * Lets cars in to carpark if its not full
     *
     * Tracks overflow decreasing chance of entry the larger the overflow
     *
     * Increases Number of cars entering during Morning rush
     *
     * Decreases Number of cars entering during evening rush
     */
    @Throws(Exception::class)
    protected override fun doInBackground(): Boolean {
        val rand = Random()
        while (!this.isCancelled()) {
            val overflow = valet.totalCars - valet.carParkSize
            if (overflow > 0) {
                // More cars than spaces
                val entryChance = (1 / overflow).toFloat()
                if (entryChance > rand.nextFloat()) {
                    // Chance of entry decreases the more overflow there is
                    this.valet.park(Car(21 != rand.nextInt(50)))
                }
            } else {
                this.valet.park(Car(21 != rand.nextInt(50)))
            }
            try {
                Thread.sleep(
                        Math.abs(
                                if (clock.isMorningRush)
                                    (rand.nextInt(150) + 1).toLong()
                                else if (clock.isEveningRush)
                                    // Less cars will try to enter during evening rush
                                    (1000 * (rand.nextInt(clock.hour + 1) + 1)).toLong()
                                else
                                    // Increase number of cars trying to enter during the morning rush
                                    (100 * (rand.nextInt(clock.hour + 1) + 1)).toLong()
                        ))
            } catch (e: InterruptedException) {
            }
        }
        return true
    }
}
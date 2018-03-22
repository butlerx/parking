package parking

import java.util.Random
import javax.swing.JLabel
import javax.swing.SwingWorker

/**
 * Exit class for letting cars leave the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 3.0
 * @since 1.0
 *
 * @param valet (required) The Valet to remove the car
 * @param number (required) The exit number should be unique
 * @param clock (required) The shared clock between all the threads
 * @param label (required) label to output too
 */
internal class Exit (
    private val valet: Valet,
    private val number: Int,
    private val clock: Clock,
    private val display: JLabel
) : SwingWorker<Boolean, Void>() {

    /**
     * Lets cars leave the carpark
     *
     * Randomly delays a car leaving carpark
     *
     * Increases Number of cars leaving during evening rush
     *
     * Decreases Number of cars leaving during Morning rush
     */
    @Override
    @Throws(Exception::class)
    protected override fun doInBackground(): Boolean {
        val delay = Random()
        while (!this.isCancelled()) {
            // Signal valet to remove a car
            this.valet.leave()
            try {
                if (delay.nextInt(50) == 21) {
                    // Car is delayed, check for how long
                    this.display.setText("exit " + this.number + " obstructed")
                    Thread.sleep(delay.nextInt(5000).toLong())
                    this.display.setText("No obstructions at Exit " + this.number)
                }
                Thread.sleep(
                        Math.abs(
                                if (clock.isEveningRush)
                                    (delay.nextInt(150) + 1).toLong()
                                else if (clock.isMorningRush)
                                    // Less Cars will be trying to leave during the morning rush
                                    (1000 * (delay.nextInt(24 - clock.hour) + 1)).toLong()
                                else
                                    // Increase number of cars trying to leave during the Evening rush
                                    (100 * (delay.nextInt(24 - clock.hour) + 1)).toLong()
                        ))
            } catch (e: InterruptedException) {
            }
        }
        return true
    }
}
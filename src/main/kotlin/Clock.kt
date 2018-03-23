package parking

import java.text.DecimalFormat
import java.util.stream.IntStream
import javax.swing.JLabel
import javax.swing.SwingWorker

/**
 * Clock class for keeping track of time in the carpark
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 3.0
 * @since 1.0
 * @param display (required) label to output info too
 * @param seed (required) a seed to begin the clock with and keep state
 */
internal class Clock (
    private val display: JLabel,
    private val time: Seed
) : SwingWorker<Boolean, Void>() {
    private val df = DecimalFormat("00")

    /**
     * Checks the minutes passed
     *
     * @return int of minutes passed
     */
    val minutes: Int
        get() = this.time.min % 60

    /**
     * Checks the hours passed
     *
     * @return int of hours passed
     */
    val hour: Int
        get() = this.time.hour % 24

    /**
     * Check if it is the morning Rush
     *
     * @return true if it is the morning rush
     */
    val isMorningRush: Boolean
        get() = IntStream.of(7, 8, 9).anyMatch({ x -> x == this.hour })

    /**
     * Check if it is the evening Rush
     *
     * @return true if it is the evening rush
     */
    val isEveningRush: Boolean
        get() = IntStream.of(17, 18, 19).anyMatch({ x -> x == this.hour })

    class Seed {
        var min = 0
        var hour = 0
    }

    /**
     * Override the workers doInBackground method Clock to keep track of time in car park.
     *
     * Simulating 10 minutes every 1 second.
     */
    @Throws(Exception::class)
    protected override fun doInBackground(): Boolean {
        while (!this.isCancelled()) {
            this.time.min += 1
            if (this.time.min % 60 == 0) {
                this.time.hour += 1
            }
            try {
                this.display.setText(
                        "The time in the CarPark is " +
                                df.format(this.hour) +
                                ":" +
                                df.format(this.minutes))
                // 100 is .1 second real time
                // 1 second real time is 10 min in simulation
                Thread.sleep(100)
            } catch (e: InterruptedException) {
            }
        }
        return true
    }
}
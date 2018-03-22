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
    private val seed: Seed
) : SwingWorker<Boolean, Void>() {
    private var time = 0
    private var _hour = 0
    private val df = DecimalFormat("00")

    /**
     * Checks the minutes passed
     *
     * @return int of minutes passed
     */
    val minutes: Int
        get() = this.time % 60

    /**
     * Checks the hours passed
     *
     * @return int of hours passed
     */
    val hour: Int
        get() = this._hour % 24

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
        var time = 0
    }

    init {
        this.time = seed.time % 60
        this._hour = (seed.time - this.time) / 60
    }

    /**
     * Override the workers doInBackground method Clock to keep track of time in car park.
     *
     * Simulating 10 minutes every 1 second.
     */
    @Throws(Exception::class)
    protected override fun doInBackground(): Boolean {
        while (!this.isCancelled()) {
            this.time += 1
            if (this.time % 60 == 0) {
                this._hour += 1
            }
            this.seed.time = this._hour * 60 + this.time
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
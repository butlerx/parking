package parking

import javax.swing.JLabel
import javax.swing.SwingWorker

/**
 * Valet class for removing cars from queue and parking them int the car Park
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 3.0
 * @since 1.0
 * @param park (required) the Car park to park in
 * @param carsLabel output for number of cars
 * @param spacesLabel output for number of spaces
 * @param parkedLabel output for number of parked cars
 * @param queueLabel output for number of cars in the queue
 */
internal class Valet (
    private val park: CarPark,
    private val carsLabel: JLabel,
    private val spacesLabel: JLabel,
    private val parkedLabel: JLabel,
    private val queueLabel: JLabel
) : SwingWorker<Boolean, Void>() {

    /**
     * Add car to array of spaces
     *
     * @param visitor (required) car to be added to the spaces array
     */
    fun park(c: Car) {
        try {
            this.park.queue.put(c)
        } catch (e: InterruptedException) {
        }
    }

    /** remove a car from the carpark if its not empty  */
    fun leave() {
        while (this.park.empty) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
            }
        }
        this.park.removeCar()
    }

    /** update the interface  */
    @Throws(Exception::class)
    protected override fun doInBackground(): Boolean {
        while (!this.isCancelled()) {
            this.refresh()
            while (this.park.full) {
                this.refresh()
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
            if (this.park.queue.size > 0) {
                this.park.addCar(this.park.queue.poll())
            }
        }
        return true
    }

    /** fresh the ui with new stats  */
    private fun refresh() {
        this.carsLabel.setText("There are currently " + this.totalCars + " Cars in the Carpark")
        this.spacesLabel.setText(
                "There are currently " + this.park.numSpaces + " Spaces in the Carpark")
        this.parkedLabel.setText("There are currently " + this.park.parkedCars + " Cars parked")
        this.queueLabel.setText(
                "There are currently " + this.park.queue.size + " Cars searching for a space")
    }

    /**
     * Check number of cars the carpark can have
     *
     * @return int size of the carpark
     */
    val carParkSize: Int
        @Synchronized get() = this.park.size

    /**
     * Gets total number of car in the carpark and queue
     *
     * @return int of number of cars in the carpark and queue
     */
    val totalCars: Int
        @Synchronized get() = this.park.parkedCars + this.park.queue.size
}
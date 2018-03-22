package parking

import java.util.ArrayList
import java.util.Random
import java.util.concurrent.LinkedBlockingQueue

/**
 * CarPark class for storing cars
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 3.0
 * @since 1.0
 */
internal class CarPark {
    private val spaces = ArrayList<Car>()
    val queue = LinkedBlockingQueue<Car>()
    /**
     * Gets number of occupied spaces in the carpark
     *
     * @return int of number occipied spaces
     */
    @get:Synchronized
    var occupied = 0
        private set
    val size = 1000

    /**
     * Gets number of car in the carpark
     *
     * @return int of number of cars in the carpark
     */
    val parkedCars: Int
        @Synchronized get() {
            var doubleParked = 0
            for (i in 0 until spaces.size) {
                if (spaces.get(i).doubleParked) {
                    doubleParked++
                }
            }
            return this.occupied - doubleParked / 2
        }

    /**
     * Remove a random Car from the carpark.
     *
     * Decrease a number of spaces occupied.
     */
    @Synchronized
    fun removeCar() {
        val rand = Random()
        val index = rand.nextInt(occupied)
        if (!spaces.get(index).doubleParked) {
            spaces.removeAt(index)
            occupied--
        } else {
            // Driver is occupying two spaces
            spaces.removeAt(index)
            spaces.removeAt(findAsshole())
            occupied -= 2
        }
    }

    /**
     * Used to find a second space when someone is parked over two spaces
     *
     * Will return first car if it cant find second car
     *
     * @return int of postion of a random double parked space
     */
    private fun findAsshole(): Int {
        for (i in 0 until spaces.size) {
            if (spaces.get(i).doubleParked) return i
        }
        return 0
    }

    /**
     * check if carpark is empty
     *
     * @return true if car is empty
     */
    val empty: Boolean
        @Synchronized get() = this.occupied == 0

    /** remove a car from the carpark if its not empty  */
    @Synchronized
    fun leave() {
        while (this.empty) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
            }
        }
        removeCar()
    }

    /**
     * Add car to array of spaces
     *
     * @param visitor (required) car to be added to the spaces array
     */
    @Synchronized
    fun addCar(visitor: Car?) {
        if (visitor != null) {
            if (visitor!!.doubleParked) {
                // Driver is parked across two spaces
                spaces.add(visitor)
                spaces.add(visitor)
                occupied += 2
            } else {
                spaces.add(visitor)
                occupied++
            }
        }
    }

    /**
     * Gets number of free spaces in the carpark
     *
     * @return int of number free spaces
     */
    val numSpaces: Int
        @Synchronized get() = if (this.size - this.occupied < 0) {
            0
        } else {
            this.size - this.occupied
        }

    /**
     * Check if the CarPark is full
     *
     * @return boolean true if carpark is full
     */
    val full: Boolean
        @Synchronized get() = this.occupied >= this.size
}
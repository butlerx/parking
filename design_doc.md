# University Car Park

## Design Overview

Our design contains a main CarPark class which other classes and methods
interact with. This CarPark is our state full store of data. The Entrance and
Exit classes for the CarPark are implemantions as swing workers, which are
wrappers around futures, They adds and removes cars from the CarPark
respectfully. The CarPark uses a queue to allow for an overflow and to process
incoming Cars. Using a Clock running in its own future the entrance and exit
simulate rush hour by increasing the time the number of cars depending on the
time.

## Class Design

### Car

Encapsulates car attributes. This used to hold the state of each car. This state
includes if the car is double parked for not.

### CarPark

This a State full object that tracks number of spaces, spaces occupied and which
car occupy them. It also contains a blocking linked list for cars that are
queued to be parked. If no spaces are available the queue holds the cars till a
space becomes available. All interactions with the state store are handled by
the Valet Class.

### Valet

Constantly tries to move the queue of cars into the CarPark array of spaces.
This is run inside a future. Leverages the fact each future runs on its own
thread to put the thread to sleep while no spaces are available and periodically
poll till spaces become available.

### Clock

Maintains a counter that is used when checking how long entrance and exit
futures' threads should sleep for and therefore control the ratio of how many
cars are entering vs how many are leaving, simulating rush hour. As the future
is stateless the clock future uses a state full object when it is initialised to
maintain the time across futures starts and stops.

### Entrance

Adds cars to the CarPark's queue. If the carpark exceeds the maximum capacity
then the entrance will generate a float between 0 and 1, which it will then
compare to another number is 1 divided by the number of cars in excess of
capacity, to determine its entry chance. If the number is less than this ratio,
then the entrance will still queue the car. This ratio halves with every
successive car that is added to the carpark, and so the chance of adding more
cars when over capacity should fall dramatically.

### Exit

Removes cars from the carpark. Also occasionally introduces delays to mimic the
effect of someone forgetting their ticket, these are obstructions that are
displayed in the ui. A delay has a 2% chance of occurring. When a delay does
occur the future that represents the Exit will put its thread to sleep for a
random amount of time.

## Concurrency Considerations

Entrance Future require a lock on the queue in order to fill it as its is a
linked blocking queue. This allows the future to block on calls to it without
needing to get a lock on it. Their only consideration on whether to fill the
queue or not is whether there is enough potential room in the car park to
accommodate more cars. They require a lock on the Valet object to determine
this, but immediately release it once they have the answer.

The Valet future require a lock on the CarPark in order to add and remove cars.
As the valet is the only class with access to the CarPark no other thread should
block on it. All calls to valet are async so no blocking and dont require a lock
so other threads dont have to have a lock on it

Exit threads will signal the valet when a Car is meant to be removed as this is
not blocking multiple exits can signal a once. As this runs in a future it will
not block the other thread when it sleeps.

The Clock thread manages the timer which modifies the amount that the Entrance
and Exit threads sleep for after each iteration through the loop. This governs
when there are more cars entering the car park vs exiting and vice versa. Since
the Clock has getter methods only and the values cannot be modified from outside
the thread it is safe for multiple threads to access the Clock’s values
simultaneously.

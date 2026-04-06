# Queue Management System

A multi-threaded Java application designed to simulate and analyze client queuing behaviors. The system distributes randomly generated clients to queues based on configurable strategies, providing real-time visual feedback and logging.


## Functionality

The application automates the process of managing a flow of clients entering a multi-queue system. Its primary functions include:

* **Dynamic Client Generation**: Automatically creates a specified number of clients, each with randomized arrival times and service durations based on user-defined ranges.
* **Real-Time Simulation**: Executes a time-based simulation where clients enter the system at their designated arrival times and are processed by servers in real-time.
* **Automated Dispatching**: Uses a scheduler to assign incoming clients to the most appropriate queue according to a selected logic.
* **Live Monitoring**: Displays the state of all queues and the list of waiting clients through a graphical user interface.
* **Data Logging**: Records every step of the simulation, including current time and queue statuses, into an external text file for later analysis.



## System Architecture and Logic

The system is built on a multi-layered architecture that separates the data models, the user interface, and the processing logic.

### Multi-Threading and Concurrency
The application utilizes Java's concurrency tools to simulate parallel processing. Each queue is an independent thread that processes its assigned clients one second at a time. A `BlockingQueue` is used within each server to handle client data safely across multiple threads, ensuring that client dispatching and client processing do not conflict.

### Selection Strategies
The `Scheduler` implements a Strategy Pattern, allowing the system to switch between two distinct algorithms:
* **Time Strategy**: The scheduler calculates the total waiting time for every queue and assigns the new client to the queue that will be free the soonest.
* **Shortest Queue Strategy**: The scheduler checks the number of clients currently waiting in each queue and assigns the new client to the queue with the fewest people.

### Simulation Flow
1. **Initialization**: The user inputs simulation parameters through the interface.
2. **Client Setup**: The system generates the client pool and sorts them by arrival time to ensure chronological entry into the simulation.
3. **The Simulation Loop**: A central clock increments every second. At each tick:
    * The system checks if any clients have arrived and dispatches them to a queue.
    * Each active queue thread decrements the service time of the client currently at the front.
    * When a client's service time reaches zero, they are removed from the queue.
4. **Finalization**: Once the time limit is reached or all clients are processed, the threads are stopped, and the final logs are saved.

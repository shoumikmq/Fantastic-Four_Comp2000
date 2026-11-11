
# Epidemic Spread Simulation

## Project Overview

This project is a Java-based Epidemic Spread Simulation developed for COMP2000: Object-Oriented Programming Practices. The main goal of the project is to demonstrate how an infection can spread through a moving population while applying Object-Oriented Programming concepts and advanced Java techniques.

The simulation contains a fixed population of 100 people, with 5 people initially infected. Each person moves around the simulation area with an individual movement speed. When an infected person comes into close contact with a healthy person, there is a possibility of infection.

Age affects infection probability. People over 50 have a higher infection risk, while people under 30 have a lower probability and require repeated exposure under the default infection strategy.

The simulation also includes three Recovery Tents and a Hospital. Infected individuals can recover by remaining inside these facilities for a specified duration. Natural recovery is also possible. Recovered individuals eventually return to the Healthy state with immunity, while individuals who remain infected for too long may die.

The program displays live statistics showing the number and percentage of Healthy, Infected, Recovered, and Dead individuals.

For Assessment 2, the simulation was improved by implementing the Strategy Pattern, lambda expressions, Java Streams, collectors, and parallel processing. These improvements separate infection rules from the main simulation and provide a more flexible approach to calculating population statistics.

## Color Guide

- Green = Healthy
- Red = Infected
- Blue = Recovered
- Black = Dead
- Orange = Recovery Tent
- White = Hospital

## Main Classes

- `Main` – Starts the program, selects the infection strategy, and opens the simulation window.
- `SimulationPanel` – Controls movement, infection, recovery, treatment facilities, statistics, and rendering.
- `Person` – Abstract parent class for all individuals in the simulation.
- `Healthy` – Represents healthy individuals and tracks their exposure count and immunity.
- `Infected` – Represents infected individuals and tracks infection duration and treatment entry times.
- `Recovered` – Represents individuals who have recovered from infection.
- `Dead` – Represents individuals who have died and no longer move.
- `Virus` – Stores virus information and its base infection probability.
- `Building` – Abstract parent class for treatment facilities.
- `Hospital` – Provides recovery treatment for infected individuals.
- `RecoveryTent` – Provides temporary recovery treatment.
- `InfectionStrategy` – Functional interface defining how infection decisions are made.
- `AgeBasedInfectionStrategy` – Implements the age-dependent infection rules using the Strategy Pattern.

## Simulation Features

The simulation consists of 100 individuals, with 5 beginning in the Infected state.

Each individual has a unique ID, name, age, position, and movement speed. Individuals move continuously around the simulation area and change direction when reaching its boundaries.

Healthy individuals may become infected after close contact with an infected person. The probability of infection depends on the selected infection strategy.

Infected individuals may recover naturally, receive treatment in Recovery Tents or the Hospital, or die if they remain infected for too long.

Recovered individuals eventually return to the Healthy state with immunity, preventing future infection.

The simulation also includes live population statistics, allowing users to observe changes in the number of healthy, infected, recovered, and dead individuals.

These interacting behaviours create different epidemic outcomes between simulation runs.

## Design Pattern — Strategy Pattern

The Strategy Pattern was implemented for Assessment 2 to separate infection probability calculations from the main simulation logic.

Previously, the infection rules were implemented directly inside `SimulationPanel.java`. This made the class responsible for both managing the simulation and calculating infection probabilities.

The Strategy Pattern separates these responsibilities by introducing an interface and interchangeable infection algorithms.

### Strategy Interface

The `InfectionStrategy` interface defines a common method for determining whether a healthy person becomes infected.

```java
@FunctionalInterface
public interface InfectionStrategy {
    boolean shouldInfect(Healthy person, Virus virus);
}
```

### Concrete Strategy

The `AgeBasedInfectionStrategy` class implements `InfectionStrategy` and calculates infection probability using the individual's age and exposure history.

Individuals under 30 require at least two close-contact exposures and have a reduced infection probability. Individuals over 50 have an increased infection probability.

### Alternative Strategy Using a Lambda

The simulation also supports a fixed-chance infection strategy implemented using a lambda expression.

```java
strategy = (person, virus) ->
        Math.random() * 100 < virus.getSpreadPercentage();
```

This strategy uses the virus's base infection probability without applying age-dependent adjustments or repeated-exposure requirements.

### Strategy Selection

The `Main` class selects the appropriate infection strategy.

By default, the program uses `AgeBasedInfectionStrategy`. When the command-line argument `fixed` is provided, the program uses the fixed-chance lambda strategy instead.

The selected strategy is passed to `SimulationPanel` through constructor injection.

```java
SimulationPanel panel = new SimulationPanel(strategy);
```

During infection spreading, `SimulationPanel` delegates the infection decision to the selected strategy:

```java
if (distance(infected, healthy) <= CONTACT_DISTANCE) {
    if (infectionStrategy.shouldInfect(healthy, virus)) {
        infect(i, healthy);
    }
}
```

This implementation improves maintainability and flexibility because alternative infection algorithms can be introduced without modifying the main infection-spreading process.

## Infection Rules

The simulation uses a virus called Zombie Virus with a base spread percentage of 45%.

Two individuals must be within the defined close-contact distance of 18 pixels before infection can be attempted.

### Age-Based Infection Strategy

This is the default infection strategy.

- **Under 30:** Requires at least two exposures and receives a 20-percentage-point reduction in infection probability.
- **Ages 30–50:** Uses the normal infection probability.
- **Over 50:** Receives a 25-percentage-point increase in infection probability.

The calculated probability is limited to a minimum of 5% and a maximum of 90%.

### Fixed-Chance Infection Strategy

The fixed-chance strategy uses the virus's base spread percentage of 45% for each eligible close-contact infection attempt.

Unlike the age-based strategy, this approach does not adjust infection probability according to age or previous exposure.

### Immunity

Individuals who have previously recovered become immune and cannot become infected again.

When a healthy individual becomes infected, their ID, name, age, position, and movement speed are preserved. Only their health state changes.

## Recovery System

### Natural Recovery

Natural recovery becomes possible after an individual has been infected for at least 15 seconds.

Recovery probability depends on age:

- Under 30: 18% chance per health check.
- Ages 30–50: 10% chance per health check.
- Over 50: 6% chance per health check.

Health checks occur approximately once per second.

### Recovery Tents

Three Recovery Tents appear at random locations in the simulation.

The tents remain active for 5 seconds, disappear for 5 seconds, and then reappear at new random locations.

An infected individual must remain inside a Recovery Tent for 2 continuous seconds to recover.

If the individual leaves before completing treatment, the treatment timer resets.

### Hospital

The Hospital appears at a random location.

It remains active for 3 seconds and reappears on a 15-second cycle.

An infected individual must remain inside the Hospital for 1 continuous second to recover.

If the individual leaves before completing treatment, the treatment timer resets.

### Recovered and Immune Individuals

When an infected individual recovers, their state changes to Recovered and they are displayed in blue.

The simulation records the time of recovery.

After 10 seconds, the individual returns to the Healthy state with immunity, preventing reinfection.

### Death System

If an infected individual remains infected for 50 seconds without recovering, they transition to the Dead state.

Their ID, name, age, and final position are preserved.

Their movement speed becomes zero, preventing further movement.

Dead individuals remain visible in black.

## Object-Oriented Programming Concepts

The simulation demonstrates several Object-Oriented Programming concepts:

- **Inheritance:** `Healthy`, `Infected`, `Recovered`, and `Dead` inherit from the abstract `Person` class.
- **Abstraction:** The `Person` and `Building` classes define shared structures for related subclasses.
- **Encapsulation:** Classes use private fields and public getter methods to control access to their data.
- **Polymorphism:** Different `Person` subclasses override `getStatus()` to return their respective health states.
- **Generics:** `ArrayList<Person>` and `ArrayList<RecoveryTent>` manage collections of related objects.
- **Exception Handling:** `InterruptedException` is handled within the simulation thread.
- **Strategy Pattern:** Infection algorithms are separated using the `InfectionStrategy` interface.
- **Functional Programming:** Lambda expressions, method references, and the Streams API are used to simplify behaviour selection and collection processing.

## Java Streams, Lambdas and Parallelism

For Assessment 2, Java Streams and parallel processing were introduced to improve how population statistics are calculated.

### Lambda Expressions

The fixed-chance infection strategy is implemented using a lambda expression in `Main.java`.

This demonstrates behaviour parameterisation by allowing the program to select different infection algorithms through the `InfectionStrategy` functional interface.

### Streams and Collectors

The `drawStats()` method in `SimulationPanel.java` uses Java Streams to count the number of individuals in each health state.

```java
Map<String, Long> counts = snapshot.parallelStream()
        .collect(Collectors.groupingBy(
                Person::getStatus,
                Collectors.counting()
        ));
```

The main operations are:

- `parallelStream()` – Processes the population snapshot using parallel stream operations.
- `Person::getStatus` – Retrieves each individual's health status through a method reference.
- `Collectors.groupingBy()` – Groups individuals according to their health state.
- `Collectors.counting()` – Counts the individuals in each group.

This replaces the previous implementation, which used a traditional `for` loop and `switch` statement with separate counters.

### Parallel Processing

Parallel streams allow the statistics calculation to be divided into smaller tasks that may execute across multiple threads.

The implementation uses a separate population snapshot to reduce the risk of concurrent structural modifications while the simulation updates the original collection.

The `volatile` keyword is used to publish the latest snapshot to the graphical interface and to ensure that changes to the simulation's running state are visible across threads.

During rendering, the program captures one snapshot and passes it to the drawing and statistics methods, improving consistency within each frame.

Because the snapshot contains references to mutable `Person` objects, it does not guarantee complete thread safety. However, it reduces some concurrency risks associated with reading a collection while its structure changes.

For a population of 100 individuals, parallel processing may introduce more overhead than a sequential stream. The main purpose of this implementation is to demonstrate the use of parallel Streams and collectors.

## Emergent Behaviour

The simulation demonstrates emergent behaviour through interactions between individual movement, infection probability, recovery mechanisms, and treatment availability.

Each individual moves independently, and infection can occur when healthy and infected individuals come into close contact.

The age-based strategy influences infection outcomes, while temporary treatment facilities create additional uncertainty.

For example, an infected individual may recover by entering a Recovery Tent, but the tent could disappear before treatment is completed.

Individuals can also recover naturally, become immune, or die after prolonged infection.

These interactions create different infection patterns between runs without directly controlling the overall epidemic outcome.

## Future Improvements

Several additional features could improve the simulation:

- **Quarantine System:** Isolate infected individuals to reduce infection transmission.
- **Limited Hospital Capacity:** Introduce treatment limits to simulate overcrowding and resource shortages.
- **Interactive Controls:** Allow users to change infection probabilities, population size, and movement speed during the simulation.
- **Additional Infection Strategies:** Introduce different virus variants or alternative infection models.
- **Automated Testing:** Add JUnit tests to verify health transitions, infection rules, and recovery conditions.
- **Improved Concurrency:** Introduce immutable rendering snapshots and investigate more efficient parallel processing.
- **Improved Architecture:** Separate infection handling, treatment management, statistics, and graphical rendering into smaller classes.

## How to Run

### Requirements

- Java JDK 17 or later
- A desktop environment supporting Java AWT

### 1. Compile the Project

Open a terminal in the folder containing the Java source files.

```bash
javac *.java
```

### 2. Run the Default Age-Based Strategy

```bash
java Main
```

This launches the simulation using the age-dependent infection strategy.

### 3. Run the Fixed-Chance Strategy

```bash
java Main fixed
```

This launches the simulation using the fixed-chance lambda strategy.

### 4. Close the Simulation

Close the simulation window to stop the simulation thread and exit the application.

## Project Summary

The Epidemic Spread Simulation demonstrates how object-oriented programming can be used to model interactions within a population.

It combines inheritance, abstraction, encapsulation, polymorphism, and probabilistic behaviour to simulate infection spreading, recovery, immunity, and death.

For Assessment 2, the project was extended through the Strategy Pattern, lambda expressions, Java Streams, and parallel processing.

These improvements demonstrate how existing software can be refactored to provide greater flexibility and maintainability while preserving its core simulation behaviour.

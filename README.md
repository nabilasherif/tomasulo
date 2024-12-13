# Tomasulo Algorithm Simulator

## Overview

This project is a simulator that implements the **Tomasulo Algorithm**, which is a dynamic scheduling algorithm used for instruction execution in modern processors. The simulator mimics the out-of-order execution of instructions, manages reservation stations, and handles data hazards in a pipeline, improving the overall throughput of instruction execution.

The simulator processes various instruction types (such as arithmetic, load/store, and branch operations), manages reservation stations, and utilizes a register file with tags to track dependencies between instructions. It uses the concepts of **Reservation Stations (RS)** and **Common Data Bus (CDB)** to simulate the execution of instructions in parallel while resolving data hazards and control hazards.

## Key Features

- **Dynamic Instruction Scheduling**: The simulator schedules and issues instructions based on available reservation stations and handles out-of-order execution.
- **Data Hazard Handling**: It manages data dependencies by tagging registers and using reservation stations.
- **Support for Multiple Instruction Types**: The simulator supports a variety of instruction types such as arithmetic operations, loads, stores, and branches.
- **Pipeline Stalls**: It simulates pipeline stalls, especially when branch instructions are encountered or when reservation stations are full.
- **Cycle Management**: The simulator progresses through cycles, issuing and executing instructions while managing the state of the pipeline.

## Instructions Supported

The simulator supports the following instruction types:

- **Arithmetic Instructions**: 
  - DADDI, DSUBI, ADD_D, ADD_S, SUB_D, SUB_S
- **Multiplication and Division**: 
  - MUL_D, MUL_S, DIV_D, DIV_S
- **Load and Store Instructions**: 
  - LW, LD, L_S, L_D, SW, SD, S_S, S_D
- **Branch Instructions**: 
  - BNE, BEQ

## How It Works

The program operates by maintaining several key components:

- **Instruction Queue**: A list of instructions to be processed.
- **Reservation Stations**: Stations for holding instructions waiting for operands (AddSubRS, MulDivRS, LoadRS, StoreRS, BranchRS).
- **Register File**: A file that stores the values of registers and their dependency status.

### Execution Process:
1. **Increment Cycle**: The `incrementCycle()` method manages the flow of execution by checking available reservation stations and issuing instructions for execution. It also handles the tagging of registers and checks for dependencies.
2. **Handling Branches**: If a branch instruction is encountered, the simulator checks whether a decision has been made and stalls the pipeline until the branch is resolved.
3. **Register Updates**: After executing an instruction, the register file is updated with the appropriate tags to indicate when the register will be updated.
4. **Loop Until Completion**: The program continues executing instructions in cycles until all instructions are completed.

## Prerequisites

Before running the project, make sure you have the following installed on your system:

- **Java 8 or higher**: The project is built using Java.
- **Maven**: A project management tool used for building and managing dependencies.
- **JavaFX**: A graphical user interface (GUI) framework used for building the front end of the simulator.

## Running the Project

### 1. Clone the repository

Clone the repository to your local machine using Git:

```bash
git clone https://github.com/your-username/tomasulo-algorithm-simulator.git
```

### 2. Navigate to the project directory

Once you've cloned the repository, navigate to the project directory:

```bash
cd tomasulo-algorithm-simulator
```


### 3. Build the project with Maven

This project uses Maven for building and dependency management. To build the project, run the following Maven command in the terminal:

```bash
mvn clean install
```

### 4. Run the simulator

Once the project is built, you can run the simulator using Maven:

```bash
mvn exec:java
```

### 5. JavaFX Setup (if needed)

The program uses JavaFX for its user interface. If you're running the project on a machine where JavaFX is not bundled with the JDK, you might need to download JavaFX separately and configure it as a dependency.

To do this, follow these steps:

1. Download JavaFX from [here](https://openjfx.io/).
2. Extract the JavaFX SDK and configure it in your IDE or Maven as an additional dependency.
3. For Maven users, you can add the JavaFX dependency in your `pom.xml` file under `<dependencies>`:

```xml
<dependency>
  <groupId>org.openjfx</groupId>
  <artifactId>javafx-controls</artifactId>
  <version>17</version>
</dependency>
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


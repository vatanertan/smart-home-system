# Smart Home System

An object-oriented, event-driven Smart Home Automation System simulator written in Java. It processes chronological command logs to manage smart devices, schedule automated state transitions, calculate power and storage consumption, and generate sorted status reports (`ZReport`).

## Features

- **Event-Driven Time Simulation:** Advances system time (`yyyy-MM-dd_HH:mm:ss`) via explicit timestamps (`SetTime`), relative intervals (`SkipMinutes`), or jumping directly to the next scheduled device action (`Nop`).
- **Polymorphic Device Hierarchy:** Models diverse smart home hardware extending the abstract `SmartDevice` base class:
  - **Smart Plug:** Tracks cumulative energy consumption in Watt-hours (`220V * Ampere * Hours`) based on active plugged-in duration.
  - **Smart Camera:** Tracks cumulative storage usage in megabytes (`MB/min * Minutes`) while switched on.
  - **Smart Lamp:** Supports adjustable color temperature (`2000K–6500K`) and brightness (`0%–100%`).
  - **Smart Color Lamp:** Extends `SmartLamp` to support both white ambiance mode and hexadecimal RGB color mode (`0x000000–0xFFFFFF`).
- **Deterministic Scheduling & Reporting:** Implements `Comparable<SmartDevice>` to execute scheduled switch events in chronological order and output sorted system snapshots (`ZReport`).
- **Domain-Specific Exception Handling:** Enforces command syntax, chronological integrity, device type checks, and parameter bounds through a dedicated `SmartHomeException` hierarchy.

## Supported Commands

- **System & Time:** `SetInitialTime`, `SetTime`, `SkipMinutes`, `Nop`, `ZReport`
- **Device Lifecycle:** `Add`, `Remove`, `ChangeName`, `Switch`, `SetSwitchTime`
- **Device Controls:** `PlugIn`, `PlugOut`, `SetKelvin`, `SetBrightness`, `SetWhite`, `SetColorCode`, `SetColor`

## Project Structure

```text
smart-home-system/
├── src/
│   ├── SmartHomeSystem.java
│   ├── SmartSystemManager.java
│   ├── TimeController.java
│   ├── SmartDevice.java
│   ├── SmartPlug.java
│   ├── SmartCamera.java
│   ├── SmartLamp.java
│   ├── SmartColorLamp.java
│   └── ErroneousCommandException.java
├── tests/
│   ├── sample_inputs/
│   │   └── input1.txt ... input7.txt
│   └── expected_outputs/
│       └── output1.txt ... output7.txt
├── .gitignore
└── README.md
```

## Prerequisites

- Java Development Kit (JDK) 8 or higher

## Compilation & Usage

1. Compile all source files into an output directory:
   ```bash
   javac -d out src/*.java
   ```

2. Run the simulation with an input and output file path:
   ```bash
   java -cp out SmartHomeSystem tests/sample_inputs/input1.txt actual_output.txt
   ```

3. Verify the output against the expected result:
   ```bash
   diff -u tests/expected_outputs/output1.txt actual_output.txt
   ```
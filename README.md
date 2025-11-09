# Welcome to COMP2000 - Object Oriented Programming Practices
## Session 2, 2025

Please ensure that you follow the weekly updates in this repository

You are free to clone this repository into your own hosted git environment, such as Github, Bitbucket, or Gitlab.

*However*, please be aware that any repository containing your assignment code **must** be made private. Any repository with assignment code that is public available, or found to be shared with other students, will be considered a violation of the academic integrity policy.

ASSIGNMENT 2
Grid + Live Weather Integration (Week 11 extension)
Student: Imrul Ahsan Fahim (ID: 48389536)

Overview
This Java program is a grid-based simulation that visualises moving actors (Cat, Dog, Bird) affected by live weather data streamed from a remote server.
The grid dynamically changes colour as rain and temperature events occur, while bots move automatically using strategy-based movement patterns.
It demonstrates real-time rendering, event-driven updates, and the use of design patterns, streams, and lambdas for clean and extensible object-oriented design.

Implementation details

Grid.java

Represents the 20 × 20 board made of Cell objects positioned on the screen.
Provides lookup methods (cellAtPoint, getRadius, etc.) and draws all cells.
Implements weather logic—applying rain and temperature to update cell states.
Uses Streams, Optionals, and lambdas to process and evaporate cells efficiently.

Cell.java

Models a single square on the grid with position, waterLevel, and hot state.
Draws itself with colour based on flooding (blue) and temperature (orange).
Includes methods like addWater, evaporate, and setHot for weather changes.
Provides contains(Point) for hover detection and highlighting interaction.

Main.java

Creates the main game window (JFrame) and renders at ~50 FPS.
Instantiates Stage (via StageReader.readStage("data/stage1.rvb") with a fallback to new Stage()).
Starts the live weather stream with:  Client.startWeatherStream(stage::applyWeather);
This method reference connects the network client directly to the game logic (lambda/behaviour parametrisation).

Client.java

Implemented a real-time HTTP client using HttpClient to connect to http://13.238.167.130/weather.
Converts each incoming line into a WeatherEvent object and delivers it to the Stage through a callback function.
Used lambdas and method references for clean, event-driven design (stage::applyWeather).

WeatherUpdate.java

Defines logic for interpreting and applying incoming weather data to the grid.
Handles attributes like rain, temp, and others, updating cell colour and state.
Uses method references and lambdas to trigger the correct grid update dynamically.
Acts as a bridge between parsed WeatherEvents and visual changes on the screen.

Stage.java

Acts as the main controller that manages the grid, actors, and user interactions.
Receives WeatherEvents from Client and applies them through its WeatherSystem.
Maintains game states (ChoosingActor, SelectingNewLocation) and bot timing (Beat).
Handles all painting operations, overlays, and weather updates each frame.


Other class summeries(Haven't made any changes)

Actor.java
Base class for all characters (Cat, Dog, Bird) that stores their current Cell location and movement behaviour.
Delegates movement logic to a MoveStrategy and handles drawing itself on the grid.

Cat.java, Dog.java, Bird.java
Subclasses of Actor, each representing a different character type.
They differ mainly in colour and whether they are controlled by the player or move automatically (bots).

MoveStrategy.java, MoveLeft.java, MoveRandomly.java
Implements the Strategy Pattern for actor movement.
Defines how a bot decides its next cell—either moving left or picking a random valid direction.

GameState.java, ChoosingActor.java, SelectingNewLocation.java
Implements the State Pattern for handling user interactions.
Determines whether the player is selecting an actor or choosing a destination to move to.

Beat.java, AnimationBeat.java, BotMoving.java
Implements a simple timing system for animations and automated bot actions.
Acts like an Observer Pattern, where bots move every time a “beat” or tick occurs.

StageReader.java
Reads the data/stage1.rvb configuration file and creates a Stage with actors positioned on specific grid cells.
Handles file input safely and returns a ready-to-use Stage object for the simulation.





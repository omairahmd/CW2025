# TetrisJFX - JavaFX Tetris Game

A modern, feature-rich Tetris game implementation built with JavaFX, featuring multiple game modes, audio system, and a jungle-themed UI.

## GitHub

**Repository Link**: https://github.com/omairahmd/CW2025.git

## Compilation Instructions

### Prerequisites
- **Java Development Kit (JDK)**: Version 23 or higher
- **Apache Maven**: Version 3.6.0 or higher
- **Operating System**: Windows, macOS, or Linux (JavaFX compatible)

### Step-by-Step Compilation Guide

1. **Clone the repository**:
   ```bash
   git clone https://github.com/omairahmd/CW2025.git
   cd CW2025-master
   ```

2. **Verify Java and Maven installation**:
   ```bash
   java -version    # Should show JDK 23 or higher
   mvn --version   # Should show Maven 3.6.0 or higher
   ```

3. **Compile the project**:
   ```bash
   mvn clean compile
   ```
   This will:
   - Download all required dependencies (JavaFX, JUnit)
   - Compile all Java source files in `src/main/java`
   - Place compiled classes in `target/classes`

4. **Run the application**:
   ```bash
   mvn javafx:run
   ```
   Or alternatively:
   ```bash
   mvn clean javafx:run
   ```

5. **Run tests** (optional):
   ```bash
   mvn test
   ```

### Dependencies
All dependencies are automatically managed by Maven via `pom.xml`:
- **JavaFX Controls**: 21.0.6
- **JavaFX FXML**: 21.0.6
- **JavaFX Media**: 21.0.6 (with Windows classifier)
- **JUnit Jupiter**: 5.12.1 (for testing)

### Special Settings
- **Java Source/Target**: 23
- **Encoding**: UTF-8
- **Main Class**: `com.comp2042.Main`

## Implemented and Working Properly

### Core Gameplay Features
- **Classic Tetris Mechanics**: Full implementation of traditional Tetris gameplay with all 7 tetromino types (I, J, L, O, S, T, Z)
- **Brick Movement**: Left, right, down movement with collision detection
- **Brick Rotation**: Counterclockwise rotation with wall-kick and collision detection
- **Line Clearing**: Complete rows are automatically cleared when filled, with score calculation
- **Score System**: Points awarded for line clears (50 points × lines²), manual moves (1 point), and hard drops (2 points per row)
- **Level System**: Game speed increases every 10 cleared lines (level progression)
- **High Score Tracking**: Persistent high score storage in user's home directory (`.tetris_highscore.txt`)

### Game Modes
- **Classic Mode**: Traditional Tetris gameplay with increasing difficulty
- **Jungle Overgrowth**: Survival mode where vine lines are added every 10 seconds, board shifts up automatically
- **Treasure Hunt**: Objective-based mode with pre-filled dirt/gold field, victory condition when all gold is cleared

### Visual Features
- **Main Menu**: Video background with Start, Settings, and Quit buttons
- **Jungle Theme**: Custom jungle-themed UI with matching color scheme and fonts
- **Ghost Piece**: Semi-transparent preview showing where the current piece will land
- **Next Bricks Panel**: Displays the next 3 upcoming pieces with jungle-themed styling
- **Grid Overlay**: Visual grid layout within game boundaries
- **Game Background**: Custom jungle-themed background image
- **Score Panel**: Left-side panel displaying current score and high score
- **Controls Panel**: In-game display of all keyboard controls
- **Dynamic UI**: Resizable window with automatic centering of game board and panels

### User Interface
- **Pause System**: ESC key pauses/resumes game with dedicated pause screen
- **Settings Panel**: Adjustable music and SFX volume controls with sliders
- **Instructions Panel**: Mode-specific gameplay instructions displayed before game starts
- **Game Over Panel**: Centered panel with "NEW GAME" and "MAIN MENU" buttons
- **Victory Screen**: Special "YOU WIN!" message for Treasure Hunt mode completion

### Audio System
- **Background Music**: Looping forest-themed background music (`ForestBackgroundMusic.wav`)
- **Sound Effects**: 
  - Move sound (`MovingBlockSFx.wav`)
  - Landing sound (`BlockLandingSFx.wav`)
  - Line clear sound (`LineCompletedSFx.wav`)
  - Game over sound (`GameOverSFx.wav`)
- **Volume Control**: Separate volume controls for music and SFX with real-time updates

### Technical Features
- **Factory Pattern**: `BrickFactory` class for centralized brick creation
- **Singleton Pattern**: `SoundManager` for global audio management
- **Event System**: Observer pattern for input event handling
- **MVC Architecture**: Separation of Model, View, and Controller
- **JUnit Tests**: Comprehensive test coverage for utility classes and core functionality

## Implemented but Not Working Properly

### Video Background
- **Issue**: Main menu video background (`Video_Generation_With_Specific_Requirements.mp4`) may not load on some systems
- **Symptoms**: Video fails to play, falls back to static background image
- **Root Cause**: Codec compatibility issues with JavaFX MediaPlayer on certain platforms
- **Workaround**: Application gracefully falls back to static background image when video fails to load
- **Status**: Functional fallback implemented, video works on most systems but not guaranteed on all platforms

## Features Not Implemented

None. All planned features have been successfully implemented and are working as expected.

## New Java Classes

### Factory Pattern Implementation
- **`BrickFactory.java`** (`src/main/java/com/comp2042/logic/bricks/BrickFactory.java`)
  - **Purpose**: Implements Factory Design Pattern for creating brick instances
  - **Description**: Centralizes brick creation logic, maps integer types (0-6) to specific brick implementations (I, J, L, O, S, T, Z). Eliminates magic numbers and provides type-safe brick creation.

### Audio System
- **`SoundManager.java`** (`src/main/java/com/comp2042/manager/SoundManager.java`)
  - **Purpose**: Singleton class managing all game audio (music and sound effects)
  - **Description**: Provides centralized control over background music and SFX playback. Implements volume control properties that can be bound to UI controls. Handles missing audio files gracefully.

### High Score System
- **`HighScoreManager.java`** (`src/main/java/com/comp2042/util/HighScoreManager.java`)
  - **Purpose**: Manages persistent high score storage
  - **Description**: Saves high scores to a file in the user's home directory (`.tetris_highscore.txt`). Provides methods to update, retrieve, and reset high scores. Includes error handling for file I/O operations.

### Game Modes
- **`GameMode.java`** (`src/main/java/com/comp2042/model/GameMode.java`)
  - **Purpose**: Enum defining available game modes
  - **Description**: Contains three game modes: CLASSIC (traditional Tetris), OVERGROWTH (survival mode), and TREASURE_HUNT (objective-based mode).

### User Interface Panels
- **`MainMenuController.java`** (`src/main/java/com/comp2042/view/MainMenuController.java`)
  - **Purpose**: Controller for the main menu screen
  - **Description**: Handles main menu navigation, video background loading, and transitions to game or settings screens.

- **`PausePanel.java`** (`src/main/java/com/comp2042/view/PausePanel.java`)
  - **Purpose**: Pause screen overlay panel
  - **Description**: Displays pause menu with "RESUME", "SETTINGS", and "MAIN MENU" buttons. Uses jungle theme styling consistent with the rest of the UI.

- **`SettingsPanel.java`** (`src/main/java/com/comp2042/view/SettingsPanel.java`)
  - **Purpose**: Settings screen for audio configuration
  - **Description**: Provides sliders for adjusting music and SFX volume. Binds slider values to `SoundManager` volume properties for real-time updates.

- **`InstructionsPanel.java`** (`src/main/java/com/comp2042/view/InstructionsPanel.java`)
  - **Purpose**: Displays mode-specific gameplay instructions
  - **Description**: Dynamically generates instruction text based on selected game mode. Shows before game starts to inform players of mode-specific rules.

- **`ModeSelectionPanel.java`** (`src/main/java/com/comp2042/view/ModeSelectionPanel.java`)
  - **Purpose**: Game mode selection screen
  - **Description**: Displays buttons for selecting between CLASSIC, JUNGLE OVERGROWTH, and TREASURE HUNT modes. Appears after clicking "Start" in main menu.

- **`GameOverPanel.java`** (`src/main/java/com/comp2042/view/GameOverPanel.java`)
  - **Purpose**: Game over screen overlay
  - **Description**: Displays game over message with "NEW GAME" and "MAIN MENU" buttons. Can also display "YOU WIN!" message for victory conditions (Treasure Hunt mode).

## Modified Java Classes

### Core Application
- **`Main.java`** (`src/main/java/com/comp2042/Main.java`)
  - **Changes**: 
    - Extracted magic numbers to named constants (`MENU_WIDTH`, `MENU_HEIGHT`, `APP_TITLE`)
    - Modified to load main menu instead of directly starting game
    - Added `SoundManager` initialization for background music
    - Enabled window resizing
  - **Reason**: Improved code maintainability and added main menu functionality

### Game Controller
- **`GameController.java`** (`src/main/java/com/comp2042/controller/GameController.java`)
  - **Changes**:
    - Added `GameMode` parameter to constructor
    - Implemented overgrowth timer for OVERGROWTH mode (adds vine lines every 10 seconds)
    - Added victory condition check for TREASURE_HUNT mode
    - Integrated `SoundManager` for sound effects (line clear, game over)
    - Added pause/resume functionality for overgrowth timer
  - **Reason**: Support for multiple game modes and enhanced gameplay features

- **`GameInputHandler.java`** (`src/main/java/com/comp2042/controller/GameInputHandler.java`)
  - **Changes**:
    - Added hard drop functionality (SPACE key)
    - Added pause functionality (ESC key)
    - Added restart functionality (N key)
    - Fixed null `eventListener` issue by making it non-final and adding setter method
  - **Reason**: Enhanced controls and bug fixes for input handling

### Game Model
- **`SimpleBoard.java`** (`src/main/java/com/comp2042/model/SimpleBoard.java`)
  - **Changes**:
    - Added `GameMode` field and getter/setter methods
    - Implemented `initTreasureField()` for TREASURE_HUNT mode (pre-fills bottom 8 rows with dirt/gold)
    - Implemented `addVineLine()` for OVERGROWTH mode (shifts board up, adds vine line at bottom)
    - Added `hasRemainingTreasure()` method for victory condition checking
    - Added level tracking (`levelProperty()`, `linesClearedTotal`)
    - Integrated `BrickFactory` instead of direct brick instantiation
    - Added `SoundManager` integration for move/rotate/land sounds
    - Fixed board matrix dimensions (changed from `[width][height]` to `[height][width]` for row-major storage)
  - **Reason**: Support for multiple game modes, level system, and bug fixes

### View Controller
- **`GuiController.java`** (`src/main/java/com/comp2042/view/GuiController.java`)
  - **Changes**:
    - Extensive refactoring of `initialize()` method into smaller helper methods
    - Added ghost piece rendering (semi-transparent preview)
    - Added next bricks panel (displays next 3 pieces)
    - Added score panel (left-side display of score and high score)
    - Added controls panel (in-game control reference)
    - Added grid overlay (visual grid lines)
    - Implemented dynamic centering for game board and panels
    - Added resize listener for window resizing
    - Added level binding and speed adjustment based on level
    - Added `HighScoreManager` integration
    - Added pause panel integration
    - Added victory screen functionality
    - Fixed initial board rendering for TREASURE_HUNT mode (now shows pre-filled rows from start)
    - Added color mapping for new game mode elements (dirt, gold, vines)
  - **Reason**: Enhanced UI features, improved code organization, and support for new game modes

### Utility Classes
- **`MatrixOperations.java`** (`src/main/java/com/comp2042/util/MatrixOperations.java`)
  - **Changes**:
    - Renamed `checkOutOfBound` to `isOutOfBounds` for clarity
    - Fixed `isOutOfBounds` method to check `targetY >= 0` (prevented ArrayIndexOutOfBoundsException)
    - Fixed indexing bug in `intersect()` method (`brick[j][i]` → `brick[i][j]`)
    - Fixed indexing bug in `merge()` method (`brick[j][i]` → `brick[i][j]`)
    - Fixed coordinate calculation in `intersect()` and `merge()` (`targetX = x + j`, `targetY = y + i`)
    - Extracted magic number `50` to `BASE_SCORE_PER_LINE` constant
  - **Reason**: Critical bug fixes and code quality improvements

- **`RandomBrickGenerator.java`** (`src/main/java/com/comp2042/logic/bricks/RandomBrickGenerator.java`)
  - **Changes**:
    - Removed `brickList` field (no longer stores brick instances)
    - Modified `getBrick()` to use `BrickFactory.getBrick()` instead of pulling from list
    - Updated constructor to use `BrickFactory` for initial brick generation
  - **Reason**: Integration with Factory Pattern implementation

## Unexpected Problems

### 1. Matrix Dimension Mismatch
- **Problem**: Board matrix was initialized as `[width][height]` but logic expected `[height][width]` (row-major)
- **Impact**: Caused `ArrayIndexOutOfBoundsException` and game freezing
- **Solution**: Changed initialization to `new int[height][width]` in `SimpleBoard` constructor and `newGame()` method
- **Location**: `SimpleBoard.java`

### 2. Coordinate Calculation Bug
- **Problem**: `intersect()` and `merge()` methods had swapped coordinate calculations (`targetX = x + i` instead of `targetX = x + j`)
- **Impact**: Incorrect collision detection and brick placement, causing rotation crashes
- **Solution**: Fixed coordinate calculations to use `targetX = x + j` and `targetY = y + i`
- **Location**: `MatrixOperations.java`

### 3. Array Indexing Bug
- **Problem**: `intersect()` and `merge()` methods accessed brick array as `brick[j][i]` instead of `brick[i][j]`
- **Impact**: Incorrect brick shape data access, causing visual glitches and crashes
- **Solution**: Changed to `brick[i][j]` to correctly access row `i`, column `j`
- **Location**: `MatrixOperations.java`

### 4. Null EventListener
- **Problem**: `GameInputHandler` had null `eventListener` because it was initialized before `GuiController.setEventListener()` was called
- **Impact**: Left/right/rotate keys not working
- **Solution**: Made `eventListener` non-final, added `setEventListener()` method, and updated `GuiController` to call setter after initialization
- **Location**: `GameInputHandler.java`, `GuiController.java`

### 5. Out of Bounds Check Missing
- **Problem**: `isOutOfBounds()` method didn't check for negative `targetY` values
- **Impact**: `ArrayIndexOutOfBoundsException` when checking collisions at top of board
- **Solution**: Added `targetY >= 0` check to the condition
- **Location**: `MatrixOperations.java`

### 6. JavaFX Media Dependency
- **Problem**: `javafx.scene.media` package not found during compilation
- **Impact**: Compilation failure for `MainMenuController`
- **Solution**: Added `javafx-media` dependency to `pom.xml` with Windows classifier
- **Location**: `pom.xml`

### 7. FXML Syntax Error
- **Problem**: `mainMenuLayout.fxml` had incorrect `VBox.margin` syntax
- **Impact**: FXML loading failure
- **Solution**: Changed to proper nested `<VBox.margin><Insets .../></VBox.margin>` syntax
- **Location**: `mainMenuLayout.fxml`

### 8. Game Board Centering Issues
- **Problem**: Game board and panels not centered after window resize
- **Impact**: Poor UI layout when window is resized
- **Solution**: Implemented `setupResizeListener()` with dynamic centering logic for all UI elements
- **Location**: `GuiController.java`

### 9. Ghost Piece Positioning
- **Problem**: Ghost piece Y-coordinate calculation used wrong gap value (`getHgap()` instead of `getVgap()`)
- **Impact**: Ghost piece appeared at incorrect vertical position
- **Solution**: Fixed calculation to use `brickPanel.getVgap()` for Y-coordinate
- **Location**: `GuiController.java`

### 10. Treasure Hunt Initial Display
- **Problem**: Pre-filled rows in TREASURE_HUNT mode not visible until first brick landed
- **Impact**: Players couldn't see the initial game state
- **Solution**: Fixed `initializeBackgroundGrid()` to use actual `boardMatrix` values instead of setting all to `TRANSPARENT`
- **Location**: `GuiController.java`

### 11. Game Controller Initialization
- **Problem**: `GameController` called `board.createNewBrick()` instead of `board.newGame()` in constructor
- **Impact**: `initTreasureField()` not called for TREASURE_HUNT mode
- **Solution**: Changed to `board.newGame()` to ensure proper initialization
- **Location**: `GameController.java`

## Testing

### Test Coverage
The project includes comprehensive JUnit 5 test suites:

- **`MatrixOperationsTest.java`**: Tests for collision detection (`intersect`), bounds checking (`isOutOfBounds`), row clearing (`checkRemoving`), and merging (`merge`)
- **`HighScoreManagerTest.java`**: Tests for default score, score updates, persistence, and update validation
- **`BrickFactoryTest.java`**: Tests for valid brick creation, specific type validation, and invalid input handling
- **`SimpleBoardTest.java`**: Tests for board initialization, brick movement, score initialization, and game mode functionality

### Running Tests
```bash
mvn test
```

## Technologies Used

- **JavaFX 21.0.6**: UI framework for desktop application
- **Maven**: Build automation and dependency management
- **JUnit 5.12.1**: Unit testing framework
- **Java 23**: Programming language

---

**Note**: Ensure all resource files (images, sounds, videos) are present in `src/main/resources/` for the application to run correctly.

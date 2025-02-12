# GwentStone Lite - A Strategic Two-Player, Turn-Based Card Game

## Description
- Implemented a simplified version of a card game that combines elements from
  Gwent and Hearthstone.
- Each game consists of two players who take turns drawing cards from their
  chosen deck, placing them on the game board and attacking the opponent's cards
  or using the cards' different abilities to harm the enemy's cards.
- Each player has a hero which also comes with different abilities for the
  player to use to harm the enemy's cards or boost his own cards.
- The game ends when one of the players' hero's health reaches 0.

## Solution Components

### Repository
- **Role**: The main class that manages the game logic, including game
  initialization, round and turn management and player actions.
- Possible actions include placing a card on the table on the designed spot for
  the chosen card, attacking a card at a position given by some coordinates,
  using a card's ability on another card, attacking the enemy hero or using the
  hero's ability on the chosen row of cards. Each action is checked if valid
  before continuing and an error message is printed if the action is not valid.
- Debug commands are available at all times to display the game state, printing
  information such as the cards on the board, the cards in the players' hands,
  the players' hero etc.
- **Relationships**: Interacts with `GameBoard`, `Player`, `Card`, `Minion`,
  `Hero`, `ActionsInput`, `ActionUtils` and `DebugUtils`.

### GameBoard and GameBoardSpace
- **Role**: Represents the game board where cards are placed, implemented as a
  four lines and five columns matrix of GameBoardSpace objects.
- Each GameBoardSpace object contains a Minion object that represents the
  card placed on that space (which can only be a minion) and the coordinates of
  the current space on the table.
- The GameBoard class has methods for getting the card at a specific position,
  removing the card at a specific position, placing a card at the best available
  position and checking if a game board line is full.
- **Relationships**: Used by `Repository` to get, place and remove cards on the
  table.

### Card, Minion and Hero
- **Role**: Represents a card in the game, with its data such as name, attack,
  health, mana cost, description etc.
- The Minion class extends the Card class, adding fields for checking if a card
  is frozen or has attacked this turn and adds a method for checking if a minion
  is a tank and getters and setters for the minion's fields.
- The Hero class extends the Card class, adding fields for checking if the hero
  has already attacked this turn and adds getters and setters for the hero's
  fields.
- **Relationships**: Used by `Repository` and `Player` to manage the cards in
  the game.

### Player
- **Role**: Represents a player in the game, including its data such as mana,
  hero, cards in hand and cards in deck.
- The class contains getters and setters for the player's fields and for
  removing a card from the player's hand.
- The class also contains a method for initializing each player's deck,
  shuffling it and getting their hero.
- **Relationships**: Used by `Repository` to manage player actions and state.

### DebugUtils
- **Role**: Contains utility methods for obtaining debug data.
- The class contains methods for printing all debug and statistics data
  requested by the user, such as the cards on the board, the cards in the players
  hands, the players' hero etc.
- **Relationships**: Used by `Repository` to get information about the game
  state.


### ActionUtils
- **Role**: Contains utility methods for checking game conditions and handling
  possible errors of different actions.
- The class contains methods for checking if the attacked player has any tanks
  on the matrix table and for checking on which row of the table matrix
  should a given card be placed.
- The class also contains methods for managing the error messages that are
  printed when an action is not valid due to the attacker being frozen, equal to
  the attacked, on cooldown or the card attacked not being a tank (because the
  tanks need to be eliminated first).
- **Relationships**: Used by `Repository` to check conditions before executing
  actions.

## Usage of the given `fileio` classes
- The only changes made in the `fileio` classes were in the
  `CardInput` and `Coordinates` classes, where I added constructors to create
  new objects with the given data.

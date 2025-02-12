package org.poo.gameRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Hero;
import org.poo.entities.Minion;
import org.poo.entities.Player;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.Coordinates;
import org.poo.fileio.GameInput;
import org.poo.fileio.Input;
import org.poo.utils.ActionUtils;
import org.poo.utils.DebugUtils;


import java.util.Objects;

import static org.poo.utils.Constants.MAX_MANA;
import static org.poo.utils.Constants.NUM_COLS;


public final class Repository {
    private final Input gameInput;
    private final ArrayNode gameOutput;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private GameBoard gameBoard = new GameBoard();

    private Player playerOne = new Player();
    private Player playerTwo = new Player();

    private int mana;
    private int startingPlayer;
    private int currentPlayer;
    private int gamesPlayed;

    private int playerOneWins;
    private int playerTwoWins;

    public Repository(final Input input, final ArrayNode output) {
        this.gameInput = input;
        this.gameOutput = output;
        this.gamesPlayed = 0;
        this.playerOneWins = 0;
        this.playerTwoWins = 0;
        this.mana = 1;
        getGameSessions();
    }

    /**
     * Starts the current game.
     * @param game the current game start data
     */
    public void gameStart(final GameInput game) {
        int seed = game.getStartGame().getShuffleSeed();
        this.mana = 1;

        this.playerOne = new Player();
        this.playerTwo = new Player();
        this.gameBoard = new GameBoard();

        this.playerOne.playerStart(this.gameInput, game, 1, seed);
        this.playerTwo.playerStart(this.gameInput, game, 2, seed);

        this.gamesPlayed++;

        this.startingPlayer = game.getStartGame().getStartingPlayer();
        this.currentPlayer = this.startingPlayer;
    }


    /**
     * Starts the next round.
     */
    public void roundStart() {
        // adding the next card into the players' hands
        if (!playerOne.getPlayerDeck().isEmpty()) {
            Minion nextCard = playerOne.getPlayerDeck().get(0);
            this.playerOne.getPlayerHand().add(nextCard);
            playerOne.getPlayerDeck().remove(0);
        }

        if (!playerTwo.getPlayerDeck().isEmpty()) {
            Minion nextCard = playerTwo.getPlayerDeck().get(0);
            this.playerTwo.getPlayerHand().add(nextCard);
            playerTwo.getPlayerDeck().remove(0);
        }

        // giving mana to the players
        playerOne.setMana(playerOne.getMana() + this.mana);
        playerTwo.setMana(playerTwo.getMana() + this.mana);

        if (this.mana < MAX_MANA) {
            this.mana++;
        }
    }

    /**
     * Ends the turn for the current player.
     */
    public void turnEnd() {
        if (this.currentPlayer == 1) {
            ActionUtils.unfreezeCards(this.gameBoard, this.currentPlayer);
            ActionUtils.resetCardsCooldown(this.gameBoard, this.currentPlayer);
            this.playerOne.setHeroCooldown(false);
            this.currentPlayer = 2;
        } else {
            ActionUtils.unfreezeCards(this.gameBoard, this.currentPlayer);
            ActionUtils.resetCardsCooldown(this.gameBoard, this.currentPlayer);
            this.playerTwo.setHeroCooldown(false);
            this.currentPlayer = 1;
        }

        // if the current player and starting player match, a new round starts
        if (this.currentPlayer == this.startingPlayer) {
            roundStart();
        }
    }

    /**
     * Places a card on a spot at the table corresponding to the given player.
     * @param player the given player
     * @param handIdx the card index in the player's hand
     * @param playerIdx the player index
     */
    private void placeCardOnTable(final Player player, final int handIdx, final int playerIdx) {
        // if the player has no cards in hand, the function returns
        if (player.getPlayerHand().isEmpty()) {
            return;
        }

        Minion cardToPlace = player.getPlayerHand().get(handIdx);

        // checks if there is enough mana to place the card
        if (cardToPlace.getMana() <= player.getMana()) {
            // places card at the corresponding line for the current player
            int lineToPlace = ActionUtils.checkLinePlacement(cardToPlace.getName(), playerIdx);
            if (!gameBoard.isGameBoardLineFull(lineToPlace)) {
                // places card only if the line is not full
                this.gameBoard.placeCardOnBoard(cardToPlace, lineToPlace);
                player.setMana(player.getMana() - cardToPlace.getMana());
                player.removeCardFromHand(cardToPlace);
            } else {
                ObjectNode objectNode = objectMapper.createObjectNode();
                this.gameOutput.add(objectNode);
                objectNode.put("command", "placeCard");
                objectNode.put("handIdx", handIdx);
                objectNode.put("error",
                        "Cannot place card on table since row is full.");
            }
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            this.gameOutput.add(objectNode);
            objectNode.put("command", "placeCard");
            objectNode.put("handIdx", handIdx);
            objectNode.put("error", "Not enough mana to place card on table.");
        }
    }

    /**
     * Card attacks another card on the table.
     * @param actionsInput the input command object
     */
    private void cardAttacksCard(final ActionsInput actionsInput) {
        // sets up the attacker and the attacked player and their cards
        ObjectNode objectNode = this.objectMapper.createObjectNode();

        Coordinates attackerCoordinates = actionsInput.getCardAttacker();
        Coordinates attackedCoordinates = actionsInput.getCardAttacked();

        Minion attackerCard = this.gameBoard.getCardAtCoordinates(attackerCoordinates);
        Minion attackedCard = this.gameBoard.getCardAtCoordinates(attackedCoordinates);

        if (attackedCard == null || attackerCard == null) {
            return;
        }

        Player attackerPlayer = this.playerOne;
        Player attackedPlayer = this.playerTwo;
        int attackedIdx = 2;

        if (attackerCoordinates.getX() <= 1) {
            attackerPlayer = this.playerTwo;
        }

        if (attackedCoordinates.getX() >= 2) {
            attackedPlayer = this.playerOne;
            attackedIdx = 1;
        }

        // checks for invalid cases
        if (Objects.equals(attackerPlayer, attackedPlayer)) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAttack");
            ActionUtils.attackerIsNotEnemy(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        if (attackerCard.hasAttacked()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAttack");
            ActionUtils.attackerCooldown(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        if (attackerCard.isFrozen()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAttack");
            ActionUtils.attackerIsFrozen(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        if (ActionUtils.checkForTanks(this.gameBoard, attackedIdx) && !attackedCard.isTank()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAttack");
            ActionUtils.attackedIsNotTank(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        // performs the attack and inserts the attacker card into the list of cards
        // that have already attacked this turn
        attackedCard.setHealth(attackedCard.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);

        // checks if the attacked card has been eliminated
        if (attackedCard.getHealth() <= 0) {
            this.gameBoard.removeCardAtCoordinates(attackedCoordinates);
        }
    }

    /**
     * Card uses its ability on another Card.
     * @param actionsInput the input command object
     */
    private void cardUsesAbility(final ActionsInput actionsInput) {
        // sets up the attacker and the attacked player and their cards
        ObjectNode objectNode = this.objectMapper.createObjectNode();

        Coordinates attackerCoordinates = actionsInput.getCardAttacker();
        Coordinates attackedCoordinates = actionsInput.getCardAttacked();

        Minion attackerCard = this.gameBoard.getCardAtCoordinates(attackerCoordinates);
        Minion attackedCard = this.gameBoard.getCardAtCoordinates(attackedCoordinates);

        if (attackedCard == null || attackerCard == null) {
            return;
        }

        Player attackerPlayer = this.playerOne;
        Player attackedPlayer = this.playerTwo;
        int attackedIdx = 2;

        if (attackerCoordinates.getX() <= 1) {
            attackerPlayer = this.playerTwo;
        }

        if (attackedCoordinates.getX() >= 2) {
            attackedPlayer = this.playerOne;
            attackedIdx = 1;
        }

        // checks for invalid cases
        if (attackerCard.isFrozen()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAbility");
            ActionUtils.attackerIsFrozen(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        if (attackerCard.hasAttacked()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAbility");
            ActionUtils.attackerCooldown(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        if (!Objects.equals(attackerPlayer, attackedPlayer)
                && Objects.equals(attackerCard.getName(), "Disciple")) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAbility");
            ActionUtils.attackerIsNotFriend(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        if (Objects.equals(attackerPlayer, attackedPlayer)
                && (Objects.equals(attackerCard.getName(), "The Ripper")
                || Objects.equals(attackerCard.getName(), "Miraj")
                || Objects.equals(attackerCard.getName(), "The Cursed One"))) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAbility");
            ActionUtils.attackerIsNotEnemy(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        if (ActionUtils.checkForTanks(this.gameBoard, attackedIdx) && !attackedCard.isTank()
                && !Objects.equals(attackerCard.getName(), "Disciple")) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "cardUsesAbility");
            ActionUtils.attackedIsNotTank(objectNode, attackerCoordinates, attackedCoordinates);
            return;
        }

        // checks the attacker card's name to apply different abilities
        switch (attackerCard.getName()) {
            case "The Ripper":
                // applies Weak Knees
                attackedCard.setAttackDamage(attackedCard.getAttackDamage() - 2);
                if (attackedCard.getAttackDamage() < 0) {
                    attackedCard.setAttackDamage(0);
                }
                break;
            case "Miraj":
                // applies Skyjack
                int attackerHealth = attackerCard.getHealth();
                int attackedHealth = attackedCard.getHealth();

                attackedCard.setHealth(attackerHealth);
                attackerCard.setHealth(attackedHealth);
                break;
            case "The Cursed One":
                // applies Shapeshift
                attackedHealth = attackedCard.getHealth();
                int attackedDamage = attackedCard.getAttackDamage();

                attackedCard.setHealth(attackedDamage);
                attackedCard.setAttackDamage(attackedHealth);

                if (attackedCard.getHealth() <= 0) {
                    this.gameBoard.removeCardAtCoordinates(attackedCoordinates);
                }
                break;
            case "Disciple":
                // applies God's Plan
                attackedCard.setHealth(attackedCard.getHealth() + 2);
                break;
            default:
                return;
        }

        // inserts the attacker card into the list of cards that have already attacked this turn
        attackerCard.setHasAttacked(true);
    }

    /**
     * Card attacks the enemy hero.
     * @param actionsInput the input command object
     */
    private void cardAttacksHero(final ActionsInput actionsInput) {
        // sets up the attacker and the attacked player and their cards
        ObjectNode objectNode = this.objectMapper.createObjectNode();
        Coordinates attackerCoordinates = actionsInput.getCardAttacker();

        Minion attackerCard = this.gameBoard.getCardAtCoordinates(attackerCoordinates);

        Player attackedPlayer = this.playerTwo;
        Player attackerPlayer = this.playerOne;
        int attackedIdx = 2;

        if (attackerCoordinates.getX() <= 1) {
            attackedPlayer = this.playerOne;
            attackerPlayer = this.playerTwo;
            attackedIdx = 1;
        }

        // checks for invalid cases
        if (attackerCard.isFrozen()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "useAttackHero");
            ActionUtils.attackerIsFrozen(objectNode, attackerCoordinates, null);
            return;
        }

        if (attackerCard.hasAttacked()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "useAttackHero");
            ActionUtils.attackerCooldown(objectNode, attackerCoordinates, null);
            return;
        }

        if (ActionUtils.checkForTanks(this.gameBoard, attackedIdx)) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "useAttackHero");
            ActionUtils.attackedIsNotTank(objectNode, attackerCoordinates, null);
            return;
        }

        // performs the attack and inserts the attacker card into the list of cards
        // that have already attacked this turn
        attackedPlayer.getPlayerHero().setHealth(attackedPlayer.getPlayerHero().getHealth()
                - attackerCard.getAttackDamage());
        attackerCard.setHasAttacked(true);

        // checks if the enemy hero has been defeated, then ends the game
        if (attackedPlayer.getPlayerHero().getHealth() <= 0) {
            this.gameOutput.add(objectNode);
            if (Objects.equals(attackerPlayer, this.playerOne)) {
                objectNode.put("gameEnded", "Player one killed the enemy hero.");
                this.playerOneWins++;
            } else {
                objectNode.put("gameEnded", "Player two killed the enemy hero.");
                this.playerTwoWins++;
            }
        }

    }

    /**
     * The hero uses an ability on the enemy's cards.
     * @param actionsInput the input command object
     */
    private void heroUsesAbility(final ActionsInput actionsInput) {
        // sets up the attacker and the attacked player and cards
        ObjectNode objectNode = this.objectMapper.createObjectNode();

        int affectedRow = actionsInput.getAffectedRow();

        Player attackerPlayer = this.playerTwo;
        Player attackedPlayer = this.playerOne;

        boolean rowBelongsToEnemy = true;

        if (this.currentPlayer == 1) {
            attackerPlayer = this.playerOne;
            attackedPlayer = this.playerTwo;
        }

        Hero attackerHero = attackerPlayer.getPlayerHero();

        // checks if the affected row doesn't belong to the enemy
        if ((affectedRow <= 1 && Objects.equals(attackerPlayer, this.playerTwo))
                || (affectedRow >= 2 && Objects.equals(attackerPlayer, this.playerOne))) {
            rowBelongsToEnemy = false;
        }

        // checks for invalid cases
        if (attackerPlayer.getMana() < attackerHero.getMana()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "useHeroAbility");
            objectNode.put("affectedRow", affectedRow);
            objectNode.put("error", "Not enough mana to use hero's ability.");
            return;
        }

        if (attackerPlayer.isHeroCooldown()) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "useHeroAbility");
            objectNode.put("affectedRow", affectedRow);
            objectNode.put("error", "Hero has already attacked this turn.");
            return;
        }

        if ((Objects.equals(attackerHero.getName(), "Lord Royce")
                || Objects.equals(attackerHero.getName(), "Empress Thorina"))
                && !rowBelongsToEnemy) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "useHeroAbility");
            objectNode.put("affectedRow", affectedRow);
            objectNode.put("error", "Selected row does not belong to the enemy.");
            return;
        }

        if ((Objects.equals(attackerHero.getName(), "General Kocioraw")
                || Objects.equals(attackerHero.getName(), "King Mudface"))
                && rowBelongsToEnemy) {
            this.gameOutput.add(objectNode);
            objectNode.put("command", "useHeroAbility");
            objectNode.put("affectedRow", affectedRow);
            objectNode.put("error",
                    "Selected row does not belong to the current player.");
            return;
        }

        // checks the attacker hero's name to apply different abilities
        switch (attackerHero.getName()) {
            case "Lord Royce":
                // applies Sub-Zero
                for (int col = 0; col < NUM_COLS; col++) {
                    Minion currentCard =
                            this.gameBoard.getCardAtCoordinates(new Coordinates(affectedRow, col));
                    if (currentCard != null) {
                        currentCard.setFrozen(true);
                    }
                }
                break;
            case "Empress Thorina":
                // applies Low Blow
                int maxHealth = 0;
                int position = 0;
                for (int col = 0; col < NUM_COLS; col++) {
                    Minion currentCard =
                            this.gameBoard.getCardAtCoordinates(new Coordinates(affectedRow, col));
                    if (currentCard != null && currentCard.getHealth() > maxHealth) {
                        maxHealth = currentCard.getHealth();
                        position = col;
                    }
                }
                this.gameBoard.removeCardAtCoordinates(new Coordinates(affectedRow, position));
                break;
            case "King Mudface":
                // applies Earth Born
                for (int col = 0; col < NUM_COLS; col++) {
                    Minion currentCard =
                            this.gameBoard.getCardAtCoordinates(new Coordinates(affectedRow, col));
                    if (currentCard != null) {
                        currentCard.setHealth(currentCard.getHealth() + 1);
                    }
                }
                break;
            case "General Kocioraw":
                // applies Blood Thirst
                for (int col = 0; col < NUM_COLS; col++) {
                    Minion currentCard =
                            this.gameBoard.getCardAtCoordinates(new Coordinates(affectedRow, col));
                    if (currentCard != null) {
                        currentCard.setAttackDamage(currentCard.getAttackDamage() + 1);
                    }
                }
                break;
            default:
                return;
        }

        // sets the attacker hero's cooldown and decreases the attacker's mana
        attackerPlayer.setHeroCooldown(true);
        attackerPlayer.setMana(attackerPlayer.getMana() - attackerHero.getMana());
    }

    /**
     * Receives the given action input and modifies the output accordingly.
     * @param actionsInput the input command object
     */
    private void cardAction(final ActionsInput actionsInput) {
        String action = actionsInput.getCommand();

        switch (action) {
            case "placeCard":
                int handIdx = actionsInput.getHandIdx();
                if (this.currentPlayer == 1) {
                    placeCardOnTable(this.playerOne, handIdx, 1);
                } else {
                    placeCardOnTable(this.playerTwo, handIdx, 2);
                }
                break;
            case "cardUsesAttack":
                cardAttacksCard(actionsInput);
                break;
            case "cardUsesAbility":
                cardUsesAbility(actionsInput);
                break;
            case "useAttackHero":
                cardAttacksHero(actionsInput);
                break;
            case "useHeroAbility":
                heroUsesAbility(actionsInput);
                break;
            case "endPlayerTurn":
                turnEnd();
                break;
            default:
                return;
        }
    }

    /**
     * Receives the given input for debugging and modifies the output accordingly.
     * @param gameAction the input command object
     */
    public void getDebugData(final ActionsInput gameAction) {
        String command = gameAction.getCommand();
        int playerIdx = gameAction.getPlayerIdx();

        switch (command) {
            case "getCardsInHand":
                if (playerIdx == 1) {
                    DebugUtils.getCardsInHand(this.gameOutput, this.playerOne, playerIdx, command);
                } else {
                    DebugUtils.getCardsInHand(this.gameOutput, this.playerTwo, playerIdx, command);
                }
                break;
            case "getPlayerDeck":
                if (playerIdx == 1) {
                    DebugUtils.getPlayerDeck(this.gameOutput, this.playerOne, playerIdx, command);
                } else {
                    DebugUtils.getPlayerDeck(this.gameOutput, this.playerTwo, playerIdx, command);
                }
                break;
            case "getCardsOnTable":
                DebugUtils.getCardsOnTable(this.gameOutput, this.gameBoard, command);
                break;
            case "getPlayerTurn":
                DebugUtils.getPlayerTurn(this.gameOutput, command, this.currentPlayer);
                break;
            case "getPlayerHero":
                if (playerIdx == 1) {
                    DebugUtils.getPlayerHero(this.gameOutput, this.playerOne, playerIdx, command);
                } else {
                    DebugUtils.getPlayerHero(this.gameOutput, this.playerTwo, playerIdx, command);
                }
                break;
            case "getCardAtPosition":
                DebugUtils.getCardAtPosition(this.gameOutput, this.gameBoard, gameAction, command);
                break;
            case "getPlayerMana":
                if (playerIdx == 1) {
                    DebugUtils.getPlayerMana(this.gameOutput, this.playerOne, playerIdx, command);
                } else {
                    DebugUtils.getPlayerMana(this.gameOutput, this.playerTwo, playerIdx, command);
                }
                break;
            case "getFrozenCardsOnTable":
                DebugUtils.getFrozenCardsOnTable(this.gameOutput, this.gameBoard, command,
                        this.playerOne, this.playerTwo);
                break;
            case "getTotalGamesPlayed":
                DebugUtils.getTotalGamesPlayed(this.gameOutput, command, this.gamesPlayed);
                break;
            case "getPlayerOneWins":
                DebugUtils.getPlayerWins(this.gameOutput, command, this.playerOneWins);
                break;
            case "getPlayerTwoWins":
                DebugUtils.getPlayerWins(this.gameOutput, command, this.playerTwoWins);
                break;
            default:
                return;
        }
    }

    /**
     * Controls the game sessions.
     */
    private void getGameSessions() {
        for (GameInput currentGame : this.gameInput.getGames()) {
            gameStart(currentGame);
            roundStart();
            for (ActionsInput action : currentGame.getActions()) {
                getDebugData(action);
                cardAction(action);
            }
        }
    }
}

package org.poo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Minion;
import org.poo.entities.Player;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.gameRepository.GameBoard;

import static org.poo.utils.Constants.NUM_COLS;
import static org.poo.utils.Constants.NUM_LINES;


public final class DebugUtils {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DebugUtils() {

    }

    /**
     * Returns an ObjectNode containing the given card's information.
     * @param cardInput the card to be parsed
     * @param isHero if the card is a hero, for special fields
     * @return the card ObjectNode
     */
    public static ObjectNode cardNode(final CardInput cardInput, final boolean isHero) {
        ObjectNode cardNode = OBJECT_MAPPER.createObjectNode();

        cardNode.put("mana", cardInput.getMana());

        if (!isHero) {
            cardNode.put("attackDamage", cardInput.getAttackDamage());
            cardNode.put("health", cardInput.getHealth());
        }

        cardNode.put("description", cardInput.getDescription());

        ArrayNode colors = OBJECT_MAPPER.createArrayNode();
        for (String color : cardInput.getColors()) {
            colors.add(color);
        }
        cardNode.set("colors", colors);

        cardNode.put("name", cardInput.getName());

        if (isHero) {
            cardNode.put("health", cardInput.getHealth());
        }

        return cardNode;
    }

    /**
     * Prints the player's mana into an ObjectNode, which is added into the given ArrayNode.
     * @param gameOutput the ArrayNode
     * @param player the given player
     * @param playerIdx the player's index
     * @param command the command name
     */
    public static void getPlayerMana(final ArrayNode gameOutput, final Player player,
                                     final int playerIdx, final String command) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("playerIdx", playerIdx);
        objectNode.put("output", player.getMana());
    }

    /**
     * Prints the current player's turn into the given ArrayNode.
     * @param gameOutput the ArrayNode to be printed in
     * @param command the command name
     * @param currentPlayer the current player index
     */
    public static void getPlayerTurn(final ArrayNode gameOutput, final String command,
                                     final int currentPlayer) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("output", currentPlayer);
    }

    /**
     * Prints the given player's deck into the given ArrayNode.
     * @param gameOutput the ArrayNode to be printed in
     * @param player the given player
     * @param playerIdx the given player's index
     * @param command the command name
     */
    public static void getPlayerDeck(final ArrayNode gameOutput, final Player player,
                                     final int playerIdx, final String command) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("playerIdx", playerIdx);

        ArrayNode deck = OBJECT_MAPPER.createArrayNode();

        for (Minion card : player.getPlayerDeck()) {
            ObjectNode cardNode = cardNode(card.getCardInput(), false);
            deck.add(cardNode);
        }

        objectNode.set("output", deck);
    }

    /**
     * Prints the given player's hero into the given ArrayNode.
     * @param gameOutput the ArrayNode to be printed in
     * @param player the given player
     * @param playerIdx the given player's index
     * @param command the command name
     */
    public static void getPlayerHero(final ArrayNode gameOutput, final Player player,
                                     final int playerIdx, final String command) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("playerIdx", playerIdx);
        ObjectNode heroNode = cardNode(player.getPlayerHero().getCardInput(), true);
        objectNode.set("output", heroNode);
    }

    /**
     * Prints the given player's hand into the given ArrayNode.
     * @param gameOutput the ArrayNode to be printed in
     * @param player the given player
     * @param playerIdx the given player's index
     * @param command the command name
     */
    public static void getCardsInHand(final ArrayNode gameOutput, final Player player,
                                      final int playerIdx, final String command) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("playerIdx", playerIdx);
        ArrayNode hand = OBJECT_MAPPER.createArrayNode();

        for (Minion card : player.getPlayerHand()) {
            ObjectNode cardNode = cardNode(card.getCardInput(), false);
            hand.add(cardNode);
        }

        objectNode.set("output", hand);
    }

    /**
     * Prints the card at the given position into the given ArrayNode.
     * @param gameOutput the ArrayNode to be printed in
     * @param gameBoard the game board
     * @param gameAction the input command object
     * @param command the command name
     */
    public static void getCardAtPosition(final ArrayNode gameOutput, final GameBoard gameBoard,
                                         final ActionsInput gameAction, final String command) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("x", gameAction.getX());
        objectNode.put("y", gameAction.getY());
        Coordinates coordinates = new Coordinates(gameAction.getX(), gameAction.getY());

        if (gameBoard.getCardAtCoordinates(coordinates) != null) {
            ObjectNode cardNode =
                    cardNode(gameBoard.getCardAtCoordinates(coordinates).getCardInput(), false);
            objectNode.set("output", cardNode);
        } else {
            objectNode.put("output", "No card available at that position.");
        }
    }

    /**
     * Prints all the cards on the table into the given ArrayNode by iterating through
     * the game board.
     * @param gameOutput the ArrayNode to be printed in
     * @param gameBoard the game board
     * @param command the command name
     */
    public static void getCardsOnTable(final ArrayNode gameOutput, final GameBoard gameBoard,
                                       final String command) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        ArrayNode table = OBJECT_MAPPER.createArrayNode();

        for (int line = 0; line < NUM_LINES; line++) {
            ArrayNode currentLine = OBJECT_MAPPER.createArrayNode();
            for (int col = 0; col < NUM_COLS; col++) {
                Coordinates coordinates = new Coordinates(line, col);
                Minion cardAtCoordinates = gameBoard.getCardAtCoordinates(coordinates);
                if (cardAtCoordinates != null) {
                    ObjectNode currentCard = DebugUtils.cardNode(cardAtCoordinates.getCardInput(),
                            false);
                    currentLine.add(currentCard);
                }
            }
            table.add(currentLine);
        }
        objectNode.set("output", table);
    }

    /**
     * Prints all the frozen cards on the table into the given ArrayNode by iterating through
     * the game board.
     * @param gameOutput the ArrayNode to be printed in
     * @param gameBoard the game board
     * @param command the command name
     * @param playerOne the first player
     * @param playerTwo the second player
     */
    public static void getFrozenCardsOnTable(final ArrayNode gameOutput, final GameBoard gameBoard,
                                             final String command, final Player playerOne,
                                             final Player playerTwo) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        ArrayNode table = OBJECT_MAPPER.createArrayNode();

        for (int line = 0; line < NUM_LINES; line++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Coordinates coordinates = new Coordinates(line, col);
                Minion cardAtCoordinates = gameBoard.getCardAtCoordinates(coordinates);
                if (cardAtCoordinates != null && cardAtCoordinates.isFrozen()) {
                    ObjectNode currentCard = DebugUtils.cardNode(cardAtCoordinates.getCardInput(),
                            false);
                    table.add(currentCard);
                }
            }
        }
        objectNode.set("output", table);
    }

    /**
     * Prints the number of wins that the given player has into the given ArrayNode.
     * @param gameOutput the ArrayNode to be printed in
     * @param command the command name
     * @param wins the number of wins of the given player
     */
    public static void getPlayerWins(final ArrayNode gameOutput, final String command,
                                     final int wins) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("output", wins);
    }

    /**
     * Prints the total number of games played.
     * @param gameOutput the ArrayNode to be printed in
     * @param command the command name
     * @param gamesPlayed the number of games played
     */
    public static void getTotalGamesPlayed(final ArrayNode gameOutput, final String command,
                                           final int gamesPlayed) {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        gameOutput.add(objectNode);
        objectNode.put("command", command);
        objectNode.put("output", gamesPlayed);
    }

}

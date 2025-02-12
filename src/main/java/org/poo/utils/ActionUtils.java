package org.poo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Minion;
import org.poo.fileio.Coordinates;
import org.poo.gameRepository.GameBoard;
import java.util.Objects;

import static org.poo.utils.Constants.NUM_COLS;
import static org.poo.utils.Constants.NUM_LINES;


public final class ActionUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ActionUtils() {

    }

    /**
     * Checks if the attacked player has any tanks on the table.
     * @param gameBoard the game board
     * @param attackedIdx the attacked player index
     * @return if the attacked player has tanks
     */
    public static boolean checkForTanks(final GameBoard gameBoard, final int attackedIdx) {
        int row = 2;

        if (attackedIdx == 2) {
            row = 1;
        }

        for (int col = 0; col < NUM_COLS; col++) {
            Minion currentCard =
                    gameBoard.getCardAtCoordinates(new Coordinates(row, col));
            if (currentCard != null && currentCard.isTank()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a card belongs to the back line.
     * @param cardName the card which should be checked
     * @return if the card should be placed in the back line
     */
    public static boolean checkBackLine(final String cardName) {
        return Objects.equals(cardName, "Sentinel") || Objects.equals(cardName, "Berserker")
                || Objects.equals(cardName, "The Cursed One")
                || Objects.equals(cardName, "Disciple");
    }

    /**
     * Checks on which line should the card be placed based on the index of the current player.
     * @param cardName the name of the card to be placed
     * @param playerIdx the index of the current player
     * @return the line at which the card should be placed
     */
    public static int checkLinePlacement(final String cardName, final int playerIdx) {
        int lineToPlace;
        if (playerIdx == 1) {
            if (checkBackLine(cardName)) {
                lineToPlace = NUM_LINES - 1;
            } else {
                lineToPlace = 2;
            }
        } else {
            if (checkBackLine(cardName)) {
                lineToPlace = 0;
            } else {
                lineToPlace = 1;
            }
        }
        return lineToPlace;
    }

    /**
     * Resets the cards that have already attacked this turn.
     */
    public static void resetCardsCooldown(final GameBoard gameBoard, final int playerIdx) {
        if (playerIdx == 1) {
            for (int i = NUM_LINES / 2; i < NUM_LINES; i++) {
                for (int j = 0; j < NUM_COLS; j++) {
                    Minion card = gameBoard.getCardAtCoordinates(new Coordinates(i, j));
                    if (card != null) {
                        card.setHasAttacked(false);
                    }
                }
            }
        } else {
            for (int i = 0; i < NUM_LINES / 2; i++) {
                for (int j = 0; j < NUM_COLS; j++) {
                    Minion card = gameBoard.getCardAtCoordinates(new Coordinates(i, j));
                    if (card != null) {
                        card.setHasAttacked(false);
                    }
                }
            }
        }
    }

    /**
     * Unfreezes the frozen cards.
     */
    public static void unfreezeCards(final GameBoard gameBoard, final int playerIdx) {
        if (playerIdx == 1) {
            for (int i = NUM_LINES / 2; i < NUM_LINES; i++) {
                for (int j = 0; j < NUM_COLS; j++) {
                    Minion card = gameBoard.getCardAtCoordinates(new Coordinates(i, j));
                    if (card != null) {
                        card.setFrozen(false);
                    }
                }
            }
        } else {
            for (int i = 0; i < NUM_LINES / 2; i++) {
                for (int j = 0; j < NUM_COLS; j++) {
                    Minion card = gameBoard.getCardAtCoordinates(new Coordinates(i, j));
                    if (card != null) {
                        card.setFrozen(false);
                    }
                }
            }
        }
    }

    /**
     * Puts the coordinates of the attacker and attacked card in an ObjectNode.
     * @param objectNode the node in which the coordinates will be put
     * @param attacker the attacker card coordinates
     * @param attacked the attacked card coordinates
     */
    public static void putCoordinates(final ObjectNode objectNode, final Coordinates attacker,
                                      final Coordinates attacked) {
        ObjectNode attackerInfo = OBJECT_MAPPER.createObjectNode();
        attackerInfo.put("x", attacker.getX());
        attackerInfo.put("y", attacker.getY());
        objectNode.set("cardAttacker", attackerInfo);

        if (attacked != null) {
            ObjectNode attackedInfo = OBJECT_MAPPER.createObjectNode();
            attackedInfo.put("x", attacked.getX());
            attackedInfo.put("y", attacked.getY());
            objectNode.set("cardAttacked", attackedInfo);
        }
    }

    /**
     * Prints an error message into an ObjectNode if the attacker card is frozen.
     * @param objectNode the output node
     * @param attacker the attacker card coordinates
     * @param attacked the attacked card coordinates
     */
    public static void attackerIsFrozen(final ObjectNode objectNode, final Coordinates attacker,
                                        final Coordinates attacked) {
        putCoordinates(objectNode, attacker, attacked);
        objectNode.put("error", "Attacker card is frozen.");
    }

    /**
     * Prints an error message into an ObjectNode if the attacker card does not belong to the enemy.
     * @param objectNode the output node
     * @param attacker the attacker card coordinates
     * @param attacked the attacked card coordinates
     */
    public static void attackerIsNotEnemy(final ObjectNode objectNode, final Coordinates attacker,
                                          final Coordinates attacked) {
        putCoordinates(objectNode, attacker, attacked);
        objectNode.put("error", "Attacked card does not belong to the enemy.");
    }

    /**
     * Prints an error message into an ObjectNode if the attacker card
     * does not belong to the player.
     * @param objectNode the output node
     * @param attacker the attacker card coordinates
     * @param attacked the attacked card coordinates
     */
    public static void attackerIsNotFriend(final ObjectNode objectNode, final Coordinates attacker,
                                           final Coordinates attacked) {
        putCoordinates(objectNode, attacker, attacked);
        objectNode.put("error", "Attacked card does not belong to the current player.");
    }

    /**
     * Prints an error message into an ObjectNode if the attacker card has already attacked.
     * @param objectNode the output node
     * @param attacker the attacker card coordinates
     * @param attacked the attacked card coordinates
     */
    public static void attackerCooldown(final ObjectNode objectNode, final Coordinates attacker,
                                        final Coordinates attacked) {
        putCoordinates(objectNode, attacker, attacked);
        objectNode.put("error", "Attacker card has already attacked this turn.");
    }

    /**
     * Prints an error message into an ObjectNode if the attacked card is not a tank.
     * @param objectNode the output node
     * @param attacker the attacker card coordinates
     * @param attacked the attacked card coordinates
     */
    public static void attackedIsNotTank(final ObjectNode objectNode, final Coordinates attacker,
                                         final Coordinates attacked) {
        putCoordinates(objectNode, attacker, attacked);
        objectNode.put("error", "Attacked card is not of type 'Tank'.");
    }
}

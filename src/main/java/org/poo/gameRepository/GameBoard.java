package org.poo.gameRepository;

import org.poo.entities.Minion;
import org.poo.fileio.Coordinates;

import static org.poo.utils.Constants.NUM_COLS;
import static org.poo.utils.Constants.NUM_LINES;


public final class GameBoard {
    private final GameBoardSpace[][] gameBoard;

    public GameBoard() {
        this.gameBoard = new GameBoardSpace[NUM_LINES][NUM_COLS];

        for (int line = 0; line < NUM_LINES; line++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Coordinates currentCoordinates = new Coordinates();
                currentCoordinates.setX(line);
                currentCoordinates.setY(col);
                this.gameBoard[line][col] = new GameBoardSpace(null, currentCoordinates);
            }
        }
    }

    /**
     *
     * @param coordinates the coordinates at which the card to be returned
     *                    is placed
     * @return a card from the game board at the given coordinates
     */
    public Minion getCardAtCoordinates(final Coordinates coordinates) {
        return this.gameBoard[coordinates.getX()][coordinates.getY()].getCard();
    }

    /**
     *
     * @param coordinates the coordinates at which the card to be removed
     *                    is placed
     */
    public void removeCardAtCoordinates(final Coordinates coordinates) {
        int line = coordinates.getX();
        int col = coordinates.getY();

        // shifts all cards to the left to accommodate the deletion
        for (int i = col; i < NUM_COLS - 1; i++) {
            this.gameBoard[line][i].setCard(this.gameBoard[line][i + 1].getCard());
        }

        this.gameBoard[line][NUM_COLS - 1].setCard(null);
    }

    /**
     *
     * @param cardToPlace the card to be placed on the game board
     * @param lineToPlace the line at which the card should be placed
     */
    public void placeCardOnBoard(final Minion cardToPlace, final int lineToPlace) {
        int colToPlace = getNextIdx(lineToPlace);
        GameBoardSpace cardOnBoard = new GameBoardSpace(new Minion(cardToPlace),
                new Coordinates(lineToPlace, colToPlace));
        this.gameBoard[lineToPlace][colToPlace] = cardOnBoard;
    }

    /**
     * Returns the first available position on the given line to place a new card.
     * @param lineToSearch the line to search for the first available position
     *                     in the game board
     * @return the first available position
     */
    public int getNextIdx(final int lineToSearch) {
        int col;
        for (col = 0; col < NUM_COLS; col++) {
            if (this.gameBoard[lineToSearch][col].getCard() == null) {
                break;
            }
        }
        return col;
    }

    /**
     * Checks if a given line on the game board is full;
     * @param line the line to check if full
     * @return if the line is full
     */
    public boolean isGameBoardLineFull(final int line) {
        return this.gameBoard[line][NUM_COLS - 1].getCard() != null;
    }
}

package org.poo.gameRepository;


import org.poo.entities.Minion;
import org.poo.fileio.Coordinates;

public final class GameBoardSpace {
    private Minion card;
    private Coordinates coordinates;

    public GameBoardSpace(final Minion card, final Coordinates coordinates) {
        this.card = card;
        this.coordinates = coordinates;
    }

    public Minion getCard() {
        return card;
    }

    public void setCard(final Minion card) {
        this.card = card;
    }
}

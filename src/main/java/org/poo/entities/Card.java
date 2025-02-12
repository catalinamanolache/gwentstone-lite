package org.poo.entities;

import org.poo.fileio.CardInput;
import java.util.Objects;

public class Card {
    private CardInput cardInput;

    public Card() {
        this.cardInput = new CardInput();
    }

    public Card(final CardInput cardInput) {
        this.cardInput = new CardInput();
        this.cardInput.setAttackDamage(cardInput.getAttackDamage());
        this.cardInput.setHealth(cardInput.getHealth());
        this.cardInput.setMana(cardInput.getMana());
        this.cardInput.setName(cardInput.getName());
        this.cardInput.setDescription(cardInput.getDescription());
        this.cardInput.setColors(cardInput.getColors());
    }

    /**
     * Gets the card input.
     * @return the card input
     */
    public CardInput getCardInput() {
        return cardInput;
    }

    /**
     * Checks if a given card is a hero.
     * @return if the card is a hero
     */
    public boolean isHero() {
        return Objects.equals(this.getCardInput().getName(), "Lord Royce")
                || Objects.equals(this.getCardInput().getName(), "Empress Thorina")
                || Objects.equals(this.getCardInput().getName(), "King Mudface")
                || Objects.equals(this.getCardInput().getName(), "General Kocioraw");
    }
}

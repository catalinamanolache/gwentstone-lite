package org.poo.entities;

import org.poo.fileio.CardInput;
import java.util.ArrayList;

public class Hero extends Card {
    private boolean hasAttacked;

    public Hero() {
        super();
        hasAttacked = false;
    }

    public Hero(final CardInput cardInput) {
        super(cardInput);
        hasAttacked = false;
    }

    /**
     * Checks if the hero has attacked this turn.
     * @return if the hero has attacked this turn
     */
    public boolean hasAttacked() {
        return hasAttacked;
    }

    /**
     * Sets the hero's attack status.
     * @param hasAttacked the hero's attack status
     */
    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    /**
     * Gets the mana cost of the card.
     * @return the mana cost of the card
     */
    public int getMana() {
        return getCardInput().getMana();
    }

    /**
     * Gets the attack damage of the card.
     * @return the attack damage of the card
     */
    public int getAttackDamage() {
        return getCardInput().getAttackDamage();
    }

    /**
     * Gets the health of the card.
     * @return the health of the card
     */
    public int getHealth() {
        return getCardInput().getHealth();
    }

    /**
     * Gets the description of the card.
     * @return the description of the card
     */
    public String getDescription() {
        return getCardInput().getDescription();
    }

    /**
     * Gets the name of the card.
     * @return the name of the card
     */
    public String getName() {
        return getCardInput().getName();
    }

    /**
     * Gets the colors of the card.
     * @return the colors of the card
     */
    public ArrayList<String> getColors() {
        return getCardInput().getColors();
    }

    /**
     * Sets the attack damage of the card.
     * @param attackDamage the attack damage of the card
     */
    public void setAttackDamage(final int attackDamage) {
        getCardInput().setAttackDamage(attackDamage);
    }

    /**
     * Sets the health of the card.
     * @param health the health of the card
     */
    public void setHealth(final int health) {
        getCardInput().setHealth(health);
    }

    /**
     * Sets the description of the card.
     * @param description the description of the card
     */
    public void setDescription(final String description) {
        getCardInput().setDescription(description);
    }

    /**
     * Sets the name of the card.
     * @param name the name of the card
     */
    public void setName(final String name) {
        getCardInput().setName(name);
    }

    /**
     * Sets the colors of the card.
     * @param colors the colors of the card
     */
    public void setColors(final ArrayList<String> colors) {
        getCardInput().setColors(colors);
    }

    /**
     * Sets the mana cost of the card.
     * @param mana the mana cost of the card
     */
    public void setMana(final int mana) {
        getCardInput().setMana(mana);
    }
}

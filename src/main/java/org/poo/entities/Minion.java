package org.poo.entities;


import org.poo.fileio.CardInput;
import java.util.ArrayList;
import java.util.Objects;

public class Minion extends Card {
    private boolean isFrozen;
    private boolean hasAttacked;

    public Minion() {
        super();
    }

    public Minion(final CardInput cardInput) {
        super(cardInput);
    }

    public Minion(final Minion minion) {
        super(minion.getCardInput());
        this.isFrozen = minion.isFrozen();
        this.hasAttacked = minion.hasAttacked();
    }

    /**
     * Gets if the card is frozen.
     * @return if the card is frozen
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Sets if the card is frozen.
     * @param frozen if the card is frozen
     */
    public void setFrozen(final boolean frozen) {
        isFrozen = frozen;
    }

    /**
     * Gets if the card has attacked.
     * @return if the card has attacked
     */
    public boolean hasAttacked() {
        return hasAttacked;
    }

    /**
     * Sets if the card has attacked.
     * @param hasAttacked if the card has attacked
     */
    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    /**
     * Gets the card's mana cost.
     * @return the card's mana cost
     */
    public int getMana() {
        return getCardInput().getMana();
    }

    /**
     * Gets the card's attack damage.
     * @return the card's attack damage
     */
    public int getAttackDamage() {
        return getCardInput().getAttackDamage();
    }

    /**
     * Gets the card's health.
     * @return the card's health
     */
    public int getHealth() {
        return getCardInput().getHealth();
    }

    /**
     * Gets the card's description.
     * @return the card's description
     */
    public String getDescription() {
        return getCardInput().getDescription();
    }

    /**
     * Gets the card's name.
     * @return the card's name
     */
    public String getName() {
        return getCardInput().getName();
    }

    /**
     * Gets the card's colors.
     * @return the card's colors
     */
    public ArrayList<String> getColors() {
        return getCardInput().getColors();
    }

    /**
     * Sets the card's mana cost.
     * @param mana the card's mana cost
     */
    public void setMana(final int mana) {
        getCardInput().setMana(mana);
    }

    /**
     * Sets the card's attack damage.
     * @param attackDamage the card's attack damage
     */
    public void setAttackDamage(final int attackDamage) {
        getCardInput().setAttackDamage(attackDamage);
    }

    /**
     * Sets the card's health.
     * @param health the card's health
     */
    public void setHealth(final int health) {
        getCardInput().setHealth(health);
    }

    /**
     * Sets the card's description.
     * @param description the card's description
     */
    public void setDescription(final String description) {
        getCardInput().setDescription(description);
    }

    /**
     * Sets the card's name.
     * @param name the card's name
     */
    public void setName(final String name) {
        getCardInput().setName(name);
    }

    /**
     * Sets the card's colors.
     * @param colors the card's colors
     */
    public void setColors(final ArrayList<String> colors) {
        getCardInput().setColors(colors);
    }

    /**
     * Checks if a given card is a tank.
     * @return if the card is a tank
     */
    public boolean isTank() {
        return Objects.equals(this.getName(), "Goliath")
                || Objects.equals(this.getName(), "Warden");
    }

}

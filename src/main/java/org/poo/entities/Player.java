package org.poo.entities;

import org.poo.fileio.CardInput;
import org.poo.fileio.GameInput;
import org.poo.fileio.Input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.poo.utils.Constants.HERO_HEALTH;


public final class Player {
    private ArrayList<Minion> playerHand;
    private ArrayList<Minion> playerDeck;
    private Hero playerHero;
    private int mana;

    public Player() {
        this.playerHand = new ArrayList<>();
        this.playerDeck = new ArrayList<>();
        this.playerHero = new Hero();
        this.mana = 0;
    }

    /**
     * Gets if the player has a hero cooldown.
     * @return if the player has a hero cooldown
     */
    public boolean isHeroCooldown() {
        return this.playerHero.hasAttacked();
    }

    /**
     * Sets the hero cooldown.
     * @param heroCooldown if the hero has a cooldown
     */
    public void setHeroCooldown(final boolean heroCooldown) {
        this.playerHero.setHasAttacked(heroCooldown);
    }

    public ArrayList<Minion> getPlayerHand() {
        return playerHand;
    }

    public ArrayList<Minion> getPlayerDeck() {
        return playerDeck;
    }

    public Hero getPlayerHero() {
        return playerHero;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }


    /**
     * Removes a card from the player's hand.
     * @param card the card to be removed
     */
    public void removeCardFromHand(final Minion card) {
        this.playerHand.remove(card);
    }

    /**
     * Initializes the player's information.
     * @param input the input data object
     * @param game the current game
     * @param playerIdx the player's index
     * @param seed the game seed
     */
    public void playerStart(final Input input, final GameInput game, final int playerIdx,
                            final int seed) {
        // gets the player's chosen deck
        if (playerIdx == 1) {
            int deckNumber = game.getStartGame().getPlayerOneDeckIdx();

            ArrayList<Minion> deck = new ArrayList<>();
            for (CardInput card : input.getPlayerOneDecks().getDecks().get(deckNumber)) {
                deck.add(new Minion(card));
            }
            this.playerDeck = deck;
        } else {
            int deckNumber = game.getStartGame().getPlayerTwoDeckIdx();

            ArrayList<Minion> deck = new ArrayList<>();
            for (CardInput card : input.getPlayerTwoDecks().getDecks().get(deckNumber)) {
                deck.add(new Minion(card));
            }
            this.playerDeck = deck;
        }

        // shuffles the player's deck
        Random random = new Random(seed);
        Collections.shuffle(this.playerDeck, random);

        // sets up the player's hero
        if (playerIdx == 1) {
            this.playerHero = new Hero(game.getStartGame().getPlayerOneHero());
        } else {
            this.playerHero = new Hero(game.getStartGame().getPlayerTwoHero());
        }

        // sets up the hero's health
        this.playerHero.setHealth(HERO_HEALTH);
    }

}

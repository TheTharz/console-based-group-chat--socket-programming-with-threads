package org.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private BufferedReader reader;

    public Player(String name){
        this.name = name;
        hand = new ArrayList<>();
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void addToDeck(Card card){
        hand.add(card);
    }

    public void removeFromDeck(Card card){
        hand.remove(card);
    }

    public boolean hasCard(Card card){
        return hand.contains(card);
    }

    public String getName(){
        return name;
    }

    public Card playCard() {
        System.out.println(name + ", Rank:");
        Rank rank = null;
        Suit suit = null;
        try {
            String rankInput = reader.readLine().toUpperCase();
            rank = Rank.valueOf(rankInput);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Invalid input. Please enter a valid rank.");
            return playCard();
        }

        System.out.println("Suit");
        try {
            String suitInput = reader.readLine().toUpperCase();
            suit = Suit.valueOf(suitInput);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Invalid input. Please enter a valid suit.");
            return playCard();
        }

        Card selectedCard = new Card(rank, suit);
        System.out.println(selectedCard);
        if (hand.contains(selectedCard)) {
            printHand();
            System.out.println("You don't have that card. Please select a card from your hand.");
            return playCard();
        }

        hand.remove(selectedCard);
        return selectedCard;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void printHand() {
        System.out.println(name+" Your hand:");
        for (Card card : hand) {
            System.out.println(card);
        }
    }
}

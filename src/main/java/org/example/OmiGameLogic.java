package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OmiGameLogic {
    private Team team1;
    private Team team2;
    private Deck deck;
    private int roundNumber;
    private int dealerIndex;
    private List<Card> currentTrick;
    private Suit trumps;

    // window
    int windowWidth = 800;
    int windowHeight = 600;

    int cardWidth = 110; //ratio should 1/1.4
    int cardHeight = 154;

    JFrame frame = new JFrame("Omi Game");
    JPanel gamePanel = new JPanel(){
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            try {
                //draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("/cards/BACK.png")).getImage();
                if (!stayButton.isEnabled()) {
                 //   hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);


            } catch (Exception e) {
                e.printStackTrace(); // Print the exception details for debugging
            }


        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hintButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");

    public OmiGameLogic() {
        initializeGame();
        frame.setVisible(true);
        frame.setSize(windowWidth, windowHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        frame.add(gamePanel);

        hintButton.setFocusable(false);
        buttonPanel.add(hintButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel , BorderLayout.SOUTH);
    }

    private void initializeGame() {
        deck = new Deck();
        deck.shuffle();

        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Player player3 = new Player("Player 3");
        Player player4 = new Player("Player 4");

        team1 = new Team("Team A", player1, player2);
        team2 = new Team("Team B", player3, player4);

        dealerIndex = 0; // Assume player1 is the dealer initially
        roundNumber = 1;
        currentTrick = new ArrayList<>();
    }

    private void dealCards() {
        List<Card> cards = deck.getCards();
        int startIndex = dealerIndex == 0 ? 0 : 1; // Skip the dealer for cutting
        int index = startIndex;

        for (int i = 0; i < 4; i++) {
            team1.getPlayer1().addToDeck(cards.get(index++));
            team1.getPlayer2().addToDeck(cards.get(index++));
            team2.getPlayer1().addToDeck(cards.get(index++));
            team2.getPlayer2().addToDeck(cards.get(index++));
        }

        dealerIndex = (dealerIndex + 1) % 4;
        System.out.println("Dealer: " + getCurrentDealer().getName());
        System.out.println("Deck cut by: " + getCurrentLeftPlayer().getName());
        System.out.println("Cards dealt.");
    }

    private void nameTrumps() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Player to the right of the dealer, please name trumps (CLUBS, DIAMONDS, HEARTS, SPADES):");

        try {
            String trumpInput = scanner.nextLine().toUpperCase();
            trumps = Suit.valueOf(trumpInput);

            System.out.println("Trumps named: " + trumps);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input. Please enter a valid suit.");
            nameTrumps();
        }
    }

    private Player getCurrentDealer() {
        switch (dealerIndex) {
            case 0:
                return team1.getPlayer1();
            case 1:
                return team1.getPlayer2();
            case 2:
                return team2.getPlayer1();
            default:
                return team2.getPlayer2();
        }
    }

    private Player getCurrentLeftPlayer() {
        int leftIndex = (dealerIndex + 1) % 4;
        switch (leftIndex) {
            case 0:
                return team1.getPlayer1();
            case 1:
                return team1.getPlayer2();
            case 2:
                return team2.getPlayer1();
            default:
                return team2.getPlayer2();
        }
    }

    private boolean isValidPlay(Player player, Card card) {
        if (currentTrick.isEmpty()) {
            return true; // First card in the trick, any card is valid
        }

        Suit leadSuit = currentTrick.get(0).getSuit();
        if (card.getSuit() == leadSuit) {
            return true; // Following suit
        }

        // Not following suit, check if the player has the lead suit
        for (Card c : player.getHand()) {
            if (c.getSuit() == leadSuit) {
                return false; // Player has a card of the lead suit, must play that card
            }
        }

        return true; // Player doesn't have the lead suit, any card is valid
    }

    private void playTrick() {
        currentTrick.clear(); // Clear the current trick
        Player currentPlayer = getCurrentDealer(); // Start with the player to the right of the dealer

        while (currentTrick.size() < 4) {
            System.out.println(currentPlayer.getName() + "'s turn.");
            System.out.println("Your hand:");
            currentPlayer.getHand().forEach(System.out::println);

            Card cardPlayed = currentPlayer.playCard();
            if (!isValidPlay(currentPlayer, cardPlayed)) {
                System.out.println("Invalid play. Please follow suit if possible.");
                continue;
            }

            currentTrick.add(cardPlayed);
            System.out.println(currentPlayer.getName() + " played: " + cardPlayed);
            currentPlayer = getNextPlayer(currentPlayer);
        }

        determineTrickWinner();
    }

    private Player getNextPlayer(Player currentPlayer) {
        if (currentPlayer == team1.getPlayer1()) {
            return team1.getPlayer2();
        } else if (currentPlayer == team1.getPlayer2()) {
            return team2.getPlayer1();
        } else if (currentPlayer == team2.getPlayer1()) {
            return team2.getPlayer2();
        } else {
            return team1.getPlayer1();
        }
    }

    private void determineTrickWinner() {
        Suit leadSuit = currentTrick.get(0).getSuit();
        Card highestTrump = null;
        Card highestOfLeadSuit = null;

        for (Card card : currentTrick) {
            if (card.getSuit() == trumps) {
                if (highestTrump == null || card.getRank().ordinal() > highestTrump.getRank().ordinal()) {
                    highestTrump = card;
                }
            } else if (card.getSuit() == leadSuit) {
                if (highestOfLeadSuit == null || card.getRank().ordinal() > highestOfLeadSuit.getRank().ordinal()) {
                    highestOfLeadSuit = card;
                }
            }
        }

        Player trickWinner = null;
        if (highestTrump != null) {
            trickWinner = getPlayerByCard(highestTrump);
        } else if (highestOfLeadSuit != null) {
            trickWinner = getPlayerByCard(highestOfLeadSuit);
        }

        if (trickWinner != null) {
            System.out.println("Trick won by " + trickWinner.getName());

        } else {
            System.out.println("Trick tied. No winner.");
        }

        // Clear the current trick for the next round
        currentTrick.clear();
    }
    private Player getPlayerByCard(Card card) {
        if (team1.getPlayer1().hasCard(card)) {
            return team1.getPlayer1();
        } else if (team1.getPlayer2().hasCard(card)) {
            return team1.getPlayer2();
        } else if (team2.getPlayer1().hasCard(card)) {
            return team2.getPlayer1();
        } else if (team2.getPlayer2().hasCard(card)) {
            return team2.getPlayer2();
        }

        return null; // Card not found in any player's hand
    }

    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to OMI Game!");

        while (roundNumber <= 10) {  // Play 10 rounds
            System.out.println("\nRound " + roundNumber);

            if (roundNumber == 1) {
                deck.shuffle();
                dealCards();
                nameTrumps();
            }

            System.out.println("Press enter to play round " + roundNumber);
            scanner.nextLine();
            playTrick();

            roundNumber++;
        }
    }

    // New method to process input
    public void processInput(String input) {
        if (input.equals("playGame")) {
            playGame();
        } else {
            System.out.println("Invalid input. Please enter 'playGame' to start the game.");
        }

    }
}

package entities;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> deck = new ArrayList<>();
    private static final Suit trump = Suit.values()[(int)(Math.random()*4)];

    public Deck(int capacity) {
        if (capacity == 36) {
            for (int i = 0; i < 4; i++)
                for (int j = Value.SIX.ordinal(); j < Value.ACE.ordinal() + 1; j++) {
                    deck.add(new Card(Value.values()[j], Suit.values()[i]));
                }
        } else {
            for (int i = 0; i < 4; i++)
                for (int j = Value.TWO.ordinal(); j < Value.ACE.ordinal() + 1; j++) {
                    deck.add(new Card(Value.values()[j], Suit.values()[i]));
                }
        }
    }

    public static Suit getTrump() {
        return trump;
    }

    public int getSize(){
        return deck.size();
    }

    public Card giveCard(){
        if (deck.size() != 0)
            return deck.remove((int)(Math.random() * deck.size()));
        else
            return null;
    }
}
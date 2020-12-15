package entities;

public class Card implements Comparable<Card> {
    private Value value;
    private Suit suit;

    Card(Value value, Suit suit){
        this.value = value;
        this.suit = suit;
    }

    Value getValue(){
        return value;
    }

    public String toString(){
        return value + "_" + suit;
    }

    public int compareTo(Card card){
        if ((card.suit == suit && card.value.ordinal() < value.ordinal()) || (suit == Deck.getTrump() && card.suit != Deck.getTrump()))
            return 1;
        else return -1;
    }
}

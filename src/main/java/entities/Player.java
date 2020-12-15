package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Бот
 */
public class Player {
    private String name;
    private List<Card> hand = new ArrayList<>();
    private boolean isRealHuman;

    public Player(String name,boolean isRealHuman) {
        this.name = name;
        this.isRealHuman = isRealHuman;
    }

    public List<Card> getHand(){
        return hand;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfCards(){
        return hand.size();
    }

    public void addToHand(Card card){
        if (card != null)
            hand.add(card);
    }

    public Card defend(Card card){
        Card defendCard = null;
        for (Card value : hand)
            if (value.compareTo(card) > 0)
                defendCard = value;
        hand.remove(defendCard);
        return defendCard;
    }

    public Card attack(List<Card> attackCards, List<Card> defendCards){
        List<Card> all = new ArrayList<>();
        all.addAll(attackCards);
        all.addAll(defendCards);
        Card attackCard = null;
        if (attackCards.size() == 0)
            attackCard = defineTheLowestCard();
        else {
            for (Card card : all)
                for (Card value : hand) {
                    if (card.getValue().ordinal() < Value.JACK.ordinal() && card.getValue() == value.getValue()) {
                        attackCard = value;
                        break;
                    }
                }
        }
        hand.remove(attackCard);
        return attackCard;
    }

    public void take(List<Card> table){
        hand.addAll(table);
    }

    private Card defineTheLowestCard(){
        Card theLowest = hand.get(0);
        for (int i = 1; i < hand.size(); i++) {
            if (theLowest.compareTo(hand.get(i)) > 0)
                theLowest = hand.get(i);
        }
        return theLowest;
    }
    public Card defineTheHighestCard(){
        Card theHighest = hand.get(0);
        for (int i = 1; i < hand.size(); i++){
            if (hand.get(i).compareTo(theHighest) > 0)
                theHighest = hand.get(i);
        }
        return theHighest;
    }
}

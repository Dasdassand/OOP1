package entities;

import java.util.List;

public class Human extends Player {

    public Human(String name, boolean isRealHuman){
        super(name, isRealHuman);
    }

    public boolean checkNumberForAttack(int number, List<Card> attackCards, List<Card> defendCards){
        if (number >= getHand().size() || number < -1)
            return false;
        if (attackCards.size() == 0 || number == -1)
            return true;
        for (Card attackCard : attackCards)
            if (getHand().get(number).getValue() == attackCard.getValue())
                return true;
        for (Card defendCard : defendCards)
            if (getHand().get(number).getValue() == defendCard.getValue())
                return true;
        return false;
    }
    public boolean checkNumberForDefend(int number,Card attackCard){
        if (number >= getHand().size() || number < -1)
            return false;
        if (number == -1)
            return true;
        return getHand().get(number).compareTo(attackCard) > 0;
    }

    public Card getCard(int number){
        if(number == -1)
            return null;
        else
            return getHand().remove(number);
    }
}
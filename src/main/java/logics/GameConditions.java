package logics;

import entities.Card;
import entities.Deck;
import entities.Player;

import java.util.List;

public class GameConditions {
    private List<Player> activePlayers;
    private List<Player> outPlayers;
    private List<Player> attackPlayers;
    private List<Card> attackCards;
    private List<Card> defendCards;
    private Deck deck;
    private int currentHumanPosition;
    private boolean isGameOver;
    private String logs;

    GameConditions(List<Player> activePlayers,List<Player> outPlayers,List<Player> attackPlayers,List<Card> attackCards,List<Card> defendCards,Deck deck,int currentHumanPosition,boolean isGameOver,String logs){
        this.activePlayers = activePlayers;
        this.outPlayers = outPlayers;
        this.attackPlayers = attackPlayers;
        this.attackCards = attackCards;
        this.defendCards = defendCards;
        this.deck = deck;
        this.currentHumanPosition = currentHumanPosition;
        this.isGameOver = isGameOver;
        this.logs = logs;
    }

    public Deck getDeck() {
        return deck;
    }
    public List<Card> getDefendCards() {
        return defendCards;
    }
    public List<Card> getAttackCards() {
        return attackCards;
    }
    public List<Player> getOutPlayers() {
        return outPlayers;
    }
    public int getCurrentHumanPosition() {
        return currentHumanPosition;
    }
    public List<Player> getActivePlayers() {
        return activePlayers;
    }
    public List<Player> getAttackPlayers() {
        return attackPlayers;
    }
    public boolean isGameOver(){
        return isGameOver;
    }
    public String getLogs(){
        return logs;
    }
}

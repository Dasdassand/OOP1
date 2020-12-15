package logics;

import entities.Card;
import entities.Deck;
import entities.Human;
import entities.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {
    /**
     * Deck - колода
     * attackCards - атакующие карты на столе
     * defendCards - защищающиеся карты
     * activePlayers - игроки в игре
     * attackPlayers - атакующий игрок
     * outPlayers - выбывший игрок
     * defendPlayer - защищающийся игрок
     * logs - логи
     * turnsList - состояние игры на каждый ход
     * currentHumanPosition - состояние человека
     * howManyPlayersCanNotAttack - количество игроков, которым нечем ходить (атакующие игроки)
     * numberOfAttackPlayer - номер атакующего игрока
     */
    private Deck deck;
    private List<Card> attackCards = new ArrayList<>();
    private List<Card> defendCards = new ArrayList<>();
    private CircularList<Player> activePlayers = new CircularList<>();
    private CircularList<Player> attackPlayers = new CircularList<>();
    private List<Player> outPlayers = new ArrayList<>();
    private Player defendPlayer;
    private StringBuilder logs = new StringBuilder();
    private transient List<GameConditions> turnsList = new ArrayList<>();

    private int currentHumanPosition = 0;
    private int howManyPlayersCanNotAttack;
    private int numberOfAttackPlayer;
    private boolean isGameOver = false;

    /**
     * @param numberOfPlayers - общее количество игроков
     * @param numberOfCards   - колиество кард
     *                        Инициализация полей для игры
     */
    public Game(int numberOfPlayers, int numberOfCards) {
        deck = new Deck(numberOfCards);
        Human human = new Human("Вы", true);
        activePlayers.add(human);
        for (int i = 0; i < 6; i++)
            human.addToHand(deck.giveCard());
        for (int i = 1; i < numberOfPlayers; i++) {
            Player player = new Player("Игрок " + i, false);
            activePlayers.add(player);
            for (int j = 0; j < 6; j++)
                player.addToHand(deck.giveCard());
        }
        int first = whoIsFirst();
        defendPlayer = activePlayers.get(first + 1);
        attackPlayers.add(activePlayers.get(first));
        for (int i = 2; i < activePlayers.size(); i++)
            attackPlayers.add(activePlayers.get(first + i));
        writeToTurnsList();
        logs.append("Начинаем игру.\nВыбранные параметры: ").append(numberOfPlayers).append(" игрока, ").append(numberOfCards).append(" карт.");
        start();
    }

    /**
     * Инициализация и запуск главного потока
     */
    public void start() {
        Thread mainThread = createMainThread();
        mainThread.start();
    }

    /**
     *
     * @param numberOfCard - количество кард
     *  Продолжение хода
     *  Проверка на наличие потонцеальных действий
     *  Если игрокам нечем атаковать или защищаться, то завершаем ход
     */
    public synchronized void continueTurn(int numberOfCard) {
        if (currentHumanPosition == 1 && numberOfCard == -1 && attackCards.size() == 0)
            return;
        if (currentHumanPosition == 1) {
            currentHumanPosition = 0;
            if (!findHuman().checkNumberForAttack(numberOfCard, attackCards, defendCards))
                return;
            Card card = findHuman().getCard(numberOfCard);
            if (card == null) {
                howManyPlayersCanNotAttack++;
                numberOfAttackPlayer++;
            } else {
                howManyPlayersCanNotAttack = 0;
                attackCards.add(card);
                logs.append("\nВы ходите картой ").append(card.toString());
                writeToTurnsList();
                if (attackCards.size() - defendCards.size() <= 1) {
                    Card defendCard = defendPlayer.defend(card);
                    if (defendCard == null) {
                        logs.append("\nИгроку ").append(defendPlayer.getName()).append(" больше нечем отбиваться. Он заберет все карты");
                        writeToTurnsList();
                    } else {
                        defendCards.add(defendCard);
                        logs.append("\n").append(defendPlayer.getName()).append(" отбивается картой ").append(defendCard.toString());
                        writeToTurnsList();
                    }
                }
            }
        } else if (currentHumanPosition == 2) {
            currentHumanPosition = 0;
            if (!findHuman().checkNumberForDefend(numberOfCard, attackCards.get(attackCards.size() - 1)))
                return;
            Card card = findHuman().getCard(numberOfCard);
            if (card == null) {
                logs.append("\nВы заберете все карты");
                writeToTurnsList();
            } else {
                defendCards.add(card);
                logs.append("\nВы отбиваетесь картой ").append(card.toString());
                writeToTurnsList();
            }
        }
        synchronized (this) {
            notify();
        }
    }

    /**
     * Завершение хода
     */
    private synchronized void endTurn() {
        attackPlayers.clear();
        int x = activePlayers.getNumber(defendPlayer);

        for (int i = 0; i < activePlayers.size(); i++)
            if (activePlayers.get(i).getNumberOfCards() == 0 && deck.getSize() == 0) {
                outPlayers.add(activePlayers.remove(i));
                logs.append("\n").append(activePlayers.get(i).getName()).append(" выходит из игры");
                writeToTurnsList();
            }

        if (attackCards.size() == defendCards.size()) { //отбился
            attackPlayers.add(activePlayers.get(x));
            defendPlayer = activePlayers.get(x + 1);
            for (int i = 2; i < activePlayers.size(); i++)
                attackPlayers.add(activePlayers.get(x + i));
        } else { //не отбился
            defendPlayer.take(attackCards);
            defendPlayer.take(defendCards);
            attackPlayers.add(activePlayers.get(x + 1));
            defendPlayer = activePlayers.get(x + 2);
            for (int i = 3; i < activePlayers.size() + 1; i++)
                attackPlayers.add(activePlayers.get(x + i));
        }
        attackCards.clear();
        defendCards.clear();

        takeCardsFromDeck(activePlayers.get(x - 1));
        for (int i = 1; i < activePlayers.size() - 1; i++)
            takeCardsFromDeck(activePlayers.get(x + i));
        takeCardsFromDeck(activePlayers.get(x));

        howManyPlayersCanNotAttack = 0;
        numberOfAttackPlayer = 0;
        writeToTurnsList();
        logs.append("\nКонец хода");
    }

    /**
     *
     * @return Игрок, который пойдёт первым
     * Самый большой козырь
     */
    private int whoIsFirst() {
        int first = 0;
        Card theHighest = activePlayers.get(0).defineTheHighestCard();
        for (int i = 1; i < activePlayers.size(); i++) {
            Card card = activePlayers.get(i).defineTheHighestCard();
            if (card.compareTo(theHighest) > 0)
                first = i;
        }
        return first;
    }

    /**
     *
     * @param player игрок
     * Добор карт из колоды
     */
    private void takeCardsFromDeck(Player player) {
        while (player.getNumberOfCards() < 6 && deck.getSize() != 0)
            player.addToHand(deck.giveCard());
    }

    /**
     *
     * @return лист действий, который мы берём для отображения
     */
    public List<GameConditions> getTurnsList() {
        if (turnsList == null)
            turnsList = new ArrayList<>();
        if (turnsList.size() == 0)
            writeToTurnsList();
        return turnsList;
    }

    public void clearTurnsList() {
        turnsList.clear();
    }

    /**
     *
     * @return Ищем человека и определяем ходит ли он
     */
    private Human findHuman() {
        Human human = null;
        for (int i = 0; i < activePlayers.size(); i++)
            if (activePlayers.get(i) instanceof Human)
                human = (Human) activePlayers.get(i);
        return human;
    }

    /**
     * Запись в лист действий
     */
    private void writeToTurnsList() {
        if (turnsList == null) {
            turnsList = new ArrayList<>();
        }
        turnsList.add(new GameConditions(activePlayers.toList(), outPlayers, attackPlayers.toList(),
                attackCards, defendCards, deck, currentHumanPosition, isGameOver, logs.toString()));
    }

    /**
     *
     * @return основной поток
     * Логика игры
     * Начало игры
     * Завершение игры
     */
    private synchronized Thread createMainThread() {
        return new Thread(() -> {
            while (activePlayers.size() > 1) {
                while (attackCards.size() < 6 && attackCards.size() < defendCards.size() + defendPlayer.getNumberOfCards()
                        && howManyPlayersCanNotAttack < attackPlayers.size()) {
                    if (attackPlayers.get(numberOfAttackPlayer) instanceof Human) {
                        logs.append("\nВаш ход!");
                        currentHumanPosition = 1;
                        writeToTurnsList();
                        synchronized (this) {
                            try {
                                wait();
                                continue;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Thread attackPlayerThread = createPlayerThread(attackPlayers.get(numberOfAttackPlayer));
                    attackPlayerThread.start();
                    try {
                        attackPlayerThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (howManyPlayersCanNotAttack == 0 && attackCards.size() - defendCards.size() <= 1) {
                        if (defendPlayer instanceof Human) {
                            logs.append("\nОтбивайтесь!");
                            currentHumanPosition = 2;
                            writeToTurnsList();
                            synchronized (this) {
                                try {
                                    wait();
                                    continue;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Thread defendPlayerThread = createPlayerThread(defendPlayer);
                        defendPlayerThread.start();
                        try {
                            defendPlayerThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                endTurn();
            }
            outPlayers.add(activePlayers.get(0));
            isGameOver = true;
            writeToTurnsList();
        });
    }

    /**
     *
     * @param player Игрок
     * @return Поток
     *  Логика поведения ботов
     */
    private synchronized Thread createPlayerThread(Player player) {
        return new Thread(() -> {
            if (attackPlayers.contains(player)) {
                Card attackCard = player.attack(attackCards, defendCards);
                if (attackCard == null) {
                    howManyPlayersCanNotAttack++;
                    numberOfAttackPlayer++;
                    writeToTurnsList();
                } else {
                    howManyPlayersCanNotAttack = 0;
                    attackCards.add(attackCard);
                    logs.append("\n").append(attackPlayers.get(numberOfAttackPlayer).getName()).append(" ходит картой ").append(attackCard.toString());
                    writeToTurnsList();
                }
            } else {
                Card defendCard = defendPlayer.defend(attackCards.get(attackCards.size() - 1));
                if (defendCard == null) {
                    logs.append("\nИгроку ").append(defendPlayer.getName()).append(" больше нечем отбиваться. Он заберет все карты");
                    writeToTurnsList();
                } else {
                    defendCards.add(defendCard);
                    logs.append("\n").append(defendPlayer.getName()).append(" отбивается картой ").append(defendCard.toString());
                    writeToTurnsList();
                }
            }
        });
    }
}

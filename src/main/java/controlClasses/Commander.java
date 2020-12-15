package controlClasses;

import logics.Game;
import logics.GameConditions;

import java.util.List;
import java.util.Stack;

/**
 * Реализиция паттерна команда
 * Сохраняет состояние игры, для отмены или возврата действий
 */
class Commander {
    private Game game;
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();

    void newGame(int numberOfPlayers, int numberOfCards){
        game = new Game(numberOfPlayers, numberOfCards);
    }

    List<GameConditions> getContent(){
        return game.getTurnsList();
    }

    void clear(){
        game.clearTurnsList();
    }

    void continueTurn(int num){
        undoStack.push(JsonOperator.toJson(game));
        redoStack.clear();
        game.continueTurn(num);
    }

    void save(String path){
        JsonOperator.writeJson(game, path);
    }
    void load(String path){
        game = JsonOperator.readJson(path);
        game.start();
    }

    List<GameConditions> undo(){
        if (undoStack.size() != 0){
            String s = undoStack.pop();
            redoStack.push(s);
            game = JsonOperator.fromJson(s);
        }
        return game.getTurnsList();
    }

    List<GameConditions> redo(){
        if(redoStack.size() != 0){
            String s = redoStack.pop();
            undoStack.push(s);
            game = JsonOperator.fromJson(s);
        }
        return game.getTurnsList();
    }
}

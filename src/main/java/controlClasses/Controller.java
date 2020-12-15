package controlClasses;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider;

import entities.Deck;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import logics.GameConditions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для обработки действий человека
 */
public class Controller {
    @FXML private ChoiceBox<Integer> choiceCardsNumber;
    @FXML private ChoiceBox<Integer> choicePlayersNumber;

    @FXML private VBox resultsBox;

    @FXML private HBox handBox;
    @FXML private HBox opponentsBox;
    @FXML private HBox attackCardsBox;
    @FXML private HBox defendCardsBox;
    @FXML private ImageView deckImageView;
    @FXML private ImageView trumpImageView;
    @FXML private Text numberOfCardsText;
    @FXML private Pane buttonPane;
    @FXML private Text actionText;
    @FXML private Pane logsPane;
    @FXML private TextArea logs;
    @FXML private Button continueButton;
    @FXML private ArrayList<ImageView> handViewers = new ArrayList<>();

    private Commander commander = new Commander();

    @FXML private void onNewGameMenu(){
        Stage primaryStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/newGame.fxml"));
            fxmlLoader.setController(this);
            Parent root = fxmlLoader.load();
            primaryStage.setTitle("Новая игра");
            primaryStage.setScene(new Scene(root, 310, 200));
            primaryStage.setResizable(false);

            List<Integer> cardsNumbers = new ArrayList<>();
            cardsNumbers.add(36);
            cardsNumbers.add(52);
            ObservableList<Integer> cardsNumbersList = FXCollections.observableArrayList(cardsNumbers);
            choiceCardsNumber.setItems(cardsNumbersList);
            choiceCardsNumber.setValue(36);
            List<Integer> playersNumbers = new ArrayList<>();
            for (int i = 2; i < 7; i++)
                playersNumbers.add(i);
            ObservableList<Integer> playersNumbersList = FXCollections.observableArrayList(playersNumbers);
            choicePlayersNumber.setItems(playersNumbersList);
            choicePlayersNumber.setValue(2);

            primaryStage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    @FXML private void onCancelButton(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
    }

    @FXML private void startGameButtonAction(ActionEvent actionEvent) {SvgImageLoaderFactory.install(new PrimitiveDimensionProvider());


        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
        commander.newGame(choicePlayersNumber.getValue(),choiceCardsNumber.getValue());
        addContent();
        updateContent(commander.getContent());
    }

    @FXML private void onSaveGameMenu(){
        commander.save("C:\\Users\\eleme\\Desktop\\save.json");
    }
    @FXML private void onSaveAsGameMenu(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить");
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("JavaScript Object Notation (*.json)","*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null)
            commander.save(file.getAbsolutePath());
    }

    @FXML private void onLoadGameMenu(){
        SvgImageLoaderFactory.install(new PrimitiveDimensionProvider());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить");
        FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter("JavaScript Object Notation (*.json)","*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            commander.load(file.getAbsolutePath());
            clear();
            buttonPane.getChildren().clear();
            logsPane.getChildren().clear();
            addContent();
            updateContent(commander.getContent());
        }
    }

    @FXML private void onQuitGameMenu() {
        System.exit(0);
    }

    @FXML private void onRulesGameMenu() {
    }

    @FXML private void onContinueButton() {
        commander.clear();
        commander.continueTurn(-1);
        updateContent(commander.getContent());
    }

    @FXML private void addContent() {
        continueButton = new Button();
        continueButton.setPrefSize(195,31);
        buttonPane.getChildren().add(continueButton);
        continueButton.setOnAction(event -> onContinueButton());

        String s = "/images/" + Deck.getTrump() + ".svg";
        Image image = new Image(s, trumpImageView.getFitWidth(), trumpImageView.getFitHeight(), false, false);
        trumpImageView.setImage(image);
        image = new Image("/images/CARD.svg", deckImageView.getFitWidth(), deckImageView.getFitHeight(), false, false);
        deckImageView.setImage(image);

        opponentsBox.setSpacing(50);

        logs = new TextArea();
        logs.setPrefSize(logsPane.getPrefWidth(), logsPane.getPrefHeight());
        logsPane.getChildren().add(logs);
    }

    @FXML private void updateContent(List<GameConditions> gameConditions){
        //for (GameConditions gc : gameConditions) {
        GameConditions gc = gameConditions.get(gameConditions.size() - 1);
            clear();
            logs.setText(gc.getLogs());
            for (int i = 0; i < gc.getActivePlayers().get(0).getHand().size(); i++) {
                String s = "/images/" + gc.getActivePlayers().get(0).getHand().get(i).toString() + ".svg";
                Image image = new Image(s, handBox.getWidth() / 12, handBox.getHeight(), false, false);
                ImageView imageView = new ImageView();
                imageView.setImage(image);
                handBox.getChildren().add(imageView);
                handViewers.add(imageView);
                imageView.setOnMouseClicked(event -> {
                    commander.clear();
                    commander.continueTurn(handViewers.indexOf(event.getSource()));
                    updateContent(commander.getContent());
                });
            }
            for (int i = 0; i < gc.getAttackCards().size(); i++) {
                String s = "/images/" + gc.getAttackCards().get(i).toString() + ".svg";
                ImageView imageView = new ImageView();
                attackCardsBox.getChildren().add(imageView);
                Image image = new Image(s, attackCardsBox.getWidth() / 6, attackCardsBox.getHeight(), false, false);
                imageView.setImage(image);
            }
            for (int i = 0; i < gc.getDefendCards().size(); i++) {
                String s = "/images/" + gc.getDefendCards().get(i).toString() + ".svg";
                ImageView imageView = new ImageView();
                defendCardsBox.getChildren().add(imageView);
                Image image = new Image(s, defendCardsBox.getWidth() / 6, defendCardsBox.getHeight(), false, false);
                imageView.setImage(image);
            }

            if (gc.getCurrentHumanPosition() == 1) {
                actionText.setText("Ходите!");
                continueButton.setText("Бито");
            } else if (gc.getCurrentHumanPosition() == 2){
                if (gc.getAttackCards().size() - gc.getDefendCards().size() <= 1) {
                    actionText.setText("Отбивайтесь!");
                    continueButton.setText("Взять");
                } else
                    actionText.setText("Вы забираете карты");
            }

            for (int i = 1; i < gc.getActivePlayers().size(); i++) {
                VBox box = new VBox(18);
                Label playerLabel = new Label(gc.getActivePlayers().get(i).getName());
                Label cardsLabel = new Label("Карт: " + gc.getActivePlayers().get(i).getNumberOfCards());
                Label activityLabel = new Label();
                Font font = new Font("System", 18);
                playerLabel.setFont(font);
                cardsLabel.setFont(font);
                activityLabel.setFont(font);

                if (gc.getAttackPlayers().contains(gc.getActivePlayers().get(i))) {
                    if (gc.getAttackPlayers().indexOf(gc.getActivePlayers().get(i)) == 0) {
                        activityLabel.setText("Ходит");
                        activityLabel.setTextFill(Color.web("#a60711"));
                    } else {
                        activityLabel.setText("Подкидывает");
                        activityLabel.setTextFill(Color.web("#c23e06"));
                    }
                } else {
                    if (gc.getAttackCards().size() - gc.getDefendCards().size() <= 1) {
                        activityLabel.setText("Отбивается");
                        activityLabel.setTextFill(Color.web("#ede613"));
                    } else {
                        activityLabel.setText("Берёт");
                        activityLabel.setTextFill(Color.web("#ede613"));
                    }
                }
                box.getChildren().addAll(playerLabel, cardsLabel, activityLabel);
                opponentsBox.getChildren().add(box);

                numberOfCardsText.setText(Integer.toString(gc.getDeck().getSize()));
            }
            if (gc.isGameOver()){
                gameOver(gc);
                //break;
            }
        //}
    }

    @FXML private void clear(){
        handViewers.clear();
        handBox.getChildren().clear();
        opponentsBox.getChildren().clear();
        attackCardsBox.getChildren().clear();
        defendCardsBox.getChildren().clear();
    }

    @FXML private void gameOver(GameConditions gc){
        Stage primaryStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/results.fxml"));
            fxmlLoader.setController(this);
            Parent root = fxmlLoader.load();
            root.setStyle("-fx-background-color: rgba(37, 161, 64, 1);");
            primaryStage.setTitle("Результаты игры");
            primaryStage.setScene(new Scene(root, 307, 450));
            primaryStage.setResizable(false);

            resultsBox.setSpacing(25);
            for (int i = 0; i < gc.getOutPlayers().size(); i++){
                Label label = new Label(gc.getOutPlayers().get(i).getName() + " - " + (i + 1) + " место");
                label.setTextFill(Color.web("#ffdd00"));
                label.setFont(new Font("System", 18));
                resultsBox.getChildren().add(label);
            }

            primaryStage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML private void onNewGameButton(ActionEvent actionEvent) {
        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
        onNewGameMenu();
    }

    public void onUndo() {
        updateContent(commander.undo());
    }

    public void onRedo() {
        updateContent(commander.redo());
    }
}



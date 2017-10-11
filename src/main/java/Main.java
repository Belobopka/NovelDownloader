import Parsers.ParsersManager;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
//TODO Замена всех идентичных парсеров на один класс,который берет требуемые данные с JSON файла.
public class Main extends Application {
    ParsersManager parsersManager;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        GridPane grid = gridCreator();
        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        Text sceneTitle = new Text("Hello There");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        final Label labelURL = new Label("URL:");
        grid.add(labelURL, 0, 2);

        final TextField labelURLTextField = new TextField();
        labelURLTextField.setPrefColumnCount(40);
        grid.add(labelURLTextField, 1, 2);

        final Label labelDownPath = new Label("Download Path:");
        grid.add(labelDownPath, 0, 3);

        final TextField labelDownPathTextField = new TextField();
        grid.add(labelDownPathTextField, 1, 3);

        Button startButton = new Button("Download");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(startButton);
        grid.add(hbBtn, 1, 6);

        Button stopButton = new Button("Stop");

        HBox hbBtn2 = new HBox(10);
        hbBtn2.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn2.getChildren().add(stopButton);
        grid.add(hbBtn2, 0, 6);

        final Label labelFirstCh = new Label("First:");
        grid.add(labelFirstCh, 0, 4);

        final TextField labelFirstCnTextField = new TextField();
        labelFirstCnTextField.setMaxSize(50, 10);
        grid.add(labelFirstCnTextField, 1, 4);

        final Label labelLastCh = new Label("Last:");
        grid.add(labelLastCh, 0, 5);

        final TextField labelLastCnTextField = new TextField();
        labelLastCnTextField.setMaxSize(50, 10);
        grid.add(labelLastCnTextField, 1, 5);

        final MyRunner r = new MyRunner(labelURLTextField, labelDownPathTextField,
                labelFirstCnTextField, labelLastCnTextField, actionTarget);
        final ArrayList<Thread> threadArray = new ArrayList<Thread>();

        actionTarget.setFill(Color.FIREBRICK);
        primaryStage.setTitle("MangaFox_Download");
        primaryStage.show();
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {

                    if (!(threadArray.size() >= 1)) {
                        threadArray.add(new Thread(r));
                        threadArray.get(threadArray.size() - 1).setDaemon(true);
                        threadArray.get(threadArray.size() - 1).start();
                        System.out.println( threadArray.get(threadArray.size() - 1).getState());
                    } else {
                        threadArray.get(threadArray.size() - 1).interrupt();


                        threadArray.remove(threadArray.size() - 1);
                        threadArray.add(new Thread(r));
                        threadArray.get(threadArray.size() - 1).setDaemon(true);
                        threadArray.get(threadArray.size() - 1).start();
                    }
                }
        });
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if(threadArray.size() != 0) {
                    for(Thread thread :threadArray){
                        thread.interrupt();

                    }
                    threadArray.clear();
                }
            }
        });
    }

    class MyRunner implements Runnable {
        TextField labelURLTextField;
        TextField labelDownPathTextField;
        TextField labelFirstCnTextField;
        TextField labelLastCnTextField;
        Text actionTarget;

        MyRunner(TextField text1, TextField text2, TextField text3, TextField text4, Text actiontarget) {
            labelURLTextField = text1;
            labelDownPathTextField = text2;
            labelFirstCnTextField = text3;
            labelLastCnTextField = text4;
            this.actionTarget = actiontarget;
        }

        public void run() {
            parsersManager = new ParsersManager(labelURLTextField.getText(),
                    labelFirstCnTextField.getText(),
                    labelLastCnTextField.getText(),actionTarget,labelDownPathTextField.getText());
            parsersManager.runAsMain();

        }
    }
        private GridPane gridCreator() {
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
            return grid;
        }

    }

package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;


public class Popup {


    public static void display(String label, String message, String button)
    {
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle(label);


        Label label1= new Label(message);


        Button button1= new Button(button);


        button1.setOnAction(e -> popupwindow.close());



        VBox layout= new VBox(10);


        layout.getChildren().addAll(label1, button1);

        layout.setAlignment(Pos.CENTER);

        Scene scene1= new Scene(layout, 400, 250);

        popupwindow.setScene(scene1);

        popupwindow.showAndWait();

    }
}

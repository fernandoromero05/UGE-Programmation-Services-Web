// src/main/java/com/uge/ws/client/InternalRentalClient.java
package com.uge.ws.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uge.ws.common.Bike;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class InternalRentalClient extends Application {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private ListView<Bike> bikeList = new ListView<>();
    private TextField userIdField = new TextField("1");
    private TextArea noteArea = new TextArea();

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        loadBikes();

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadBikes());

        Button rentBtn = new Button("Rent");
        rentBtn.setOnAction(e -> rentSelected());

        Button returnBtn = new Button("Return");
        returnBtn.setOnAction(e -> returnSelected());

        HBox top = new HBox(10, new Label("User ID:"), userIdField, refreshBtn, rentBtn, returnBtn);
        top.setPadding(new Insets(5));
        root.setTop(top);

        root.setCenter(bikeList);
        noteArea.setPromptText("Return note...");
        root.setBottom(noteArea);

        stage.setScene(new Scene(root, 700, 400));
        stage.setTitle("EiffelBikeCorp – Internal Rental Client");
        stage.show();
    }

    private void loadBikes() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/corp/bikes"))
                    .GET().build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                List<Bike> bikes = mapper.readValue(resp.body(), new TypeReference<List<Bike>>(){});
                bikeList.getItems().setAll(bikes);
            } else {
                showError("Failed to load bikes: " + resp.statusCode());
            }
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void rentSelected() {
        Bike bike = bikeList.getSelectionModel().getSelectedItem();
        if (bike == null) { showError("Select a bike."); return; }
        long userId = Long.parseLong(userIdField.getText());
        try {
            String url = BASE_URL + "/corp/bikes/" + bike.getId() + "/rent?userId=" + userId;
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            showInfo(resp.body());
            loadBikes();
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void returnSelected() {
        Bike bike = bikeList.getSelectionModel().getSelectedItem();
        if (bike == null) { showError("Select a bike."); return; }
        String note = noteArea.getText();
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/corp/bikes/" + bike.getId() + "/return"))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(note))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            showInfo(resp.body());
            loadBikes();
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

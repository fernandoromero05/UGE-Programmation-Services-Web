package com.uge.ws.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uge.ws.common.Bike;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class InternalRentalClient extends JFrame {

    private final JTextField baseUrlField;
    private final JButton refreshButton;
    private final JTable bikesTable;
    private final DefaultTableModel tableModel;
    private final JTextArea logArea;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public InternalRentalClient() {
        super("EiffelBikeCorp – Internal Rental Client");

        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();

        // --- Top panel: base URL + refresh button ---
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        baseUrlField = new JTextField("http://localhost:8080/api");
        refreshButton = new JButton("Load bikes");

        topPanel.add(new JLabel("Server URL: "), BorderLayout.WEST);
        topPanel.add(baseUrlField, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);

        // --- Center: table with bikes ---
        String[] columns = {"ID", "Model", "Description", "Status", "Total rentals", "Base price EUR"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };
        bikesTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(bikesTable);

        // --- Bottom: log area ---
        logArea = new JTextArea(4, 40);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        // Layout main frame
        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        // Button action
        refreshButton.addActionListener(e -> loadBikes());

        // Load once at startup
        SwingUtilities.invokeLater(this::loadBikes);
    }

    private void loadBikes() {
        String baseUrl = baseUrlField.getText().trim();
        String url = baseUrl + "/corp/bikes";
        appendLog("Requesting bikes from: " + url);

        // Run HTTP call off the EDT
        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                if (response.statusCode() != 200) {
                    appendLog("ERROR: HTTP " + response.statusCode() + " – " + response.body());
                    return;
                }

                String json = response.body();
                List<Bike> bikes = objectMapper.readValue(
                        json,
                        new TypeReference<>() {}
                );

                SwingUtilities.invokeLater(() -> updateTable(bikes));
                appendLog("Loaded " + bikes.size() + " bikes.");

            } catch (IOException | InterruptedException ex) {
                appendLog("ERROR: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }

    private void updateTable(List<Bike> bikes) {
        tableModel.setRowCount(0);
        for (Bike b : bikes) {
            tableModel.addRow(new Object[]{
                    b.getId(),
                    b.getModel(),
                    b.getDescription(),
                    b.getStatus(),
                    b.getTotalRentals(),
                    b.getBasePriceEUR()
            });
        }
    }

    private void appendLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InternalRentalClient client = new InternalRentalClient();
            client.setVisible(true);
        });
    }
}

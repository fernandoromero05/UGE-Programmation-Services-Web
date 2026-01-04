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
import java.util.Map;

public class InternalRentalClient extends JFrame {

    // Shared
    private final JTextField baseUrlField;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final JTextArea logArea;

    // Internal rental tab
    private final JTextField userIdField;
    private final JTable corpBikesTable;
    private final DefaultTableModel corpTableModel;

    // Shop tab
    private final JTextField customerIdField;
    private final JTextField currencyField;
    private final JTable shopBikesTable;
    private final DefaultTableModel shopTableModel;
    private Long currentBasketId = null;

    public InternalRentalClient() {
        super("Eiffel Bike – Client");

        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();

        // Top bar: base URL
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        baseUrlField = new JTextField("http://localhost:8080/api");
        topPanel.add(new JLabel("Server URL: "), BorderLayout.WEST);
        topPanel.add(baseUrlField, BorderLayout.CENTER);

        // Log area (bottom, shared)
        logArea = new JTextArea(5, 80);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        // ------------ Internal rental tab -------------
        JPanel corpPanel = new JPanel(new BorderLayout(5, 5));

        JPanel corpTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userIdField = new JTextField("1", 5);
        JButton loadCorpBikesBtn = new JButton("Load bikes");
        JButton rentBtn = new JButton("Rent selected");
        JButton myRentalsBtn = new JButton("My rentals");
        JButton returnBtn = new JButton("Return rental");
        JButton viewNotesBtn = new JButton("View notes");

        corpTop.add(new JLabel("User ID:"));
        corpTop.add(userIdField);
        corpTop.add(loadCorpBikesBtn);
        corpTop.add(rentBtn);
        corpTop.add(myRentalsBtn);
        corpTop.add(returnBtn);
        corpTop.add(viewNotesBtn);  

        String[] corpCols = {"ID", "Model", "Description", "Status", "Total rentals", "Base price EUR"};
        corpTableModel = new DefaultTableModel(corpCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        corpBikesTable = new JTable(corpTableModel);
        JScrollPane corpTableScroll = new JScrollPane(corpBikesTable);

        corpPanel.add(corpTop, BorderLayout.NORTH);
        corpPanel.add(corpTableScroll, BorderLayout.CENTER);

        // ------------ Shop tab -------------
        JPanel shopPanel = new JPanel(new BorderLayout(5, 5));

        JPanel shopTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerIdField = new JTextField("1", 5);
        currencyField = new JTextField("EUR", 4);
        JButton loadShopBikesBtn = new JButton("Load bikes for sale");
        JButton createBasketBtn = new JButton("Create basket");
        JButton addToBasketBtn = new JButton("Add selected to basket");
        JButton viewBasketBtn = new JButton("View basket");
        JButton checkoutBtn = new JButton("Checkout");

        shopTop.add(new JLabel("Customer ID:"));
        shopTop.add(customerIdField);
        shopTop.add(new JLabel("Currency:"));
        shopTop.add(currencyField);
        shopTop.add(loadShopBikesBtn);
        shopTop.add(createBasketBtn);
        shopTop.add(addToBasketBtn);
        shopTop.add(viewBasketBtn);
        shopTop.add(checkoutBtn);

        String[] shopCols = {"ID", "Model", "Description", "Base price EUR"};
        shopTableModel = new DefaultTableModel(shopCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        shopBikesTable = new JTable(shopTableModel);
        JScrollPane shopTableScroll = new JScrollPane(shopBikesTable);

        shopPanel.add(shopTop, BorderLayout.NORTH);
        shopPanel.add(shopTableScroll, BorderLayout.CENTER);

        // Tabs add
        tabs.addTab("Internal rental", corpPanel);
        tabs.addTab("Shop", shopPanel);

        // Layout frame
        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(logScroll, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);

        // --------- Button actions --------- //

        // Internal rental
        loadCorpBikesBtn.addActionListener(e -> loadCorpBikes());
        rentBtn.addActionListener(e -> rentSelectedBike());
        myRentalsBtn.addActionListener(e -> showMyRentals());
        returnBtn.addActionListener(e -> returnRental());
        viewNotesBtn.addActionListener(e -> showBikeNotes());

        // Shop
        loadShopBikesBtn.addActionListener(e -> loadShopBikes());
        createBasketBtn.addActionListener(e -> createBasket());
        addToBasketBtn.addActionListener(e -> addSelectedBikeToBasket());
        viewBasketBtn.addActionListener(e -> viewBasket());
        checkoutBtn.addActionListener(e -> checkoutBasket());

        // Initial load
        SwingUtilities.invokeLater(this::loadCorpBikes);
    }

    // ------------------------------------------------------------------
    // Internal rental – helpers
    // ------------------------------------------------------------------

    private void loadCorpBikes() {
        String url = baseUrlField.getText().trim() + "/corp/bikes";
        appendLog("[GET] " + url);

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendGet(url);
                if (resp.statusCode() != 200) {
                    appendLog("ERROR: " + resp.statusCode() + " " + resp.body());
                    return;
                }

                // Parse generically to avoid enum / Jackson version issues
                List<Map<String, Object>> bikes = objectMapper.readValue(
                        resp.body(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                SwingUtilities.invokeLater(() -> {
                    corpTableModel.setRowCount(0);
                    for (Map<String, Object> b : bikes) {
                        Object id           = b.get("id");
                        Object model        = b.get("model");
                        Object description  = b.get("description");
                        Object status       = b.get("status");
                        Object totalRentals = b.get("totalRentals");
                        Object basePrice    = b.get("basePriceEUR");

                        corpTableModel.addRow(new Object[]{
                                id,
                                model,
                                description,
                                status,
                                totalRentals,
                                basePrice
                        });
                    }
                });

                appendLog("Loaded " + bikes.size() + " bikes.");
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    private Integer getSelectedCorpBikeId() {
        int row = corpBikesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bike in the table.");
            return null;
        }
        Object idObj = corpTableModel.getValueAt(row, 0);
        return (idObj instanceof Number) ? ((Number) idObj).intValue()
                                         : Integer.parseInt(idObj.toString());
    }

        private void showBikeNotes() {
        Integer bikeId = getSelectedCorpBikeId();
        if (bikeId == null) return;

        String url = baseUrlField.getText().trim() + "/corp/bikes/" + bikeId + "/notes";
        appendLog("[GET] " + url);

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendGet(url);
                if (resp.statusCode() != 200) {
                    appendLog("ERROR: " + resp.statusCode() + " " + resp.body());
                    return;
                }

                // JSON array of strings
                List<String> notes = objectMapper.readValue(
                        resp.body(),
                        new TypeReference<List<String>>() {}
                );

                String text;
                if (notes.isEmpty()) {
                    text = "No notes recorded for this bike.";
                } else {
                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    for (String n : notes) {
                        sb.append(i++).append(". ").append(n).append("\n");
                    }
                    text = sb.toString();
                }

                String finalText = text;
                SwingUtilities.invokeLater(() ->
                        showLargeText("Notes for bike " + bikeId, finalText)
                );
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    private void rentSelectedBike() {
        Integer bikeId = getSelectedCorpBikeId();
        if (bikeId == null) return;

        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID.");
            return;
        }
        long userId = Long.parseLong(userIdStr);

        String url = baseUrlField.getText().trim() + "/corp/rent";
        appendLog("[POST] " + url + " bikeId=" + bikeId + " userId=" + userId);

        Map<String, Object> payload = Map.of(
                "bikeId", bikeId,
                "userId", userId
        );

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendPostJson(url, payload);
                appendLog("Response (" + resp.statusCode() + "): " + resp.body());

                SwingUtilities.invokeLater(this::loadCorpBikes);
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    private void showMyRentals() {
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID.");
            return;
        }
        long userId = Long.parseLong(userIdStr);

        String url = baseUrlField.getText().trim() + "/corp/rentals/user/" + userId;
        appendLog("[GET] " + url);

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendGet(url);
                if (resp.statusCode() != 200) {
                    appendLog("ERROR: " + resp.statusCode() + " " + resp.body());
                    return;
                }

                String pretty;
                try {
                    // Pretty-print JSON so IDs and fields are easy to see
                    var tree = objectMapper.readTree(resp.body());
                    pretty = objectMapper
                            .writerWithDefaultPrettyPrinter()
                            .writeValueAsString(tree);
                } catch (Exception e) {
                    // Fallback: raw body
                    pretty = resp.body();
                }

                String finalPretty = pretty;
                SwingUtilities.invokeLater(() ->
                        showLargeText("Rentals for user " + userId, finalPretty)
                );
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    private void returnRental() {
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID first.");
            return;
        }
        long userId = Long.parseLong(userIdStr);

        // 1) Get rentals for this user
        String rentalsUrl = baseUrlField.getText().trim() + "/corp/rentals/user/" + userId;
        appendLog("[GET] " + rentalsUrl);

        new Thread(() -> {
            try {
                HttpResponse<String> rentalsResp = sendGet(rentalsUrl);
                if (rentalsResp.statusCode() != 200) {
                    appendLog("ERROR: " + rentalsResp.statusCode() + " " + rentalsResp.body());
                    return;
                }

                // rentals is a List<Map<String,Object>> (RentalDto from the server)
                List<Map<String, Object>> rentals = objectMapper.readValue(
                        rentalsResp.body(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                // Filter ACTIVE rentals
                List<Map<String, Object>> active = rentals.stream()
                        .filter(r -> "ACTIVE".equals(String.valueOf(r.get("status"))))
                        .toList();

                if (active.isEmpty()) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(
                                    this,
                                    "No active rentals for user " + userId + "."
                            )
                    );
                    return;
                }

                // Decide which rental to return
                Map<String, Object> chosen;
                if (active.size() == 1) {
                    chosen = active.get(0);
                } else {
                    // Build human-readable options
                    String[] options = new String[active.size()];
                    for (int i = 0; i < active.size(); i++) {
                        Map<String, Object> r = active.get(i);
                        options[i] = "Rental " + r.get("id")
                                + " – bike " + r.get("bikeId")
                                + " – start " + r.get("start");
                    }
                    int idx = JOptionPane.showOptionDialog(
                            this,
                            "Select rental to return:",
                            "Return rental",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    if (idx < 0) {
                        return; // cancelled
                    }
                    chosen = active.get(idx);
                }

                long rentalId = Long.parseLong(String.valueOf(chosen.get("id")));

                // Ask for note on the EDT
                final long finalRentalId = rentalId;
                SwingUtilities.invokeLater(() -> {
                    String note = JOptionPane.showInputDialog(
                            this,
                            "Condition note for rental " + finalRentalId + " (optional):"
                    );
                    if (note == null) note = "";

                    String url = baseUrlField.getText().trim() + "/corp/return";
                    appendLog("[POST] " + url + " rentalId=" + finalRentalId);

                    Map<String, Object> payload = Map.of(
                            "rentalId", finalRentalId,
                            "note", note
                    );

                    new Thread(() -> {
                        try {
                            HttpResponse<String> resp = sendPostJson(url, payload);
                            appendLog("Response (" + resp.statusCode() + "): " + resp.body());
                            SwingUtilities.invokeLater(this::loadCorpBikes);
                        } catch (Exception ex) {
                            logException(ex);
                        }
                    }).start();
                });

            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }
    // ------------------------------------------------------------------
    // Shop – helpers
    // ------------------------------------------------------------------

    private void loadShopBikes() {
        String url = baseUrlField.getText().trim() + "/shop/bikes";
        appendLog("[GET] " + url);

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendGet(url);
                if (resp.statusCode() != 200) {
                    appendLog("ERROR: " + resp.statusCode() + " " + resp.body());
                    return;
                }

                // Parse as generic maps to avoid enum/Jackson version issues
                List<Map<String, Object>> bikes = objectMapper.readValue(
                        resp.body(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                SwingUtilities.invokeLater(() -> {
                    shopTableModel.setRowCount(0);
                    for (Map<String, Object> b : bikes) {
                        Object id   = b.get("id");
                        Object model = b.get("model");
                        Object desc  = b.get("description");
                        Object price = b.get("basePriceEUR");

                        shopTableModel.addRow(new Object[]{
                                id,
                                model,
                                desc,
                                price
                        });
                    }
                });
                appendLog("Loaded " + bikes.size() + " sellable bikes.");
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    private Integer getSelectedShopBikeId() {
        int row = shopBikesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a bike in the Shop table.");
            return null;
        }
        Object idObj = shopTableModel.getValueAt(row, 0);
        return (idObj instanceof Number) ? ((Number) idObj).intValue()
                                         : Integer.parseInt(idObj.toString());
    }

    private void createBasket() {
        String customerIdStr = customerIdField.getText().trim();
        if (customerIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Customer ID first.");
            return;
        }
        long customerId = Long.parseLong(customerIdStr);

        String url = baseUrlField.getText().trim() + "/shop/baskets";
        appendLog("[POST] " + url + " customerId=" + customerId);

        Map<String, Object> payload = Map.of("customerId", customerId);

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendPostJson(url, payload);
                if (resp.statusCode() != 200 && resp.statusCode() != 201) {
                    appendLog("ERROR: " + resp.statusCode() + " " + resp.body());
                    return;
                }
                Map<String, Object> basket = objectMapper.readValue(
                        resp.body(), new TypeReference<Map<String, Object>>() {}
                );
                Object idObj = basket.get("id");
                if (idObj != null) {
                    currentBasketId = Long.parseLong(idObj.toString());
                    appendLog("Created basket with id=" + currentBasketId);
                } else {
                    appendLog("WARNING: basket id not found in response: " + resp.body());
                }
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    private void addSelectedBikeToBasket() {
        if (currentBasketId == null) {
            JOptionPane.showMessageDialog(this, "Create a basket first.");
            return;
        }
        Integer bikeId = getSelectedShopBikeId();
        if (bikeId == null) return;

        String qtyStr = JOptionPane.showInputDialog(
                this,
                "Quantity:",
                "1"
        );
        if (qtyStr == null || qtyStr.isBlank()) return;
        int qty = Integer.parseInt(qtyStr.trim());

        String url = baseUrlField.getText().trim() + "/shop/baskets/" + currentBasketId + "/items";
        appendLog("[POST] " + url + " bikeId=" + bikeId + " qty=" + qty);

        Map<String, Object> payload = Map.of(
                "bikeId", bikeId,
                "quantity", qty
        );

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendPostJson(url, payload);
                appendLog("Response (" + resp.statusCode() + "): " + resp.body());
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    private void viewBasket() {
        if (currentBasketId == null) {
            JOptionPane.showMessageDialog(this, "No basket created yet.");
            return;
        }
        String url = baseUrlField.getText().trim() + "/shop/baskets/" + currentBasketId;
        appendLog("[GET] " + url);

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendGet(url);
                if (resp.statusCode() != 200) {
                    appendLog("ERROR: " + resp.statusCode() + " " + resp.body());
                    return;
                }
                String json = resp.body();
                SwingUtilities.invokeLater(() ->
                        showLargeText("Basket " + currentBasketId, json)
                );
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    private void checkoutBasket() {
        if (currentBasketId == null) {
            JOptionPane.showMessageDialog(this, "No basket created yet.");
            return;
        }
        String currency = currencyField.getText().trim();
        if (currency.isEmpty()) currency = "EUR";

        String url = baseUrlField.getText().trim()
                + "/shop/baskets/" + currentBasketId + "/checkout?currency=" + currency;
        appendLog("[POST] " + url);

        new Thread(() -> {
            try {
                HttpResponse<String> resp = sendPostJson(url, Map.of()); // empty body
                appendLog("Checkout response (" + resp.statusCode() + "): " + resp.body());
            } catch (Exception ex) {
                logException(ex);
            }
        }).start();
    }

    // ------------------------------------------------------------------
    // HTTP utilities
    // ------------------------------------------------------------------

    private HttpResponse<String> sendGet(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostJson(String url, Object body)
            throws IOException, InterruptedException {

        String json = objectMapper.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private void showLargeText(String title, String text) {
        JTextArea area = new JTextArea(20, 60);
        area.setText(text);
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        JOptionPane.showMessageDialog(
                this,
                scroll,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void appendLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void logException(Exception ex) {
        ex.printStackTrace();
        appendLog("ERROR: " + ex.getClass().getSimpleName() + " – " + ex.getMessage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InternalRentalClient client = new InternalRentalClient();
            client.setVisible(true);
        });
    }
}

package com.example.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HelloApplication extends Application {
    private TableView<Books> bookTable = new TableView<>();
    private PieChart pieChart = new PieChart();
    private ObservableList<Books> books = FXCollections.observableArrayList();

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/ug-final";
        String username = "root";
        String password = "";
        return DriverManager.getConnection(url, username, password);
    }

    private void insertBook(int id, String title, String author, int price) {
        try (Connection connection = getConnection()) {
            String insertQuery = "INSERT INTO `bookstore` (id, title, author, price) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.setInt(4, price);
            preparedStatement.executeUpdate();

            refreshData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private ObservableList<Books> getBooks() {
        ObservableList<Books> bookList = FXCollections.observableArrayList();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM `bookstore`");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                int price = resultSet.getInt("price");
                String author = resultSet.getString("author");
                Books book = new Books(id, title, price, author);
                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }
    private void refreshData() {
        books.clear();
        books.addAll(getBooks());

        bookTable.setItems(books);
        pieChart.getData().clear();
        groupBooksByPriceInterval();
    }
    private void groupBooksByPriceInterval() {
        List<Books> books = getBooks();

        Map<String, Long> priceIntervalCount = books.stream()
                .collect(Collectors.groupingBy(
                        book -> {
                            int price = book.getPrice();
                            int interval = (price / 100) * 100;
                            return "$" + interval + " - $" + (interval + 100);
                        },
                        Collectors.counting()
                ));

        priceIntervalCount.forEach((interval, count) ->
                pieChart.getData().add(new PieChart.Data(interval + " (" + count + " books)", count))
        );
    }

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label();
        Label authorLabel = new Label();
        Label priceLabel = new Label();
        titleLabel.setText("Enter title");
        authorLabel.setText("Enter author");
        priceLabel.setText("Enter price");
        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField priceField = new TextField();
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            int id = books.size() + 1;
            String title = titleField.getText();
            String author = authorField.getText();
            int price = Integer.parseInt(priceField.getText());

            insertBook(id, title, author, price);

            refreshData();
            titleField.clear();
            authorField.clear();
            priceField.clear();
        });

        TableColumn<Books, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Books, String> titleColumn = new TableColumn<>("title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Books, Number> priceColumn = new TableColumn<>("price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        TableColumn<Books, String> authorColumn = new TableColumn<>("author");
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());

        bookTable.getColumns().addAll(idColumn, titleColumn, priceColumn, authorColumn);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                titleLabel, titleField, authorLabel, authorField, priceLabel, priceField, addButton, bookTable, pieChart
        );

        Scene scene = new Scene(layout, 900, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Books Management");
        primaryStage.show();

        refreshData();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

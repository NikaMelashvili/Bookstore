package com.example.demo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class Books {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty title;
    private final SimpleIntegerProperty price;
    private final SimpleStringProperty author;

    public Books(int id, String title, int price, String author) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.price = new SimpleIntegerProperty(price);
        this.author = new SimpleStringProperty(author);
    }

    public String getName() {
        return title.get();
    }
    public int getPrice(){
        return price.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public ObservableValue<String> nameProperty() {
        return title;
    }

    public ObservableValue<Number> priceProperty() {
        return price;
    }

    public ObservableValue<String> quantityProperty() {
        return author;
    }

    public ObservableValue<Number> idProperty() {
        return  id;
    }
}

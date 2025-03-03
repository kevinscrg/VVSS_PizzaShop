package pizzashop.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pizzashop.controller.OrdersGUIController;
import pizzashop.service.PizzaService;

import java.io.IOException;

public class OrdersGUI {
    protected int tableNumber;
    public int getTableNumber() {
        return tableNumber;
    }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    private PizzaService service;

    public void displayOrdersForm(PizzaService service) {
        if (service == null) {
            System.err.println("Error: Service cannot be null.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrdersGUIFXML.fxml"));
            VBox vBoxOrders = loader.load();

            OrdersGUIController ordersCtrl = loader.getController();
            if (ordersCtrl == null) {
                throw new IllegalStateException("Error: OrdersGUIController could not be initialized.");
            }

            ordersCtrl.setService(service, tableNumber);

            Stage stage = new Stage();
            stage.setTitle("Table " + getTableNumber() + " order form");
            stage.setResizable(false);

            // Disable X button
            stage.setOnCloseRequest(event -> event.consume());

            stage.setScene(new Scene(vBoxOrders));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Orders GUI: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
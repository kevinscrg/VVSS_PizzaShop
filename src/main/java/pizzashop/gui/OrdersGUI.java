package pizzashop.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pizzashop.controller.OrdersGUIController;
import pizzashop.service.PizzaService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrdersGUI {

    protected int tableNumber;

    private PizzaService service;

    // Mapa care ține evidența meselor deschise
    private static final Map<Integer, Stage> openTables = new HashMap<>();

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public static void removeTableFromOpenTables(int tableNumber) {
        if (openTables.containsKey(tableNumber)) {
            openTables.remove(tableNumber);
            System.out.println("Removed table " + tableNumber + " from openTables");
        }
    }


    public void displayOrdersForm(PizzaService service) {
        if (service == null) {
            System.err.println("Error: Service cannot be null.");
            return;
        }

        // Verifică dacă masa este deja deschisă
        if (openTables.containsKey(tableNumber)) {
            System.out.println("Table " + tableNumber + " already has an open order window!");
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


            openTables.put(tableNumber, stage);


            stage.setOnCloseRequest(event -> {
                openTables.remove(tableNumber);
                System.out.println("Closed window for table " + tableNumber);
            });

            stage.setScene(new Scene(vBoxOrders));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading Orders GUI: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}

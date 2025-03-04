package pizzashop.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import java.util.Calendar;

public class KitchenGUIController {
    @FXML
    private ListView kitchenOrdersList;
    @FXML
    public Button cook;
    @FXML
    public Button ready;

    public static  ObservableList<String> order = FXCollections.observableArrayList();
    private Object selectedOrder;
    private Calendar now = Calendar.getInstance();
    private String extractedTableNumberString;
    private int extractedTableNumberInteger;
    //thread for adding data to kitchenOrderList
    public  Thread addOrders = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        kitchenOrdersList.setItems(order);
                        }
                });
                try {
                    Thread.sleep(100);
                  } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    });

    public void initialize() {
        //starting thread for adding data to kitchenOrderList
        addOrders.setDaemon(true);
        addOrders.start();
        //Controller for Cook Button
        cook.setOnAction(event -> {
            selectedOrder = kitchenOrdersList.getSelectionModel().getSelectedItem();

            if (selectedOrder == null) {
                System.out.println("Error: No order selected!");
                return;
            }
            if(selectedOrder.toString().contains(" COOKING STARTED AT: ")) {
                System.out.println("Error: order already completed!");
                return;
            }

            kitchenOrdersList.getItems().remove(selectedOrder);
            kitchenOrdersList.getItems().add(selectedOrder.toString()
                     .concat(" Cooking started at: ").toUpperCase()
                     .concat(now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE)));
        });
        //Controller for Ready Button
        ready.setOnAction(event -> {
            selectedOrder = kitchenOrdersList.getSelectionModel().getSelectedItem();

            if (selectedOrder == null) {
                System.out.println("Error: No order selected!");
                return;
            }

            String orderString = selectedOrder.toString();

            if (orderString.length() < 6 || !orderString.startsWith("TABLE")) {
                System.out.println("Error: Invalid order format!");
                return;
            }

            try {
                extractedTableNumberString = orderString.substring(6,7);
                extractedTableNumberInteger = Integer.parseInt(extractedTableNumberString);
            } catch (NumberFormatException e) {
                System.out.println("Error: Table number is not a valid integer.");
                return;
            }

            kitchenOrdersList.getItems().remove(selectedOrder);

            String time = String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
            System.out.println("--------------------------");
            System.out.println("Table " + extractedTableNumberInteger + " ready at: " + time);
            System.out.println("--------------------------");

        });

    }
}
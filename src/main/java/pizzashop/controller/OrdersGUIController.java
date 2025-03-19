package pizzashop.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pizzashop.model.MenuDataModel;
import pizzashop.gui.OrdersGUI;
import pizzashop.service.PaymentAlert;
import pizzashop.service.PaymentException;
import pizzashop.service.PizzaService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrdersGUIController {
    @FXML
    protected ComboBox<Integer> orderQuantity;
    @FXML
    protected TableView orderTable;
    @FXML
    protected TableColumn tableQuantity;
    @FXML
    protected TableColumn tableMenuItem;
    @FXML
    protected TableColumn tablePrice;
    @FXML
    protected Label pizzaTypeLabel;
    @FXML
    protected Button addToOrder;
    @FXML
    protected Label orderStatus;
    @FXML
    protected Button placeOrder;
    @FXML
    protected Button orderServed;
    @FXML
    protected Button payOrder;
    @FXML
    protected Button newOrder;

    private ObservableList<String> orderList = FXCollections.observableArrayList();
    private List<Double> orderPaymentList = FXCollections.observableArrayList();
    public static double getTotalAmount() {
        return Math.round(totalAmount * 100.0) / 100.0;
    }
    public static void setTotalAmount(double totalAmount) {
        OrdersGUIController.totalAmount = totalAmount;
    }

    private PizzaService service;
    private int tableNumber;

    public ObservableList<String> observableList;
    private TableView<MenuDataModel> table = new TableView<MenuDataModel>();
    private ObservableList<MenuDataModel> menuData;// = FXCollections.observableArrayList();
    private Calendar now = Calendar.getInstance();
    private static double totalAmount = 0;

    private MenuDataModel selectedValue;
    protected final List<String> orderSummary = new ArrayList<>();

    public OrdersGUIController(){ }

    public void setService(PizzaService service, int tableNumber){
        this.service=service;
        this.tableNumber=tableNumber;
        try {
            initData();
            setTotalAmount(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void initData() throws Exception{
        menuData = FXCollections.observableArrayList(service.getMenuData());
        menuData.setAll(service.getMenuData());
        orderTable.setItems(menuData);

        //Controller for Place Order Button
        placeOrder.setOnAction(event -> {
            if (menuData == null || menuData.isEmpty()) {
                throw new RuntimeException("Error: Menu data is not available.");

            }

            List<String> validOrders = menuData.stream()
                    .filter(x -> x.getQuantity() > 0)
                    .map(menuDataModel -> menuDataModel.getQuantity() + " " + menuDataModel.getMenuItem())
                    .collect(Collectors.toList());

            if (validOrders.isEmpty()) {
                System.out.println("Error: No items selected for the order.");
                return;
            }

            String formattedOrder = "Table " + tableNumber + ": " + String.join(", ", validOrders);
            KitchenGUIController.order.add(formattedOrder);
            orderSummary.addAll(validOrders);
            System.out.println(orderSummary);
            orderStatus.setText("Order placed at: " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE));

            orderPaymentList= menuData.stream()
                    .filter(x -> x.getQuantity()>0)
                    .map(menuDataModel -> menuDataModel.getQuantity()*menuDataModel.getPrice())
                    .collect(Collectors.toList());

            setTotalAmount(orderPaymentList.stream().mapToDouble(e->e).sum() + getTotalAmount());

            menuData.forEach(x -> x.setQuantity(0));
        });

        //Controller for Order Served Button
        orderServed.setOnAction(event -> {orderStatus.setText("Served at: " + now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE));
        });

        //Controller for Pay Order Button
        payOrder.setOnAction(event -> {
            orderStatus.setText("Total amount: " + getTotalAmount());
            System.out.println("--------------------------");
            System.out.println("Table: " + tableNumber);
            System.out.println("Total: " + getTotalAmount());
            System.out.println("--------------------------");
            PaymentAlert pay = new PaymentAlert(service);
            try {
                pay.showPaymentAlert(tableNumber, getTotalAmount(), orderSummary);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public void initialize(){

        //populate table view with menuData from OrderGUI
        table.setEditable(true);
        tableMenuItem.setCellValueFactory(
                new PropertyValueFactory<MenuDataModel, String>("menuItem"));
        tablePrice.setCellValueFactory(
                new PropertyValueFactory<MenuDataModel, Double>("price"));
        tableQuantity.setCellValueFactory(
                new PropertyValueFactory<MenuDataModel, Integer>("quantity"));

        //bind pizzaTypeLabel and quantity combo box with the selection on the table view
        orderTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MenuDataModel>() {
        @Override
        public void changed(ObservableValue<? extends MenuDataModel> observable, MenuDataModel oldValue, MenuDataModel newValue) {
           pizzaTypeLabel.textProperty().bind(newValue.menuItemProperty());
           selectedValue = newValue;
              }
        });

        //Populate Combo box for Quantity
        ObservableList<Integer> quantityValues =  FXCollections.observableArrayList(0, 1, 2,3,4,5);
        orderQuantity.getItems().addAll(quantityValues);
        orderQuantity.setPromptText("Quantity");

        //Controller for Add to order Button
        addToOrder.setOnAction(event -> {
            selectedValue.setQuantity(orderQuantity.getValue());
        });

        //Controller for Exit table Button
        newOrder.setOnAction(event -> {
            Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION, "Exit table?",ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = exitAlert.showAndWait();
            if (result.get() == ButtonType.YES){

                Stage stage = (Stage) newOrder.getScene().getWindow();

                OrdersGUI.removeTableFromOpenTables(tableNumber);

                stage.close();
                }
        });
    }
}
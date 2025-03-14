package pizzashop.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pizzashop.model.PaymentType;

import java.util.List;
import java.util.Optional;

public class PaymentAlert implements PaymentOperation {
    private PizzaService service;

    public PaymentAlert(PizzaService service){
        this.service=service;
    }

    @Override
    public void cardPayment() {
        System.out.println("--------------------------");
        System.out.println("Paying by card...");
        System.out.println("Please insert your card!");
        System.out.println("--------------------------");
    }
    @Override
    public void cashPayment() {
        System.out.println("--------------------------");
        System.out.println("Paying cash...");
        System.out.println("Please show the cash...!");
        System.out.println("--------------------------");
    }
    @Override
    public void cancelPayment() {
        System.out.println("--------------------------");
        System.out.println("Payment choice needed...");
        System.out.println("--------------------------");
    }
      public void showPaymentAlert(int tableNumber, double totalAmount, List<String> orderSummary) throws PaymentException {
        Alert paymentAlert = new Alert(Alert.AlertType.CONFIRMATION);
        paymentAlert.setTitle("Payment for Table "+tableNumber);
        paymentAlert.setHeaderText(String.join("\n", orderSummary) +"\n-------\n" + "Total amount: " + totalAmount);
        paymentAlert.setContentText("Please choose payment option");
        ButtonType cardPayment = new ButtonType("Pay by Card");
        ButtonType cashPayment = new ButtonType("Pay Cash");
        ButtonType cancel = new ButtonType("Cancel");
        paymentAlert.getButtonTypes().setAll(cardPayment, cashPayment, cancel);

        Optional<ButtonType> result = paymentAlert.showAndWait();
        result.ifPresent(selected -> {
            if (selected == cardPayment) {
                if(tableNumber < 1 || tableNumber > 8){
                    try {
                        throw new PaymentException("table number not valid");
                    } catch (PaymentException e) {
                        throw new RuntimeException(e);
                    }
                }
                cardPayment();
                service.addPayment(tableNumber, PaymentType.Card, totalAmount);
            } else if (selected == cashPayment) {
                if(tableNumber < 1 || tableNumber > 8){
                    try {
                        throw new PaymentException("table number not valid");
                    } catch (PaymentException e) {
                        throw new RuntimeException(e);
                    }
                }
                cashPayment();
                service.addPayment(tableNumber, PaymentType.Cash, totalAmount);
            } else {
                cancelPayment();
            }
        });
      }
}
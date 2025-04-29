package pizzashop.controller;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pizzashop.model.MenuDataModel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestPlasareComanda {

    private OrdersGUIController controller;

    @BeforeEach
    void setUp() {
        controller = new OrdersGUIController();

        controller.orderStatus = null;
        controller.orderTable = null;
        controller.tableMenuItem = null;
        controller.tablePrice = null;
        controller.tableQuantity = null;
        controller.pizzaTypeLabel = null;
        controller.orderQuantity = null;
        controller.addToOrder = null;
        controller.placeOrder = null;
        controller.orderServed = null;
        controller.payOrder = null;
        controller.newOrder = null;

        KitchenGUIController.order = FXCollections.observableArrayList();
        controller.menuData = FXCollections.observableArrayList();

        controller.setTotalAmount(0);
    }



    @AfterEach
    void tearDown() {
        KitchenGUIController.order.clear();
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("All tests finished.");
    }

    // --- TESTE ---
    @Test
    @DisplayName("ECP: Plasare comanda valida")
    @Order(1)
    void testPlaceOrderButtonWorks() {
        List<String> order = new ArrayList<>();
        order.add("2 Margherita");

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        controller.placeOrder(order, menu);

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("2 Margherita"));

    }

    @Test
    @DisplayName("ECP: Comanda fara produse selectate - No items selected for the order")
    @Order(2)
    void testPlaceOrderButtonFails() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        List<String> order = new ArrayList<>();
        order.add(0 + " Margherita");

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        controller.placeOrder(order, menu);

        System.setOut(originalOut);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: No items selected for the order."));
        assertTrue(controller.orderSummary.isEmpty());
    }

    @Test
    @DisplayName("ECP: Meniu gol - Error: Menu data is not available")
    @Order(3)
    void testEmptyMeniu() {
        List<String> order = new ArrayList<>();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.placeOrder(order, null);
        });

        assertEquals("Error: Menu data is not available.", exception.getMessage());
    }

    @Test
    @DisplayName("BVA: Cantitate minima valida (1 pizza) - Successful order placement")
    @Order(4)
    void testPlaceOrderBoundaryMinPositive() {
        List<String> order = new ArrayList<>();
        order.add("1 Margherita");

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        controller.placeOrder(order, menu);

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("1 Margherita"));

    }

    @Test
    @DisplayName("BVA: Cantitate maxima -1 acceptabila (999 pizze) - Successful large order placement")
    @Order(5)
    void testPlaceOrderBoundaryMicMaxPositive() {
        List<String> order = new ArrayList<>();
        order.add("999999999 Margherita");

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        controller.placeOrder(order, menu);

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("999999999 Margherita"));

    }

    @Test
    @DisplayName("BVA: Cantitate maxima acceptabila (1000 pizze) - Successful large order placement")
    @Order(6)
    void testPlaceOrderBoundaryMaxPositive() {
        List<String> order = new ArrayList<>();
        order.add("1000000000 Margherita");

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        controller.placeOrder(order, menu);

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("1000000000 Margherita"));

    }

    @Test
    @DisplayName("BVA: Cantitate zero - No items selected for the order error")
    @Order(7)
    void testPlaceOrderBoundaryZeroNegative() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        List<String> order = new ArrayList<>();
        order.add("0 Margherita");

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        controller.placeOrder(order, menu);

        System.setOut(originalOut);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: No items selected for the order."));
        assertTrue(controller.orderSummary.isEmpty());
    }

    @Test
    @DisplayName("BVA: Cantitate negativa (-1) - No items selected for the order error")
    @Order(8)
    void testPlaceOrderBoundaryNegativeQuantity() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        List<String> order = new ArrayList<>();
        order.add("-1 Margherita");

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        controller.placeOrder(order, menu);

        System.setOut(originalOut);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: No items selected for the order."));
        assertTrue(controller.orderSummary.isEmpty());
    }
}

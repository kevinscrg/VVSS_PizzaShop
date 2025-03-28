package pizzashop.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.*;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import pizzashop.model.MenuDataModel;
import pizzashop.service.PizzaService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestPlasareComanda extends ApplicationTest {

    private OrdersGUIController controller;
    private PizzaService mockService;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrdersGUIFXML.fxml"));
        Parent root = loader.load();


        controller = loader.getController();


        mockService = mock(PizzaService.class);
        MenuDataModel pizza = new MenuDataModel("Margherita", 0, 20.0);
        pizza.setQuantity(2);

        when(mockService.getMenuData()).thenReturn(List.of(pizza));


        controller.setService(mockService, 1);

    }

    @BeforeAll
    static void initAll() {
        System.out.println("Initialize tests...");
    }

    @BeforeEach
    void setUp() {
        controller.setTotalAmount(0);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished.");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("All tests finished.");
    }





    // ECP
    @DisplayName("ECP: Plasare comanda valida")
    @RepeatedTest(3)
    @Order(1)
    void testPlaceOrderButtonWorks() {

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);
        List<String> order = new ArrayList<>();
        order.add("2 Margherita");

        Platform.runLater(() -> controller.placeOrder(order,menu));
        WaitForAsyncUtils.waitForFxEvents();


        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("2 Margherita"));
        assertTrue(controller.orderStatus.getText().startsWith("Order placed at:"));
    }

    // ECP
    @ParameterizedTest
    @ValueSource(ints = { 0, -4 })
    @DisplayName("ECP: Comanda fara produse selectate - No items selected for the order")
    @Order(2)
    void testPlaceOrderButtonFails(int quantity){

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);
        List<String> order = new ArrayList<>();
        order.add(quantity + " Margherita");
        System.setOut(originalOut);
        System.out.printf(order.toString());
        System.setOut(new PrintStream(outContent));

        Platform.runLater(() -> controller.placeOrder(order,menu));
        WaitForAsyncUtils.waitForFxEvents();


        try {
            String output = outContent.toString().trim();
            assertTrue(output.contains("Error: No items selected for the order."));
            assertFalse(output.contains("Order placed at:"));
            assertTrue(controller.orderSummary.isEmpty());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        finally {
            System.setOut(originalOut);
        }


    }

    // ECP si BVA negativ menu
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


    //BVA - pozitiv minim
    @Test
    @DisplayName("BVA: Cantitate minima valida (1 pizza) - Successful order placement")
    @Order(4)
    void testPlaceOrderBoundaryMinPositive() {

        List<String> order = new ArrayList<>();
        order.add("1 Margherita");
        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        Platform.runLater(() -> controller.placeOrder(order, menu));
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("1 Margherita"));
        assertTrue(controller.orderStatus.getText().startsWith("Order placed at:"));

    }

    //BVA - pozitiv maxim-1
    @Test
    @DisplayName("BVA: Cantitate maxima -1 acceptabila (999 pizze) - Successful large order placement")
    @Order(5)
    void testPlaceOrderBoundaryMicMaxPositive() {
        List<String> order = new ArrayList<>();
        order.add("999999999 Margherita");
        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        Platform.runLater(() -> controller.placeOrder(order, menu));
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("999999999 Margherita"));
        assertTrue(controller.orderStatus.getText().startsWith("Order placed at:"));

    }

    //BVA - pozitiv maxim
    @Test
    @DisplayName("BVA: Cantitate maxima acceptabila (1000 pizze) - Successful large order placement")
    @Order(6)
    void testPlaceOrderBoundaryMaxPositive() {
        List<String> order = new ArrayList<>();
        order.add("1000000000 Margherita");
        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        Platform.runLater(() -> controller.placeOrder(order, menu));
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("1000000000 Margherita"));
        assertTrue(controller.orderStatus.getText().startsWith("Order placed at:"));

    }


    //BVA - negativ
    @Test
    @DisplayName("BVA: Cantitate zero - No items selected for the order error")
    @Order(1)
    void testPlaceOrderBoundaryZeroNegative() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        List<String> order = new ArrayList<>();
        order.add("0 Margherita");
        MenuDataModel menu = new MenuDataModel("Margherita", 0, 20.0);

        Platform.runLater(() -> controller.placeOrder(order,menu));
        WaitForAsyncUtils.waitForFxEvents();

        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: No items selected for the order."));
        assertTrue(controller.orderSummary.isEmpty());

        System.setOut(originalOut);
    }

    //BVA - negativ
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

        Platform.runLater(() -> controller.placeOrder(order, menu));
        WaitForAsyncUtils.waitForFxEvents();

        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: No items selected for the order."));
        assertTrue(controller.orderSummary.isEmpty());

        System.setOut(originalOut);
    }



}
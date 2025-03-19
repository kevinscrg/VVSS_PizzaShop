package pizzashop.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.*;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationTest;
import pizzashop.model.MenuDataModel;
import pizzashop.service.PizzaService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
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

        MenuDataModel pizza = new MenuDataModel("Margherita", 0, 20.0);
        pizza.setQuantity(2);

        when(mockService.getMenuData()).thenReturn(List.of(pizza));

        controller.setService(mockService, 1);

        clickOn(controller.placeOrder);
        double total = OrdersGUIController.getTotalAmount();


        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("2 Margherita"));
        assertTrue(controller.orderStatus.getText().startsWith("Order placed at:"));

        assertEquals(40.0, total, 0.01);
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

        MenuDataModel pizza = new MenuDataModel("Margherita", 0, 20.0);
        pizza.setQuantity(quantity);

        when(mockService.getMenuData()).thenReturn(List.of(pizza));

        controller.setService(mockService, 2);

        controller.placeOrder.fire();


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

    // ECP si BVA
    @Test
    @DisplayName("ECP: Meniu gol - Error: Menu data is not available")
    @Order(3)
    void testEmptyMeniu() {

        when(mockService.getMenuData()).thenReturn(List.of());
        controller.setService(mockService, 3);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.placeOrder.fire();
        });

        assertEquals("Error: Menu data is not available.", exception.getMessage());
    }


    //BVA - pozitiv minim
    @Test
    @DisplayName("BVA: Cantitate minima valida (1 pizza) - Successful order placement")
    @Order(4)
    void testPlaceOrderBoundaryMinPositive() {
        MenuDataModel pizza = new MenuDataModel("Margherita", 0, 20.0);
        pizza.setQuantity(1);  // cantitate minimă validă

        when(mockService.getMenuData()).thenReturn(List.of(pizza));
        controller.setService(mockService, 5);

        clickOn(controller.placeOrder);

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("1 Margherita"));
        assertTrue(controller.orderStatus.getText().startsWith("Order placed at:"));

        double total = OrdersGUIController.getTotalAmount();
        assertEquals(20.0, total, 0.01);
    }


    //BVA - pozitiv maxim
    @Test
    @DisplayName("BVA: Cantitate maxima acceptabila (1000 pizze) - Successful large order placement")
    @Order(5)
    void testPlaceOrderBoundaryMaxPositive() {
        MenuDataModel pizza = new MenuDataModel("Margherita", 0, 20.0);
        pizza.setQuantity(1000);  // cantitate maximă acceptabilă (presupunem 1000 ca limită superioară logică)

        when(mockService.getMenuData()).thenReturn(List.of(pizza));
        controller.setService(mockService, 6);

        clickOn(controller.placeOrder);

        assertFalse(controller.orderSummary.isEmpty());
        assertTrue(controller.orderSummary.contains("1000 Margherita"));
        assertTrue(controller.orderStatus.getText().startsWith("Order placed at:"));

        double total = OrdersGUIController.getTotalAmount();
        assertEquals(20000.0, total, 0.01);
    }


    //BVA - negativ
    @Test
    @DisplayName("BVA: Cantitate zero - No items selected for the order error")
    @Order(6)
    void testPlaceOrderBoundaryZeroNegative() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MenuDataModel pizza = new MenuDataModel("Margherita", 0, 20.0);
        pizza.setQuantity(0);  // cantitate invalidă: 0

        when(mockService.getMenuData()).thenReturn(List.of(pizza));
        controller.setService(mockService, 1);

        controller.placeOrder.fire();

        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: No items selected for the order."));
        assertTrue(controller.orderSummary.isEmpty());

        System.setOut(originalOut);
    }

    //BVA - negativ
    @Test
    @DisplayName("BVA: Cantitate negativa (-1) - No items selected for the order error")
    @Order(7)
    void testPlaceOrderBoundaryNegativeQuantity() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        MenuDataModel pizza = new MenuDataModel("Margherita", 0, 20.0);
        pizza.setQuantity(-1);  // cantitate negativă

        when(mockService.getMenuData()).thenReturn(List.of(pizza));
        controller.setService(mockService, 1);

        controller.placeOrder.fire();

        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: No items selected for the order."));
        assertTrue(controller.orderSummary.isEmpty());

        System.setOut(originalOut);
    }



}
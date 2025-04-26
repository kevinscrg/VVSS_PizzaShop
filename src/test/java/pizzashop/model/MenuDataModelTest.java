package pizzashop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MenuDataModelTest {

    @Test
    public void testConstructorAndGetters() {
        MenuDataModel menuItem = new MenuDataModel("Pizza", 2, 15.0);
        assertEquals("Pizza", menuItem.getMenuItem());
        assertEquals(2, menuItem.getQuantity());
        assertEquals(15.0, menuItem.getPrice());
    }

    @Test
    public void testSetters() {
        MenuDataModel menuItem = new MenuDataModel("Pasta", 1, 10.0);
        menuItem.setMenuItem("Burger");
        menuItem.setQuantity(3);
        menuItem.setPrice(20.0);

        assertEquals("Burger", menuItem.getMenuItem());
        assertEquals(3, menuItem.getQuantity());
        assertEquals(20.0, menuItem.getPrice());
    }
}
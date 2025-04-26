package pizzashop.repository;

import org.junit.jupiter.api.Test;
import pizzashop.model.MenuDataModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuRepositoryTest {

    // Teste UNITARE
    @Test
    public void testGetMenuItemWithValidLine() {
        MenuRepository repo = new MenuRepository();
        String line = "Pizza,15.0";
        MenuDataModel item = repo.getMenuItem(line);

        assertNotNull(item);
        assertEquals("Pizza", item.getMenuItem());
        assertEquals(15.0, item.getPrice());
    }

    @Test
    public void testGetMenuItemWithNullLine() {
        MenuRepository repo = new MenuRepository();
        assertNull(repo.getMenuItem(null));
        assertNull(repo.getMenuItem(""));
    }

    // Teste de INTEGRARE (Step 3: E + R)
    @Test
    public void integrationTestReadMenuWithMockFile() throws IOException {
        File tempFile = File.createTempFile("menu", ".txt");
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write("Pizza,15.0\nPasta,10.0");
            }

            MenuRepository repo = new MenuRepository(tempFile.getAbsolutePath());
            List<MenuDataModel> menuList = repo.getMenu();

            assertNotNull(menuList);
            assertEquals(2, menuList.size());
            assertEquals("Pizza", menuList.get(0).getMenuItem());
            assertEquals(15.0, menuList.get(0).getPrice());

        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void integrationTestReadMenuWithEmptyFile() throws IOException {
        File tempFile = File.createTempFile("menu_empty", ".txt");

        try {
            MenuRepository repo = new MenuRepository(tempFile.getAbsolutePath());
            List<MenuDataModel> menuList = repo.getMenu();

            assertNotNull(menuList);
            assertEquals(0, menuList.size());
        } finally {
            tempFile.delete();
        }
    }


}
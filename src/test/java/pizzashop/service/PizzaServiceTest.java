package pizzashop.service;

import org.junit.jupiter.api.Test;
import pizzashop.model.MenuDataModel;
import pizzashop.model.Payment;
import pizzashop.model.PaymentType;
import pizzashop.repository.MenuRepository;
import pizzashop.repository.PaymentRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PizzaServiceTest {

    // Teste UNITARE
    @Test
    public void unitTestGetMenuData() {
        MenuRepository mockMenuRepo = mock(MenuRepository.class);
        PaymentRepository mockPayRepo = mock(PaymentRepository.class);

        List<MenuDataModel> mockMenuList = Arrays.asList(
                new MenuDataModel("Pizza", 0, 15.0),
                new MenuDataModel("Pasta", 0, 10.0)
        );
        when(mockMenuRepo.getMenu()).thenReturn(mockMenuList);

        PizzaService service = new PizzaService(mockMenuRepo, mockPayRepo);

        List<MenuDataModel> result = service.getMenuData();
        assertEquals(2, result.size());
    }

    @Test
    public void unitTestGetTotalAmount() {
        MenuRepository mockMenuRepo = mock(MenuRepository.class);
        PaymentRepository mockPayRepo = mock(PaymentRepository.class);

        List<Payment> payments = Arrays.asList(
                new Payment(1, PaymentType.Card, 20.0),
                new Payment(2, PaymentType.Cash, 15.0),
                new Payment(3, PaymentType.Card, 30.0)
        );
        when(mockPayRepo.getAll()).thenReturn(payments);

        PizzaService service = new PizzaService(mockMenuRepo, mockPayRepo);
        double totalCard = service.getTotalAmount(PaymentType.Card);
        assertEquals(50.0, totalCard);
    }

    // Teste de INTEGRARE Step 2 (PizzaService + MenuRepository, mock E)
    @Test
    public void integrationTestWithMenuRepositoryMockE_OneItem() {
        MenuRepository mockMenuRepo = mock(MenuRepository.class);
        PaymentRepository mockPayRepo = mock(PaymentRepository.class);

        MenuDataModel mockMenuItem = mock(MenuDataModel.class);
        when(mockMenuItem.getMenuItem()).thenReturn("Pizza");

        when(mockMenuRepo.getMenu()).thenReturn(Arrays.asList(mockMenuItem));

        PizzaService service = new PizzaService(mockMenuRepo, mockPayRepo);
        List<MenuDataModel> result = service.getMenuData();

        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getMenuItem());
    }

    @Test
    public void integrationTestWithMenuRepositoryMockE_MultipleItems() {
        MenuRepository mockMenuRepo = mock(MenuRepository.class);
        PaymentRepository mockPayRepo = mock(PaymentRepository.class);

        MenuDataModel mockItem1 = mock(MenuDataModel.class);
        MenuDataModel mockItem2 = mock(MenuDataModel.class);
        when(mockItem1.getMenuItem()).thenReturn("Pizza");
        when(mockItem2.getMenuItem()).thenReturn("Pasta");

        when(mockMenuRepo.getMenu()).thenReturn(Arrays.asList(mockItem1, mockItem2));

        PizzaService service = new PizzaService(mockMenuRepo, mockPayRepo);
        List<MenuDataModel> result = service.getMenuData();

        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getMenuItem());
    }


    // Teste de INTEGRARE Step 3 (PizzaService + MenuRepository + MenuDataModel reale)
    @Test
    public void fullIntegrationTestE_R_S_MenuData() throws IOException {
        File tempFile = File.createTempFile("menu", ".txt");
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write("Pizza,15.0\nPasta,10.0");
            }

            MenuRepository realRepo = new MenuRepository(tempFile.getAbsolutePath());
            PaymentRepository mockPayRepo = mock(PaymentRepository.class);

            PizzaService service = new PizzaService(realRepo, mockPayRepo);
            List<MenuDataModel> menuData = service.getMenuData();

            assertEquals(2, menuData.size());
            assertEquals("Pizza", menuData.get(0).getMenuItem());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void fullIntegrationTestE_R_S_TotalAmount() {
        MenuRepository realMenuRepo = new MenuRepository("data/menu.txt");  // presupunem ca exista
        PaymentRepository mockPayRepo = mock(PaymentRepository.class);

        List<Payment> payments = Arrays.asList(
                new Payment(1, PaymentType.Card, 20.0),
                new Payment(2, PaymentType.Cash, 15.0)
        );
        when(mockPayRepo.getAll()).thenReturn(payments);

        PizzaService service = new PizzaService(realMenuRepo, mockPayRepo);

        double totalCard = service.getTotalAmount(PaymentType.Card);
        assertEquals(20.0, totalCard);
    }

}
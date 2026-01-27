package org.example.repository;

import org.example.model.Investment;
import org.example.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryRepositoryTest {

    @Test
    public void loadStateTest() {
        BinaryRepository binaryRepository = new BinaryRepository("src/test/resources/portfolioTest.ser");
        List<Investment> investments = binaryRepository.loadState();
        assertEquals(6, investments.size());
    }

    @Test
    public void loadStateEmptyTest() {
        BinaryRepository binaryRepository = new BinaryRepository("src/test/resources/portfolioEmptyTest.ser");
        List<Investment> investments = binaryRepository.loadState();
        assertEquals(0, investments.size());
    }

    @Test
    public void loadStateFakeTest() throws Exception {
        File filePath = new File("src/test/resources/portfolioFakeTest.ser");
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(new byte[]{0, 1, 2, 3, 4});
        }
        BinaryRepository repository = new BinaryRepository(filePath.getAbsolutePath());
        assertThrows(PortfolioLoadException.class, repository::loadState);
    }

    @Test
    public void saveStateTest() throws Exception {
        File filePath = new File("src/test/resources/portfolioForSaveTest.ser");
        Investment inv = Stock.builder()
                .id("ID1")
                .name("Test Stock")
                .tickerSymbol("TST")
                .shares(10)
                .currentSharePrice(100)
                .annualDividendPerShare(1)
                .build();
        List<Investment> data = List.of(inv);
        BinaryRepository repo = new BinaryRepository(filePath.getAbsolutePath());
        repo.saveState(data);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            Investment read = (Investment) in.readObject();
            assertEquals(inv.getId(), read.getId());
            assertEquals(inv.getName(), read.getName());
            assertEquals(inv.calculateCurrentValue(), read.calculateCurrentValue());
            assertEquals(inv.getProjectedAnnualReturn(), read.getProjectedAnnualReturn());
        }
    }

    @Test
    public void saveStateExceptionTest() {
        File filePath = new File("testDir");
        BinaryRepository repo = new BinaryRepository(filePath.getAbsolutePath());
        List<Investment> test = List.of();
        assertThrows(IncorrectSaveException.class, () -> repo.saveState(test));
    }
}
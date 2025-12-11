package org.example;

import org.example.model.Investment;
import org.example.model.Stock;
import org.example.repository.BinaryRepository;
import org.example.service.PortfolioService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PortfolioServiceTest {
    @Test
    void testCreateInvestmentAddsAndSorts() {
        BinaryRepository mockRepository = mock(BinaryRepository.class);
        List<Investment> initialPortfolio = new ArrayList<>();
        initialPortfolio.add(new Stock("ID321", "Name"));
        when(mockRepository.loadState()).thenReturn(initialPortfolio);
        PortfolioService service = new PortfolioService(mockRepository);
    }




}

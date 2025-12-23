package org.example;

import org.example.model.Investment;
import org.example.model.Stock;
import org.example.repository.BinaryRepository;
import org.example.service.PortfolioService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PortfolioServiceTest {
   /* @Test
    void testCreateInvestmentAddsAndSorts() {
        BinaryRepository mockRepository = mock(BinaryRepository.class);
        PortfolioService mockService = mock(PortfolioService.class);
        List<Investment> initialPortfolio = new ArrayList<>();
        mockService.createInvestment(initialPortfolio.add(Stock.builder().id("ID321").name("B").tickerSymbol("MSFT")
                .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build()));
        mockService.createInvestment(initialPortfolio.add(Stock.builder().id("ID222").name("A").tickerSymbol("MSFT")
                .shares(42).currentSharePrice(310.50).annualDividendPerShare(2.25).build());
        when(mockRepository.loadState()).thenReturn(initialPortfolio);
        assertEquals("A", initialPortfolio.getFirst().getName());
        assertEquals("B", initialPortfolio.get(1).getName());
    }

   */
}

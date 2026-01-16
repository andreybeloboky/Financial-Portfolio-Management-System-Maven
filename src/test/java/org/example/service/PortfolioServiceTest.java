package org.example.service;

import org.example.model.Bond;
import org.example.model.Investment;
import org.example.model.Stock;
import org.example.repository.BinaryRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PortfolioServiceTest {
    @Test
    public void calculateTotalPortfolioValueTestMethod() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(new ArrayList<>());
        PortfolioService service = new PortfolioService(mock);
        double totalValue = service.calculateTotalPortfolioValue();
        assertEquals(0.0, totalValue);
    }

    @Test
    public void calculateTotalProjectedAnnualReturnTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(new ArrayList<>());
        PortfolioService service = new PortfolioService(mock);
        double projectedAnnualReturn = service.calculateTotalProjectedAnnualReturn();
        assertEquals(0.0, projectedAnnualReturn);
    }

    @Test
    public void findAssetAllocationByTypeTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(new ArrayList<>());
        PortfolioService service = new PortfolioService(mock);
        Map<String, Double> allocationMap = service.findAssetAllocationByType();
        assertEquals(3, allocationMap.size());
    }

    @Test
    public void findBondsMaturingInTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(new ArrayList<>());
        PortfolioService service = new PortfolioService(mock);
        List<Investment> maturingBonds = service.findBondsMaturingIn(2023);
        assertEquals(0, maturingBonds.size());
    }

    @Test
    public void findHighestValueAssetTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Collections.singletonList(Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build()));
        PortfolioService service = new PortfolioService(mock);
        Investment highestValueAsset = service.findHighestValueAsset();
        assertEquals("Microsoft Corp.", highestValueAsset.getName());
    }

    @Test
    public void getAllInvestmentsTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Arrays.asList(Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> investments = service.getAllInvestments();
        assertEquals(2, investments.size());
    }
}

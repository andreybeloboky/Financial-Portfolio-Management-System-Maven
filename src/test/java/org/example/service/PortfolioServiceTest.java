package org.example.service;

import org.example.model.*;
import org.example.repository.BinaryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class PortfolioServiceTest {

    @Test
    public void calculateTotalPortfolioValueTestMethod() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Arrays.asList(Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build(),
                MutualFund.builder().id("ID987").name("Emerging Markets Fund").fundCode("EMF456")
                        .currentNAV(1200.75).avgAnnualDistribution(18.40).unitsHeld(0.95).build()));
        PortfolioService service = new PortfolioService(mock);
        double totalValue = service.calculateTotalPortfolioValue();
        assertEquals(29428.2125, totalValue);
    }

    @Test
    public void calculateTotalProjectedAnnualReturnTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Arrays.asList(Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        double projectedAnnualReturn = service.calculateTotalProjectedAnnualReturn();
        assertEquals(393.75, projectedAnnualReturn);
    }

    @Test
    public void findAssetAllocationByTypeTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Arrays.asList(Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build(), MutualFund.builder().id("ID987").name("Emerging Markets Fund").fundCode("EMF456")
                        .currentNAV(1200.75).avgAnnualDistribution(18.40).unitsHeld(0.95).build()));
        PortfolioService service = new PortfolioService(mock);
        Map<InvestmentType, Double> allocationMap = service.findAssetAllocationByType();
        assertEquals(3, allocationMap.size());
        assertEquals(23287.5, allocationMap.get(InvestmentType.STOCK));
        assertEquals(5000.0, allocationMap.get(InvestmentType.BOND));
        assertEquals(1140.7124999999999, allocationMap.get(InvestmentType.MUTUAL_FUND));
    }

    @Test
    public void findBondsMaturingInTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Collections.singletonList(Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> maturingBonds = service.findBondsMaturingIn(2023);
        assertEquals(0, maturingBonds.size());
    }

    @Test
    public void findBondsMaturingInTestTwo() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Arrays.asList(Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build(),
                Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2003, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> maturingBonds = service.findBondsMaturingIn(2028);
        assertEquals("Corporate Bond XYZ", maturingBonds.getFirst().getName());
    }

    @Test
    public void findHighestValueAssetTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.loadState()).thenReturn(Arrays.asList(Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
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

    @Test
    public void createInvestmentTest() {
        BinaryRepository mockRepo = mock(BinaryRepository.class);
        when(mockRepo.loadState()).thenReturn(new ArrayList<>(Arrays.asList(Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build())));
        PortfolioService service = new PortfolioService(mockRepo);
        Investment newBond = Bond.builder()
                .id("ID156")
                .name("Corporate Bond XYZZZ")
                .faceValue(5000)
                .couponRate(0.045)
                .maturityDate(LocalDate.of(2028, 6, 30))
                .build();
        service.createInvestment(newBond);
        ArgumentCaptor<List<Investment>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockRepo).saveState(captor.capture());
        List<Investment> savedList = captor.getValue();
        assertEquals(3, savedList.size());
        assertTrue(savedList.contains(newBond));
        List<Investment> sortedCopy = new ArrayList<>(savedList);
        Collections.sort(sortedCopy);
        assertEquals(sortedCopy, savedList);
        verify(mockRepo, times(1)).loadState();
    }

}

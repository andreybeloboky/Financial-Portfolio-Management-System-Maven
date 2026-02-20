package org.example.service;

import org.example.model.*;
import org.example.repository.BinaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class PortfolioServiceTest {

    @Test
    public void calculateTotalPortfolioValueTestMethod() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(Arrays.asList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build(),
                MutualFund.builder().id(987).name("Emerging Markets Fund").fundCode("EMF456")
                        .currentNAV(1200.75).avgAnnualDistribution(18.40).unitsHeld(0.95).build()));
        PortfolioService service = new PortfolioService(mock);
        double totalValue = service.calculateTotalPortfolioValue();
        assertEquals(29428.2125, totalValue);
    }
/*
    @Test
    public void calculateTotalProjectedAnnualReturnTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(Arrays.asList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        double projectedAnnualReturn = service.calculateTotalProjectedAnnualReturn();
        assertEquals(393.75, projectedAnnualReturn);
    }

    @Test
    public void findAssetAllocationByTypeTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(Arrays.asList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(12)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build(),
                MutualFund.builder().id(987).name("Emerging Markets Fund").fundCode("EMF456")
                        .currentNAV(1).avgAnnualDistribution(18.40).unitsHeld(0.95).build(),
                MutualFund.builder().id(987).name("Emerging Markets Fund").fundCode("EMF456")
                        .currentNAV(1).avgAnnualDistribution(18.40).unitsHeld(0.95).build()));
        PortfolioService service = new PortfolioService(mock);
        Map<InvestmentType, Double> allocationMap = service.findAssetAllocationByType();
        assertEquals(3, allocationMap.size());
        assertEquals(23287.5, allocationMap.get(InvestmentType.STOCK));
        assertEquals(12.0, allocationMap.get(InvestmentType.BOND));
        assertEquals(2281.4249999999997, allocationMap.get(InvestmentType.MUTUAL_FUND));
    }

    @Test
    public void findBondsMaturingInNoFoundTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(Collections.singletonList(Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> maturingBonds = service.findBondsMaturingIn(2023);
        assertEquals(0, maturingBonds.size());
    }

    @Test
    public void findBondsMaturingInTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(Arrays.asList(Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2003, 6, 30)).build(),
                Bond.builder().id(654).name("Corporate Bond XYZXYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build(),
                Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> maturingBonds = service.findBondsMaturingIn(2028);
        assertEquals("Corporate Bond XYZXYZ", maturingBonds.getFirst().getName());
        assertEquals(1, maturingBonds.size());
    }

    @Test
    public void findHighestValueAssetTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(Arrays.asList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        Investment highestValueAsset = service.findHighestValueAsset();
        assertEquals("Microsoft Corp.", highestValueAsset.getName());
    }

    @Test
    public void takeAllInvestmentsTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(Arrays.asList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> investments = service.takeAllInvestments();
        assertEquals(2, investments.size());
        assertEquals("ID654", investments.get(0).getId());
        assertEquals("Microsoft Corp.", investments.get(1).getName());
    }

    @Test
    public void createInvestmentTest() {
        BinaryRepository mockRepo = mock(BinaryRepository.class);
        when(mockRepo.load()).thenReturn(new ArrayList<>(Arrays.asList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build())));
        PortfolioService service = new PortfolioService(mockRepo);
        Investment newBond = Bond.builder()
                .id(156)
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
        verify(mockRepo, times(1)).load();
        verify(mockRepo,times(1)).saveState(captor.capture());
    }

//    @ParameterizedTest
//    @CsvSource Source( {"id", "name", "-1"}, {"id", "", "100"))
//    public void createInvestmentInvalidTest(String  id, String name, String faceValue) {
//        BinaryRepository mockRepo = mock(BinaryRepository.class);
//        PortfolioService service = new PortfolioService(mockRepo);
//        Investment invalid = Bond.builder()
//                .id(id)
//                .name(name)
//                .faceValue(Double.valueOf(faceValue))  // todo
//                .couponRate(0.03)  // todo it as a param. test
//                .maturityDate(LocalDate.of(2030, 1, 1))
//                .build();
//        assertThrows(IllegalArgumentException.class, () -> service.createInvestment(invalid));
//    }

    @Test
    public void cloneInvestmentTest() throws CloneNotSupportedException {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(new ArrayList<>(Collections.singletonList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build())));
        PortfolioService service = new PortfolioService(mock);
        service.cloneInvestment(321);
        ArgumentCaptor<List<Investment>> captor = ArgumentCaptor.forClass(List.class);
        verify(mock).saveState(captor.capture());
        List<Investment> savedList = captor.getValue();
        assertEquals(2, savedList.size());
        assertEquals(savedList.getFirst().getId(), savedList.getLast().getId());
        verify(mock).load();
        verify(mock).saveState(captor.capture());
    }

    @Test
    public void cloneInvestmentExceptionTest() {
        BinaryRepository mock = mock(BinaryRepository.class);
        when(mock.load()).thenReturn(new ArrayList<>(Collections.singletonList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build())));
        PortfolioService service = new PortfolioService(mock);
        assertThrows(NullPointerException.class, () -> service.cloneInvestment(600));
        verify(mock).loadState();
    }

 */
}

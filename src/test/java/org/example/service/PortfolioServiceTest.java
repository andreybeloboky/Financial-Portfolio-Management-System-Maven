package org.example.service;

import org.example.model.*;
import org.example.repository.JdbcInvestmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class PortfolioServiceTest {

    @Test
    public void calculateTotalPortfolioValueTestMethod() {
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
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

    @Test
    public void calculateTotalProjectedAnnualReturnTest() {
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
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
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
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
        assertEquals(1.9, allocationMap.get(InvestmentType.MUTUAL_FUND));
    }

    @Test
    public void findBondsMaturingInNoFoundTest() {
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
        when(mock.load()).thenReturn(Collections.singletonList(Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> maturingBonds = service.findBondsMaturingIn(2023);
        assertEquals(0, maturingBonds.size());
    }

    @Test
    public void findBondsMaturingInTest() {
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
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
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
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
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
        when(mock.load()).thenReturn(Arrays.asList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                        .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build(),
                Bond.builder().id(654).name("Corporate Bond XYZ").faceValue(5000)
                        .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build()));
        PortfolioService service = new PortfolioService(mock);
        List<Investment> investments = service.takeAllInvestments();
        assertEquals(2, investments.size());
        assertEquals(Integer.valueOf(321), investments.get(0).getId());
        assertEquals("Corporate Bond XYZ", investments.get(1).getName());
    }

    @Test
    public void createInvestmentTest() {
        JdbcInvestmentRepository mockRepo = mock(JdbcInvestmentRepository.class);
        PortfolioService service = new PortfolioService(mockRepo);
        Investment newBond = Bond.builder()
                .id(156)
                .name("Corporate Bond XYZZZ")
                .faceValue(5000)
                .couponRate(0.045)
                .maturityDate(LocalDate.of(2028, 6, 30))
                .build();
        service.createInvestment(newBond);
        ArgumentCaptor<Investment> captor = ArgumentCaptor.forClass(Investment.class);
        verify(mockRepo).add(captor.capture());
        Investment saved = captor.getValue();
        assertEquals(Integer.valueOf(156), saved.getId());
        assertEquals(225.0, saved.getProjectedAnnualReturn());
        verify(mockRepo, times(1)).add(captor.capture());
    }

    @ParameterizedTest
    @CsvSource(value = {"'', 1.1,1.0,2028-01-01",
            "Amazon, -1.0, 19.1,2021-01-01"})
    public void createInvestmentInvalidTest(String name, Double faceValue, Double couponRate, LocalDate maturityDate) {
        JdbcInvestmentRepository mockRepo = mock(JdbcInvestmentRepository.class);
        PortfolioService service = new PortfolioService(mockRepo);
        Investment invalid = Bond.builder()
                .name(name)
                .faceValue(faceValue)
                .couponRate(couponRate)
                .maturityDate(maturityDate)
                .build();
        assertThrows(IllegalArgumentException.class, () -> service.createInvestment(invalid));
    }

    @Test
    public void cloneInvestmentTest() throws CloneNotSupportedException {
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
        when(mock.load()).thenReturn(new ArrayList<>(Collections.singletonList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build())));
        PortfolioService service = new PortfolioService(mock);
        service.cloneInvestment(321);
        ArgumentCaptor<Investment> captor = ArgumentCaptor.forClass(Investment.class);
        verify(mock).add(captor.capture());
        Investment cloneInvestment = captor.getValue();
        assertEquals(Integer.valueOf(321), cloneInvestment.getId());
        assertEquals("Microsoft Corp.", cloneInvestment.getName());
        assertEquals(168.75, cloneInvestment.getProjectedAnnualReturn());
        verify(mock).add(captor.capture());
    }

    @Test
    public void cloneInvestmentExceptionTest() {
        JdbcInvestmentRepository mock = mock(JdbcInvestmentRepository.class);
        when(mock.load()).thenReturn(new ArrayList<>(Collections.singletonList(Stock.builder().id(321).name("Microsoft Corp.").tickerSymbol("MSFT")
                .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build())));
        PortfolioService service = new PortfolioService(mock);
        assertThrows(NullPointerException.class, () -> service.cloneInvestment(600));
        verify(mock).load();
    }
}

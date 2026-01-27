package org.example.controller;

import org.example.model.*;
import org.example.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PortfolioControllerTest {

    @Test
    public void controllerFindAssetAllocationByTypeTest() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        Map<InvestmentType, Double> fakeInvestments = new HashMap<>();
        fakeInvestments.put(InvestmentType.STOCK, 10.2);
        fakeInvestments.put(InvestmentType.BOND, 13.2);
        fakeInvestments.put(InvestmentType.MUTUAL_FUND, 11.2);
        when(portfolioService.findAssetAllocationByType()).thenReturn(fakeInvestments);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("REPORT ALLOCATION");
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).findAssetAllocationByType();
    }

    @Test
    public void controllerFindBondsMaturingInTest() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        Investment fakeInvestment = Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build();
        when(portfolioService.findBondsMaturingIn(2028)).thenReturn(Collections.singletonList(fakeInvestment));
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("REPORT YEAR");
        when(scanner.nextInt()).thenReturn(2028);
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).findBondsMaturingIn(2028);
    }

    @Test
    public void controllerFindBondsMaturingInTestNegative() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        List<Investment> emptyList = new ArrayList<>();
        when(portfolioService.findBondsMaturingIn(2026)).thenReturn(emptyList);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("REPORT YEAR");
        when(scanner.nextInt()).thenReturn(2026);
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).findBondsMaturingIn(2026);
    }

    @Test
    public void calculateTotalProjectedAnnualReturnTest() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        when(portfolioService.calculateTotalProjectedAnnualReturn()).thenReturn(1.0);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("REPORT RETURN");
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).calculateTotalProjectedAnnualReturn();
    }

    @Test
    public void findHighestValueAssetTest() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        Investment fakeInvestment = Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build();
        when(portfolioService.findHighestValueAsset()).thenReturn(fakeInvestment);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("REPORT HIGHEST");
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).findHighestValueAsset();
    }

    @Test
    public void calculateTotalPortfolioValueTest() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        when(portfolioService.calculateTotalPortfolioValue()).thenReturn(1.0);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("REPORT VALUE");
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).calculateTotalPortfolioValue();
    }

    @Test
    public void cloneInvestmentTest() throws CloneNotSupportedException {
        PortfolioService portfolioService = mock(PortfolioService.class);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("CLONE ID654");
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).cloneInvestment("ID654");
    }

    @Test
    public void cloneInvestmentExceptionTest() throws CloneNotSupportedException {
        PortfolioService portfolioService = mock(PortfolioService.class);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("CLONE ID700");
        doThrow(new CloneNotSupportedException()).when(portfolioService).cloneInvestment("ID700");
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        assertThrows(RuntimeException.class, controller::process);
    }

    @Test
    public void getAllInvestmentsTest() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        List<Investment> fakeInvestments = new ArrayList<>();
        Investment fakeBondInvestment = Bond.builder().id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30)).build();
        Investment fakeMutualFundInvestment = MutualFund.builder().id("ID987").name("Emerging Markets Fund").fundCode("EMF456")
                .currentNAV(1200.75).avgAnnualDistribution(18.40).unitsHeld(0.95).build();
        Investment fakeStockInvestment = Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25).build();
        fakeInvestments.add(fakeBondInvestment);
        fakeInvestments.add(fakeMutualFundInvestment);
        fakeInvestments.add(fakeStockInvestment);
        when(portfolioService.takeAllInvestments()).thenReturn(fakeInvestments);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn("LIST");
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).takeAllInvestments();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADD STOCK", "ADD BOND", "ADD MUTUAL_FUND"})
    public void createInvestmentTest(String command) {
        PortfolioService portfolioService = mock(PortfolioService.class);
        Scanner scanner = mock(Scanner.class);
        when(scanner.nextLine()).thenReturn(command);
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        Investment expected = switch (command) {
            case "ADD STOCK" -> Stock.builder()
                    .id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT")
                    .shares(75).currentSharePrice(310.50).annualDividendPerShare(2.25)
                    .build();
            case "ADD BOND" -> Bond.builder()
                    .id("ID654").name("Corporate Bond XYZ").faceValue(5000)
                    .couponRate(0.045).maturityDate(LocalDate.of(2028, 6, 30))
                    .build();
            case "ADD MUTUAL_FUND" -> MutualFund.builder()
                    .id("ID987").name("Emerging Markets Fund").fundCode("EMF456")
                    .currentNAV(1200.75).avgAnnualDistribution(18.40).unitsHeld(0.95)
                    .build();
            default -> throw new IllegalArgumentException("Unknown command");
        };
        verify(portfolioService).createInvestment(expected);
    }

}
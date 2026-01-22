package org.example.controller;

import org.example.model.Bond;
import org.example.model.Investment;
import org.example.model.InvestmentType;
import org.example.service.PortfolioService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;

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
        ByteArrayInputStream input = new ByteArrayInputStream("REPORT ALLOCATION\n".getBytes());
        Scanner scanner = new Scanner(input);
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
        ByteArrayInputStream input = new ByteArrayInputStream("REPORT YEAR\n2028\n".getBytes());
        Scanner scanner = new Scanner(input);
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).findBondsMaturingIn(2028);
    }

    @Test
    public void controllerFindBondsMaturingInTestNegative() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        List<Investment> emptyList = new ArrayList<>();
        when(portfolioService.findBondsMaturingIn(2026)).thenReturn(emptyList);
        ByteArrayInputStream input = new ByteArrayInputStream("REPORT YEAR\n2026\n".getBytes());
        Scanner scanner = new Scanner(input);
        PortfolioController controller = new PortfolioController(scanner, portfolioService);
        controller.process();
        verify(portfolioService, times(1)).findBondsMaturingIn(2026);
    }
}
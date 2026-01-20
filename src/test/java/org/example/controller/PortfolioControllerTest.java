package org.example.controller;

import org.example.model.Bond;
import org.example.model.Investment;
import org.example.model.Stock;
import org.example.service.PortfolioService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Scanner;

import static org.mockito.Mockito.*;

class PortfolioControllerTest {

    @Test
    public void controllerFindAssetAllocationByTypeTest() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        Investment fakeInvestment = Stock.builder().id("ID321").name("Microsoft Corp.").tickerSymbol("MSFT").shares(10).currentSharePrice(100).annualDividendPerShare(1).build();
        when(portfolioService.findHighestValueAsset()).thenReturn(fakeInvestment);
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
}
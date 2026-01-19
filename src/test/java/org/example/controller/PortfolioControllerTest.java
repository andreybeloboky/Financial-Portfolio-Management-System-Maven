package org.example.controller;

import org.example.service.PortfolioService;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioControllerTest {

    @Test
    public void test() {
        PortfolioService portfolioService = mock(PortfolioService.class);
        //PortfolioController portfolioController = new PortfolioController(portfolioService);
        /// add logic here

        verify(portfolioService, times(1)).findHighestValueAsset();
    }

}
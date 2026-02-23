package org.example.controller;

import org.example.repository.BinaryRepository;
import org.example.service.PortfolioService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        PortfolioController controller = new PortfolioController(new Scanner(System.in), new PortfolioService(new BinaryRepository()));
        controller.process();
    }
}

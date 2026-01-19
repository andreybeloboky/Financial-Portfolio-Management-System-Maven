package org.example.controller;

import org.example.model.*;
import org.example.repository.BinaryRepository;
import org.example.service.PortfolioService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        PortfolioController controller = new PortfolioController(new Scanner(System.in), new PortfolioService(new BinaryRepository()));
        controller.process();
    }
}

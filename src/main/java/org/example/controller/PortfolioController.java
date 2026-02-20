package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.exception.InvestmentCloneException;
import org.example.model.*;
import org.example.service.PortfolioService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

@AllArgsConstructor
public class PortfolioController {

    private static final String EXIT_MESSAGE = "Goodbye";
    private static final String SPLIT_COMMA = ",";
    private static final String SPLIT_WHITESPACE = " ";
    private static final String ENTER_YEAR_MESSAGE = "Enter the year which you want to get:";
    private static final String YEAR_ERROR_MESSAGE = "There is no such %s year in this list";
    private static final String INCORRECT_MESSAGE = "This %s doesn't exist.";
    private static final String VALUE = "%s value %s \n";

    private Scanner scanner;
    private PortfolioService service;

    public void process() {
        String userCommand = scanner.nextLine();
        String[] splitCommand = userCommand.split(SPLIT_WHITESPACE);
        Command command = Command.valueOf(splitCommand[0].toUpperCase());
        switch (command) {
            case ADD:
                InvestmentType investmentType = InvestmentType.valueOf(splitCommand[1].toUpperCase());
                switch (investmentType) {
                    case STOCK -> {
                        Stock newInvestment = Stock.builder().name("Microsoft Corp.").tickerSymbol("MSFT")
                                .shares(75).currentSharePrice(310.50).annualDividendPerShare(3.25).build();
                        service.createInvestment(newInvestment);
                    }
                    case BOND -> {
                        Bond newInvestment = Bond.builder().name("Corporate Bond XYZ").faceValue(5000)
                                .couponRate(1.045).maturityDate(LocalDate.of(2028, 6, 30)).build();
                        service.createInvestment(newInvestment);
                    }
                    case MUTUAL_FUND -> {
                        MutualFund newInvestment = MutualFund.builder().name("Emerging Markets Fund").fundCode("EMF456")
                                .currentNAV(1200.75).avgAnnualDistribution(18.40).unitsHeld(0.95).build();
                        service.createInvestment(newInvestment);
                    }
                }
                break;
            case LIST:
                List<Investment> allPortfolio = service.takeAllInvestments();
                for (Investment investment : allPortfolio) {
                    switch (investment) {
                        case Bond bond ->
                                System.out.println(bond.getId() + SPLIT_COMMA + bond.getName() + SPLIT_COMMA + bond.getCouponRate() + SPLIT_COMMA + bond.getFaceValue() + SPLIT_COMMA + bond.getMaturityDate());
                        case Stock stock ->
                                System.out.println(stock.getId() + SPLIT_COMMA + stock.getName() + SPLIT_COMMA + stock.getTickerSymbol() + SPLIT_COMMA + stock.getShares() + SPLIT_COMMA + stock.getCurrentSharePrice() + SPLIT_COMMA + stock.getAnnualDividendPerShare());
                        case MutualFund mutualFund ->
                                System.out.println(mutualFund.getId() + SPLIT_COMMA + mutualFund.getName() + SPLIT_COMMA + mutualFund.getFundCode() + SPLIT_COMMA + mutualFund.getCurrentNAV() + SPLIT_COMMA + mutualFund.getUnitsHeld() + SPLIT_COMMA + mutualFund.getAvgAnnualDistribution());
                        default -> throw new IllegalStateException(INCORRECT_MESSAGE.formatted(investment));
                    }
                }
                break;
            case CLONE:
                int id = scanner.nextInt();
                try {
                    service.cloneInvestment(id);
                } catch (CloneNotSupportedException e) {
                    throw new InvestmentCloneException("Cannot clone investment with id " + id, e);
                }
                break;
            case REPORT:
                CommandReport commandReport = CommandReport.valueOf(splitCommand[1].toUpperCase());
                switch (commandReport) {
                    case VALUE -> System.out.println(service.calculateTotalPortfolioValue());
                    case RETURN -> System.out.println(service.calculateTotalProjectedAnnualReturn());
                    case HIGHEST -> {
                        Investment highestValueAsset = service.findHighestValueAsset();
                        System.out.println(highestValueAsset.getName());
                    }
                    case ALLOCATION -> {
                        Map<InvestmentType, Double> assetAllocationByType = service.findAssetAllocationByType();
                        for (Map.Entry<InvestmentType, Double> entry : assetAllocationByType.entrySet()) {
                            System.out.printf(VALUE.formatted(entry.getKey(), entry.getValue()));
                        }
                    }
                    case YEAR -> {
                        System.out.println(ENTER_YEAR_MESSAGE);
                        int year = scanner.nextInt();
                        List<Investment> bondInvestment = service.findBondsMaturingIn(year);
                        if (!bondInvestment.isEmpty()) {
                            for (Investment bondIterator : bondInvestment) {
                                System.out.println(bondIterator.getId() + SPLIT_COMMA + bondIterator.getName());
                            }
                        } else {
                            System.out.printf(YEAR_ERROR_MESSAGE.formatted(year));
                        }
                    }
                }
                break;
            case EXIT:
                System.out.println(EXIT_MESSAGE);
                break;
        }
    }
}

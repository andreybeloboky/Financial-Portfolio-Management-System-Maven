package org.example.service;


import org.example.model.*;
import org.example.repository.BinaryRepository;

import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class PortfolioService {

    private static final String INCORRECT_MESSAGE = "This %s doesn't exist.";

    private final BinaryRepository repository = new BinaryRepository();

    public double calculateTotalPortfolioValue() {
        List<Investment> portfolio = getAllInvestments();
        double totalSum = 0;
        for (Investment investment : portfolio) {
            totalSum += investment.calculateCurrentValue();
        }
        return totalSum;
    }

    public double calculateTotalProjectedAnnualReturn() {
        List<Investment> portfolio = getAllInvestments();
        double totalSum = 0;
        for (Investment investment : portfolio) {
            totalSum += investment.getProjectedAnnualReturn();
        }
        return totalSum;
    }

    public Map<String, Double> findAssetAllocationByType() {
        List<Investment> portfolio = getAllInvestments();
        Map<String, Double> assetAllocationByType = new HashMap<>();
        double bondAllocation = 0;
        double stockAllocation = 0;
        double mutualFunAllocation = 0;
        for (Investment investment : portfolio) {
            switch (investment) {
                case Bond bond -> bondAllocation += bond.calculateCurrentValue();
                case Stock stock -> stockAllocation += stock.calculateCurrentValue();
                case MutualFund mutualFund -> mutualFunAllocation += mutualFund.calculateCurrentValue();
                default -> throw new IllegalStateException(INCORRECT_MESSAGE.formatted(investment));
            }
        }
        assetAllocationByType.put(InvestmentType.STOCK.toString(), stockAllocation);
        assetAllocationByType.put(InvestmentType.BOND.toString(), bondAllocation);
        assetAllocationByType.put(InvestmentType.MUTUAL_FUND.toString(), mutualFunAllocation);
        return assetAllocationByType;
    }

    public List<Investment> findBondsMaturingIn(int year) {
        List<Investment> portfolio = getAllInvestments();
        List<Investment> bonds = new LinkedList<>();
        for (Investment investment : portfolio) {
            if (requireNonNull(investment) instanceof Bond bond) {
                LocalDate date = bond.getMaturityDate();
                int yearBond = date.getYear();
                if (yearBond == year) {
                    bonds.add(investment);
                }
            }
        }
        return bonds;
    }

    public Investment findHighestValueAsset() {
        Investment investment = null;
        List<Investment> portfolio = getAllInvestments();
        double current;
        double max = 0;
        for (Investment investmentHighestValue : portfolio) {
            current = investmentHighestValue.calculateCurrentValue();
            if (current > max) {
                max = current;
                investment = investmentHighestValue;
            }
        }
        return investment;
    }

    public void createInvestment(Investment newInvestment) {
        List<Investment> portfolio = repository.loadState();
        portfolio.add(newInvestment);
        Collections.sort(portfolio);
        repository.saveState(portfolio);
    }

    public List<Investment> getAllInvestments() {
        List<Investment> portfolio = repository.loadState();
        Collections.sort(portfolio);
        return portfolio;
    }

    public void cloneInvestment(String id) throws CloneNotSupportedException {
        List<Investment> portfolio = repository.loadState();
        Investment investmentClone = null;
        for (Investment investment : portfolio) {
            if (investment.getId()
                    .equals(id)) {
                investmentClone = (Investment) investment.clone();
            }
        }
        portfolio.add(investmentClone);
        Collections.sort(portfolio);
        repository.saveState(portfolio);
    }
}

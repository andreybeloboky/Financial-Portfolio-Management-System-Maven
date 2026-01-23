package org.example.service;


import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.example.model.*;
import org.example.repository.BinaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
public class PortfolioService {

    private static final String INCORRECT_MESSAGE = "This %s doesn't exist.";
    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);

    private final BinaryRepository repository;

    public double calculateTotalPortfolioValue() {
        logger.debug("Calculating total portfolio value");
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

    public Map<InvestmentType, Double> findAssetAllocationByType() {
        List<Investment> investmentList = getAllInvestments();
        Map<InvestmentType, Double> assetAllocationByType = new HashMap<>();
        double bondAllocation = 0;
        double stockAllocation = 0;
        double mutualFunAllocation = 0;
        for (Investment investment : investmentList) {
            switch (investment) {
                case Bond bond -> bondAllocation += bond.calculateCurrentValue();
                case Stock stock -> stockAllocation += stock.calculateCurrentValue();
                case MutualFund mutualFund -> mutualFunAllocation += mutualFund.calculateCurrentValue();
                default -> {
                    logger.error("Unknown investment type: {}", investment.getClass().getName());
                    throw new IllegalStateException(INCORRECT_MESSAGE.formatted(investment));
                }
            }
        }
        assetAllocationByType.put(InvestmentType.STOCK, stockAllocation);
        assetAllocationByType.put(InvestmentType.BOND, bondAllocation);
        assetAllocationByType.put(InvestmentType.MUTUAL_FUND, mutualFunAllocation);
        return assetAllocationByType;
    }

    public List<Investment> findBondsMaturingIn(int year) {
        logger.debug("Searching for bonds maturing in {}", year);
        List<Investment> portfolio = getAllInvestments();
        List<Investment> bonds = new LinkedList<>();
        for (Investment investment : portfolio) {
            if (investment instanceof Bond bond) {
                LocalDate date = bond.getMaturityDate();
                int yearBond = date.getYear();
                if (yearBond == year) {
                    bonds.add(investment);
                }
            }
        }
        logger.debug("Found {} bonds maturing in {}", bonds.size(), year);
        return bonds;
    }

    public Investment findHighestValueAsset() {
        logger.debug("Finding highest value asset");
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
        Objects.requireNonNull(investment, "investment must not be null");
        logger.info("Highest value asset is {} with value {}",
                investment.getName(), max);
        return investment;
    }

    public void createInvestment(Investment newInvestment) {
        Validate.notBlank(newInvestment.getName(), "Name cannot be empty");
        List<Investment> portfolio = repository.loadState();
        portfolio.add(newInvestment);
        Collections.sort(portfolio);
        repository.saveState(portfolio);
        logger.info("Investment created: {}, {}", newInvestment.getId(), newInvestment.getName());
    }

    public List<Investment> getAllInvestments() {
        logger.debug("Loading all investments from repository");
        List<Investment> portfolio = repository.loadState();
        Collections.sort(portfolio);
        logger.debug("Loaded {} investments", portfolio.size());
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
        assert investmentClone != null;
        portfolio.add(investmentClone);
        Collections.sort(portfolio);
        repository.saveState(portfolio);
        logger.info("Investment cloned: {}, {}", investmentClone.getId(), investmentClone.getName());
    }
}

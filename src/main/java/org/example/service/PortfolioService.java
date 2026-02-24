package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.example.model.*;
import org.example.repository.JdbcInvestmentRepository;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Slf4j
public class PortfolioService {

    private static final String INCORRECT_MESSAGE = "This %s doesn't exist.";

    private final JdbcInvestmentRepository repository;

    public double calculateTotalPortfolioValue() {
        log.debug("Calculating total portfolio value");
        List<Investment> portfolio = findAllInvestments();
        double totalSum = 0;
        for (Investment investment : portfolio) {
            totalSum += investment.calculateCurrentValue();
        }
        return totalSum;
    }

    public double calculateTotalProjectedAnnualReturn() {
        List<Investment> portfolio = findAllInvestments();
        double totalSum = 0;
        for (Investment investment : portfolio) {
            totalSum += investment.getProjectedAnnualReturn();
        }
        return totalSum;
    }

    public Map<InvestmentType, Double> findAssetAllocationByType() {
        List<Investment> investmentList = findAllInvestments();
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
                    log.error("Unknown investment type: {}", investment.getClass().getName());
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
        log.debug("Searching for bonds maturing in {}", year);
        List<Investment> portfolio = findAllInvestments();
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
        log.debug("Found {} bonds maturing in {}", bonds.size(), year);
        return bonds;
    }

    public Investment findHighestValueAsset() {
        log.debug("Finding highest value asset");
        Investment investment = null;
        List<Investment> portfolio = findAllInvestments();
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
        log.info("Highest value asset is {} with value {}",
                investment.getName(), max);
        return investment;
    }


    public void createInvestment(Investment newInvestment) {
        Validate.notBlank(newInvestment.getName(), "Name cannot be empty");
        newInvestment.validate();
        repository.add(newInvestment);
        log.info("Investment created: {}", newInvestment.getName());
    }

    public List<Investment> findAllInvestments() {
        log.debug("Loading all investments from repository");
        List<Investment> portfolio = repository.load();
        log.debug("Loaded {} investments", portfolio.size());
        return portfolio;
    }

    public void cloneInvestment(int id) throws CloneNotSupportedException {
        Investment copyInvestment = repository.loadById(id);
        Investment investmentClone = (Investment) copyInvestment.clone();
        Objects.requireNonNull(investmentClone);
        repository.add(investmentClone);
        log.info("Investment cloned: {}, {}", investmentClone.getId(), investmentClone.getName());
    }
}

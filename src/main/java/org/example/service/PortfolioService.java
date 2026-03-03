package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.example.model.*;
import org.example.repository.JdbcInvestmentRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class PortfolioService {

    private static final String INCORRECT_MESSAGE = "This %s doesn't exist.";

    private final JdbcInvestmentRepository repository;

    public double calculateTotalPortfolioValue() {
        log.debug("Calculating total portfolio value");
        List<Investment> portfolio = findAllInvestments();
        return portfolio.stream().mapToDouble(Investment::calculateCurrentValue).sum();
    }

    public double calculateTotalProjectedAnnualReturn() {
        List<Investment> portfolio = findAllInvestments();
        return portfolio.stream().mapToDouble(Investment::getProjectedAnnualReturn).sum();
    }

    public Map<InvestmentType, DoubleSummaryStatistics> findAssetAllocationByType() {
        List<Investment> investmentList = findAllInvestments();
        return investmentList.stream().collect(Collectors.groupingBy(
                investment -> {
                    if (investment instanceof Stock) return InvestmentType.STOCK;
                    if (investment instanceof Bond) return InvestmentType.BOND;
                    if (investment instanceof MutualFund) return InvestmentType.MUTUAL_FUND;
                    log.error("Unknown investment type: {}", investment.getClass().getName());
                    throw new IllegalStateException(INCORRECT_MESSAGE.formatted(investment));
                },
                Collectors.summarizingDouble(Investment::calculateCurrentValue)));
    }

    public List<Investment> findBondsMaturingIn(int year) {
        log.debug("Searching for bonds maturing in {}", year);
        List<Investment> portfolio = findAllInvestments();
        return portfolio.stream().filter(investment -> investment instanceof Bond bond
                && bond.getMaturityDate().getYear() == year).toList();
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

package org.example.repository;

import org.example.exception.IncorrectLoadException;
import org.example.exception.IncorrectSQLInputException;
import org.example.exception.IncorrectSaveException;
import org.example.model.Bond;
import org.example.model.Investment;
import org.example.model.MutualFund;
import org.example.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class BinaryRepository {

   /* private final File filePath;
    private static final String PORTFOLIO_LOAD_MESSAGE = "Failed to load portfolio state";
    private static final String PORTFOLIO_SAVE_MESSAGE = "Failed to save portfolio state";

    */


    private static final Logger logger = LoggerFactory.getLogger(BinaryRepository.class);
    private static final String INSERT_INVESTMENT = "INSERT INTO investments(id, name) VALUES (?,?)";
    private static final String INSERT_BONDS = "INSERT INTO bonds(id_investment, face_value,coupon_rate,local_date) VALUES (?,?,?,?)";
    private static final String INSERT_STOCK = "INSERT INTO stock(id_investment, ticker_symbol,shares,current_share_price, annual_dividend_per_share) VALUES (?,?,?,?,?)";
    private static final String INSERT_MUTUAL_FUND = "INSERT INTO mutual_fund(id_investment, fund_code,units_held,current_nav, avg_annual_distribution) VALUES (?,?,?,?,?)";
    private static final String LOGIN = "postgres";
    private static final String PASSWORD = "mysecretpassword";
    private static final String URL = "jdbc:postgresql://localhost:5433/postgres";

/*
    public BinaryRepository() {
        this("src/main/resources/portfolio.ser");
    }
    BinaryRepository(String path) {
        this.filePath = new File(path);
    }
   */


    public void add(Investment investment) throws SQLException {
        Connection conn = openConnection();
        try (conn;
             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_INVESTMENT)) {
            conn.setAutoCommit(false);
            preparedStatement.setInt(1, investment.getId());
            preparedStatement.setString(2, investment.getName());
            preparedStatement.executeUpdate();
            conn.commit();
            switch (investment) {
                case Bond bond -> {
                    try (PreparedStatement preparedStatementBond = conn.prepareStatement(INSERT_BONDS)) {
                        preparedStatementBond.setInt(1, investment.getId());
                        preparedStatementBond.setDouble(2, bond.getFaceValue());
                        preparedStatementBond.setDouble(3, bond.getCouponRate());
                        preparedStatementBond.setDate(4, Date.valueOf(bond.getMaturityDate()));
                        preparedStatementBond.executeUpdate();
                    }
                }
                case Stock stock -> {
                    try (PreparedStatement preparedStatementStock = conn.prepareStatement(INSERT_STOCK)) {
                        preparedStatementStock.setInt(1, investment.getId());
                        preparedStatementStock.setString(2, stock.getTickerSymbol());
                        preparedStatementStock.setInt(3, stock.getShares());
                        preparedStatementStock.setDouble(4, stock.getCurrentSharePrice());
                        preparedStatementStock.setDouble(5, stock.getAnnualDividendPerShare());
                        preparedStatementStock.executeUpdate();
                    }
                }
                case MutualFund mutualFund -> {
                    try (PreparedStatement preparedStatementMutualFund = conn.prepareStatement(INSERT_MUTUAL_FUND)) {
                        preparedStatementMutualFund.setInt(1, investment.getId());
                        preparedStatementMutualFund.setString(2, mutualFund.getFundCode());
                        preparedStatementMutualFund.setDouble(3, mutualFund.getUnitsHeld());
                        preparedStatementMutualFund.setDouble(4, mutualFund.getCurrentNAV());
                        preparedStatementMutualFund.setDouble(5, mutualFund.getAvgAnnualDistribution());
                        preparedStatementMutualFund.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new IncorrectSQLInputException("Failed to insert investment into database", e);
        }
    }


    private Connection openConnection() {
        try {
            return DriverManager.getConnection(URL, LOGIN, PASSWORD);
        } catch (SQLException e) {
            throw new IncorrectSQLInputException("Impossible connect with database", e);
        }
    }

    /*
    public List<Investment> loadState() {
        if (filePath.length() == 0) {
            logger.warn("Portfolio file is empty, returning an empty portfolio");
            return new LinkedList<>();
        }
        List<Investment> portfolio = new LinkedList<>();
        logger.info("Loading portfolio state from {}", filePath.getAbsolutePath());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            boolean endOfFile = false;
            while (!endOfFile) {
                try {
                    portfolio.add((Investment) objectInputStream.readObject());
                } catch (EOFException | RuntimeException e) {
                    endOfFile = true;
                }
            }
            logger.info("Successfully loaded {} investments", portfolio.size());
            return portfolio;
        } catch (ClassNotFoundException | IOException e) {
            logger.error(PORTFOLIO_LOAD_MESSAGE, e);
            throw new IncorrectLoadException(PORTFOLIO_LOAD_MESSAGE, e);
        }
    }

    public void saveState(List<Investment> data) {
        logger.info("Saving portfolio state to {}", filePath.getAbsolutePath());
        try (FileOutputStream out = new FileOutputStream(String.valueOf(filePath));
             ObjectOutput objectOutput = new ObjectOutputStream(out)) {
            for (Investment investment : data) {
                objectOutput.writeObject(investment);
            }
            logger.info("Successfully saved {} investments", data.size());
        } catch (IOException e) {
            logger.error(PORTFOLIO_SAVE_MESSAGE, e);
            throw new IncorrectSaveException(PORTFOLIO_SAVE_MESSAGE, e);
        }
    }

     */
}

package org.example.repository;

import org.example.exception.IncorrectSQLInputException;
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

    private static final Logger logger = LoggerFactory.getLogger(BinaryRepository.class);
    private static final String INSERT_INVESTMENT = "INSERT INTO investments(id, name) VALUES (?,?)";
    private static final String INSERT_BONDS = "INSERT INTO bonds(id_investment, face_value,coupon_rate,local_date) VALUES (?,?,?,?)";
    private static final String INSERT_STOCK = "INSERT INTO stock(id_investment, ticker_symbol,shares,current_share_price, annual_dividend_per_share) VALUES (?,?,?,?,?)";
    private static final String INSERT_MUTUAL_FUND = "INSERT INTO mutual_fund(id_investment, fund_code,units_held,current_nav, avg_annual_distribution) VALUES (?,?,?,?,?)";
    private static final String SELECT_BONDS = "SELECT * FROM bonds b LEFT JOIN investments i ON i.id = b.id_investment ";
    private static final String SELECT_STOCK = "SELECT * FROM stock s LEFT JOIN investments i ON i.id = s.id_investment";
    private static final String SELECT_MUTUAL_FUND = "SELECT * FROM mutual_fund m LEFT JOIN investments i ON i.id = m.id_investment";
    private static final String LOGIN = "postgres";
    private static final String PASSWORD = "mysecretpassword";
    private static final String URL = "jdbc:postgresql://localhost:5433/postgres";

    public void add(Investment investment) {
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
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new IncorrectSQLInputException("Failed to insert investment into database", e);
        }
    }

    public List<Investment> load() {
        List<Investment> portfolio = new LinkedList<>();
        Connection conn = openConnection();
        try (conn) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT_BONDS);
                 ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    portfolio.add(Bond.builder().id(rs.getInt("id")).name(rs.getString("name"))
                            .faceValue(rs.getDouble("face_value"))
                            .couponRate(rs.getDouble("coupon_rate"))
                            .maturityDate(rs.getDate("local_date").toLocalDate()).build());
                }
            }
            try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT_STOCK);
                 ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    portfolio.add(Stock.builder().id(rs.getInt("id")).name(rs.getString("name"))
                            .tickerSymbol(rs.getString("ticker_symbol"))
                            .shares(rs.getInt("shares"))
                            .currentSharePrice(rs.getDouble("current_share_price"))
                            .annualDividendPerShare(rs.getDouble("annual_dividend_per_share"))
                            .build());
                }
            }
            try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT_MUTUAL_FUND);
                 ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    portfolio.add(MutualFund.builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .fundCode(rs.getString("fund_code"))
                            .unitsHeld(rs.getDouble("units_held"))
                            .currentNAV(rs.getDouble("current_nav"))
                            .avgAnnualDistribution(rs.getDouble("avg_annual_distribution")).build());
                }
            }
        } catch (SQLException e) {
            logger.warn("Error while loading: " + portfolio.size());
            throw new IncorrectSQLInputException("Failed to insert investment into database", e);
        }
        return portfolio;
    }


    private Connection openConnection() {
        try {
            return DriverManager.getConnection(URL, LOGIN, PASSWORD);
        } catch (SQLException e) {
            throw new IncorrectSQLInputException("Impossible connect with database", e);
        }
    }
}

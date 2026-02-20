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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BinaryRepository {

    private static final Logger logger = LoggerFactory.getLogger(BinaryRepository.class);
    private static final String INSERT_INVESTMENT = "INSERT INTO investments(name) VALUES (?) RETURNING id";
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
        try {
            conn.setAutoCommit(false);
            int id = insertInvestment(conn, investment);
            insertSpecific(conn, investment, id);
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.close();
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
            portfolio.addAll(loadBond(conn));
            portfolio.addAll(loadStock(conn));
            portfolio.addAll(loadMF(conn));
        } catch (SQLException e) {
            logger.warn("Error while loading: " + portfolio.size());
            throw new IncorrectSQLInputException("Failed to load investment from database", e);
        }
        return portfolio;
    }

    private List<Investment> loadBond(Connection conn) throws SQLException {
        List<Investment> portfolio = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT_BONDS);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                portfolio.add(Bond.builder().id(rs.getInt("id")).name(rs.getString("name"))
                        .faceValue(rs.getDouble("face_value"))
                        .couponRate(rs.getDouble("coupon_rate"))
                        .maturityDate(rs.getDate("local_date").toLocalDate()).build());
            }
        }
        return portfolio;
    }

    private List<Investment> loadStock(Connection conn) throws SQLException {
        List<Investment> portfolio = new ArrayList<>();
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
        return portfolio;
    }

    private List<Investment> loadMF(Connection conn) throws SQLException {
        List<Investment> portfolio = new ArrayList<>();
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
        return portfolio;
    }

    private int insertInvestment(Connection conn, Investment investment) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_INVESTMENT)) {
            ps.setString(1, investment.getName());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");
        }
    }

    private void insertSpecific(Connection conn, Investment investment, int id) throws SQLException {
        switch (investment) {
            case Bond bond -> insertBond(conn, bond, id);
            case Stock stock -> insertStock(conn, stock, id);
            case MutualFund mf -> insertMutualFund(conn, mf, id);
        }
    }

    private void insertBond(Connection conn, Bond bond, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_BONDS)) {
            ps.setInt(1, id);
            ps.setDouble(2, bond.getFaceValue());
            ps.setDouble(3, bond.getCouponRate());
            ps.setDate(4, Date.valueOf(bond.getMaturityDate()));
            ps.executeUpdate();
        }
    }

    private void insertStock(Connection conn, Stock stock, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_STOCK)) {
            ps.setInt(1, id);
            ps.setString(2, stock.getTickerSymbol());
            ps.setInt(3, stock.getShares());
            ps.setDouble(4, stock.getCurrentSharePrice());
            ps.setDouble(5, stock.getAnnualDividendPerShare());
            ps.executeUpdate();
        }
    }

    private void insertMutualFund(Connection conn, MutualFund mutualFund, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_MUTUAL_FUND)) {
            ps.setInt(1, id);
            ps.setString(2, mutualFund.getFundCode());
            ps.setDouble(3, mutualFund.getUnitsHeld());
            ps.setDouble(4, mutualFund.getCurrentNAV());
            ps.setDouble(5, mutualFund.getAvgAnnualDistribution());
            ps.executeUpdate();
        }
    }

    private Connection openConnection() {
        try {
            return DriverManager.getConnection(URL, LOGIN, PASSWORD);
        } catch (SQLException e) {
            throw new IncorrectSQLInputException("Impossible connect with database", e);
        }
    }
}

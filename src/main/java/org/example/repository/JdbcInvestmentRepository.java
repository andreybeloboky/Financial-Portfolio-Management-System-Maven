package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.IncorrectSQLInputException;
import org.example.model.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
public class JdbcInvestmentRepository {

    private static final String INSERT_INVESTMENT = "INSERT INTO investments(name,type) VALUES (?,?) RETURNING id";
    private static final String INSERT_BONDS = "INSERT INTO bonds(id_investment, face_value,coupon_rate,local_date) VALUES (?,?,?,?)";
    private static final String INSERT_STOCK = "INSERT INTO stocks(id_investment, ticker_symbol,shares,current_share_price, annual_dividend_per_share) VALUES (?,?,?,?,?)";
    private static final String INSERT_MUTUAL_FUND = "INSERT INTO mutual_funds(id_investment, fund_code,units_held,current_nav, avg_annual_distribution) VALUES (?,?,?,?,?)";
    private static final String SELECT = """
            SELECT *
            FROM investments i
            LEFT JOIN bonds b2 ON i.id = b2.id_investment
            LEFT JOIN stocks s ON i.id = s.id_investment
            LEFT JOIN mutual_funds mf ON i.id = mf.id_investment
            ORDER BY id
            """;

    private static final String LOGIN =System.getenv("DB_LOGIN");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    private static final String URL = System.getenv("DB_URL");

    public List<Investment> load() {
        List<Investment> portfolio;
        Connection conn = openConnection();
        try (conn) {
            portfolio = load(conn);
            log.info("Loaded {} investments into portfolio", portfolio.size());
        } catch (SQLException e) {
            log.warn("Error while loading investments", e);
            throw new IncorrectSQLInputException("Failed to load investment from database", e);
        }
        return portfolio;
    }

    private List<Investment> load(Connection conn) throws SQLException {
        List<Investment> portfolio = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                InvestmentType type = InvestmentType.valueOf(rs.getString("type"));
                switch (type) {
                    case BOND -> portfolio.add(Bond.builder().id(rs.getInt("id")).name(rs.getString("name"))
                            .faceValue(rs.getDouble("face_value"))
                            .couponRate(rs.getDouble("coupon_rate"))
                            .maturityDate(rs.getDate("local_date").toLocalDate()).build());
                    case STOCK -> portfolio.add(Stock.builder().id(rs.getInt("id")).name(rs.getString("name"))
                            .tickerSymbol(rs.getString("ticker_symbol"))
                            .shares(rs.getInt("shares"))
                            .currentSharePrice(rs.getDouble("current_share_price"))
                            .annualDividendPerShare(rs.getDouble("annual_dividend_per_share"))
                            .build());
                    case MUTUAL_FUND -> portfolio.add(MutualFund.builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .fundCode(rs.getString("fund_code"))
                            .unitsHeld(rs.getDouble("units_held"))
                            .currentNAV(rs.getDouble("current_nav"))
                            .avgAnnualDistribution(rs.getDouble("avg_annual_distribution")).build());
                }
            }
        }
        return portfolio;
    }

    public void add(Investment investment) {
        Connection conn = openConnection();
        try {
            conn.setAutoCommit(false);
            int id = insertInvestment(conn, investment);
            insertSpecific(conn, investment, id);
            conn.commit();
            log.info("Transaction commited");
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException ex) {
                log.warn("Rollback failed after SQL exception", ex);
                throw new RuntimeException(ex);
            }
            log.warn("SQL exception occurred during insert operation");
            throw new IncorrectSQLInputException("Failed to insert investment into database", e);
        }
    }

    private int insertInvestment(Connection conn, Investment investment) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_INVESTMENT)) {
            ps.setString(1, investment.getName());
            switch (investment) {
                case Bond ignored -> ps.setString(2, "BOND");
                case Stock ignored -> ps.setString(2, "STOCK");
                case MutualFund ignored -> ps.setString(2, "MUTUAL_FUND");
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
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
            log.info("Opening database connection");
            return DriverManager.getConnection(URL, LOGIN, PASSWORD);
        } catch (SQLException e) {
            log.warn("Unable to establish database connection", e);
            throw new IncorrectSQLInputException("Impossible connect with database", e);
        }
    }
}

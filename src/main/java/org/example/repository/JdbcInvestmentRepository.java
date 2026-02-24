package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.DataAccessException;
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
    private static final String LOGIN = System.getenv("DB_LOGIN");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    private static final String URL = System.getenv("DB_URL");
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String FACE_VALUE = "face_value";
    private static final String COUPON_RATE = "coupon_rate";
    private static final String LOCAL_DATE = "local_date";
    private static final String TICKER_SYMBOL = "ticker_symbol";
    private static final String SHARES = "shares";
    private static final String CURRENT_SHARE_PRICE = "current_share_price";
    private static final String ANNUAL_DIVIDEND_PER_SHARE = "annual_dividend_per_share";
    private static final String FUND_CODE = "fund_code";
    private static final String UNITS_HELD = "units_held";
    private static final String CURRENT_NAV = "current_nav";
    private static final String AVG_ANNUAL_DISTRIBUTION = "avg_annual_distribution";


    public List<Investment> load() {
        List<Investment> portfolio;
        try (Connection conn = openConnection()) {
            portfolio = load(conn);
            log.info("Loaded {} investments into portfolio", portfolio.size());
        } catch (SQLException e) {
            log.warn("Error while loading investments", e);
            throw new DataAccessException("Failed to load investment from database", e);
        }
        return portfolio;
    }

    private List<Investment> load(Connection conn) throws SQLException {
        List<Investment> portfolio = new ArrayList<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                InvestmentType type = InvestmentType.valueOf(rs.getString(TYPE));
                switch (type) {
                    case BOND -> portfolio.add(Bond.builder().id(rs.getInt(ID)).name(rs.getString(NAME))
                            .faceValue(rs.getDouble(FACE_VALUE))
                            .couponRate(rs.getDouble(COUPON_RATE))
                            .maturityDate(rs.getDate(LOCAL_DATE).toLocalDate()).build());
                    case STOCK -> portfolio.add(Stock.builder().id(rs.getInt(ID)).name(rs.getString(NAME))
                            .tickerSymbol(rs.getString(TICKER_SYMBOL))
                            .shares(rs.getInt(SHARES))
                            .currentSharePrice(rs.getDouble(CURRENT_SHARE_PRICE))
                            .annualDividendPerShare(rs.getDouble(ANNUAL_DIVIDEND_PER_SHARE))
                            .build());
                    case MUTUAL_FUND -> portfolio.add(MutualFund.builder()
                            .id(rs.getInt(ID))
                            .name(rs.getString(NAME))
                            .fundCode(rs.getString(FUND_CODE))
                            .unitsHeld(rs.getDouble(UNITS_HELD))
                            .currentNAV(rs.getDouble(CURRENT_NAV))
                            .avgAnnualDistribution(rs.getDouble(AVG_ANNUAL_DISTRIBUTION)).build());
                }
            }
        }
        return portfolio;
    }

    public void add(Investment investment) {
        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);
            try {
                int id = insertInvestment(conn, investment);
                insertSpecific(conn, investment, id);
                conn.commit();
                log.info("Transaction committed");
            } catch (SQLException e) {
                conn.rollback();
                log.warn("SQL exception occurred, transaction rolled back", e);
                throw new DataAccessException("Failed to insert investment into database", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
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
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Creating investment failed, no ID obtained.");
                }
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
            throw new DataAccessException("Impossible connect with database", e);
        }
    }
}

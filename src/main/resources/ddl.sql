CREATE TABLE investments
(
    id   INT GENERATED ALWAYS AS IDENTITY (START WITH 1) PRIMARY KEY,
    name VARCHAR NOT NULL,
    type VARCHAR NOT NULL
);

CREATE TABLE bonds
(
    id_investment INT REFERENCES investments (id) ON DELETE CASCADE,
    face_value    NUMERIC(10, 2),
    coupon_rate   NUMERIC(10, 2),
    maturity_date DATE,
);

CREATE TABLE stocks
(
    id_investment             INT REFERENCES investments (id) ON DELETE CASCADE,
    ticker                    VARCHAR,
    shares                    INT NOT NULL,
    current_share_price       NUMERIC(10, 2),
    annual_dividend_per_share NUMERIC(10, 2)
);

CREATE TABLE mutual_funds
(
    id_investment           INT REFERENCES investments (id) ON DELETE CASCADE,
    fund_code               VARCHAR,
    units_held              NUMERIC(10, 2),
    current_nav             NUMERIC(10, 2),
    avg_annual_distribution NUMERIC(10, 2)
);

package org.example.repository;



import org.example.model.Investment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class BinaryRepository {

    private static final File FILE_PATH = new File("resources/portfolio.ser");
    private static final String PORTFOLIO_LOAD_MESSAGE = "Failed to load portfolio state";
    private static final String PORTFOLIO_SAVE_MESSAGE = "Failed to save portfolio state";
    private static final Logger logger = LoggerFactory.getLogger(BinaryRepository.class);

    public List<Investment> loadState() {
        if (FILE_PATH.length() == 0) {
            logger.warn("Portfolio file is empty, returning an empty portfolio");
            return new LinkedList<>();
        }
        List<Investment> portfolio = new LinkedList<>();
        logger.info("Loading portfolio state from {}", FILE_PATH.getAbsolutePath());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
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
            throw new PortfolioLoadException(PORTFOLIO_LOAD_MESSAGE, e);
        }
    }

    public void saveState(List<Investment> data) {
        logger.info("Saving portfolio state to {}", FILE_PATH.getAbsolutePath());
        try ( FileOutputStream out = new FileOutputStream(String.valueOf(FILE_PATH));
              ObjectOutput objectOutput = new ObjectOutputStream(out)) {
            for (Investment investment : data) {
                objectOutput.writeObject(investment);
            }
            logger.info("Successfully saved {} investments", data.size());
        } catch (IOException e) {
            logger.error(PORTFOLIO_SAVE_MESSAGE, e);
            throw new PortfolioSaveException(PORTFOLIO_SAVE_MESSAGE, e);
        }
    }
}

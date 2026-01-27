package org.example.repository;



import org.example.model.Investment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class BinaryRepository {

    private final File filePath;
    private static final String PORTFOLIO_LOAD_MESSAGE = "Failed to load portfolio state";
    private static final String PORTFOLIO_SAVE_MESSAGE = "Failed to save portfolio state";
    private static final Logger logger = LoggerFactory.getLogger(BinaryRepository.class);

    public BinaryRepository() {
        this("src/main/resources/portfolio.ser");
    }

    BinaryRepository(String path) {
        this.filePath = new File(path);
    }

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
}

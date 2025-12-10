package org.example.repository;



import org.example.model.Investment;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class BinaryRepository {

    private static final File FILE_PATH = new File("resources/portfolio.ser");
    private static final String PORTFOLIO_LOAD_MESSAGE = "Failed to load portfolio state";
    private static final String PORTFOLIO_SAVE_MESSAGE = "Failed to save portfolio state";

    public List<Investment> loadState() {
        if (FILE_PATH.length() == 0) {
            return new LinkedList<>();
        }
        List<Investment> portfolio = new LinkedList<>();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            boolean endOfFile = false;
            while (!endOfFile) {
                try {
                    portfolio.add((Investment) objectInputStream.readObject());
                } catch (EOFException | RuntimeException e) {
                    endOfFile = true;
                }
            }
            return portfolio;
        } catch (ClassNotFoundException | IOException e) {
            throw new PortfolioLoadException(PORTFOLIO_LOAD_MESSAGE, e);
        }
    }

    public void saveState(List<Investment> data) {
        try ( FileOutputStream out = new FileOutputStream(String.valueOf(FILE_PATH));
              ObjectOutput objectOutput = new ObjectOutputStream(out)) {
            for (Investment investment : data) {
                objectOutput.writeObject(investment);
            }
        } catch (IOException e) {
            throw new PortfolioSaveException(PORTFOLIO_SAVE_MESSAGE, e);
        }
    }
}

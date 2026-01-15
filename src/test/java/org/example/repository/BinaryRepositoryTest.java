package org.example.repository;

import org.example.model.Investment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryRepositoryTest {

    @Test
    public void loadStateTest() {
        BinaryRepository binaryRepository = new BinaryRepository("src/test/resources/anothername.ser");
        List<Investment> investments = binaryRepository.loadState();
        assertEquals(6, investments.size());
    }
}
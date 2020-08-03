package ru.craftysoft.irrigationsystemservice.logic;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PositionDeterminationOperationTest {

    @Test
    void process() throws IOException {
        var result = new PositionDeterminationOperation().process("src/test/resources/test.txt");

        assertThat(result).isEqualTo(357.1375947738882d);
    }
}
package io.parmigiano;

import org.junit.jupiter.api.Test;

import static io.parmigiano.Permutation.cycle;
import static org.junit.jupiter.api.Assertions.assertEquals;

class S3Test {

    @Test
    void testS3() {
        assertEquals(cycle(1, 2), cycle(0, 2).compose(1, 2).compose(0, 1));
        assertEquals(cycle(0, 1), cycle(0, 2).compose(0, 1).compose(1, 2));
        assertEquals(cycle(0, 2), cycle(1, 2).compose(0, 2).compose(0, 1));
        assertEquals(cycle(0, 1), cycle(1, 2).compose(0, 1).compose(0, 2));
        assertEquals(cycle(0, 2), cycle(0, 1).compose(0, 2).compose(1, 2));
        assertEquals(cycle(1, 2), cycle(0, 1).compose(1, 2).compose(0, 2));
    }
}

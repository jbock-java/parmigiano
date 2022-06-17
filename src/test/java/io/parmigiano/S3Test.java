package io.parmigiano;

import org.junit.jupiter.api.Test;

import static io.parmigiano.Permutation.create;
import static org.junit.jupiter.api.Assertions.assertEquals;

class S3Test {

    @Test
    void testS3() {
        assertEquals(create(1, 2), create(0, 2).compose(1, 2).compose(0, 1));
        assertEquals(create(0, 1), create(0, 2).compose(0, 1).compose(1, 2));
        assertEquals(create(0, 2), create(1, 2).compose(0, 2).compose(0, 1));
        assertEquals(create(0, 1), create(1, 2).compose(0, 1).compose(0, 2));
        assertEquals(create(0, 2), create(0, 1).compose(0, 2).compose(1, 2));
        assertEquals(create(1, 2), create(0, 1).compose(1, 2).compose(0, 2));
    }
}

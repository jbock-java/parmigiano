package io.parmigiano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

// https://www.youtube.com/watch?v=s8-JXU8TeE8
class TestPythagoras {

  record Triple(int a, int b, int c) {
    boolean isPythagoras() {
      return a * a + b * b - c * c == 0;
    }
  }

  Triple createTriple(int p, int q) {
    return new Triple(2 * p * q, p * p - q * q, p * p + q * q);
  }

  @Test
  void checkTriples() {
    for (int p = 5; p < 10; p++) {
      for (int q = 15; q < 20; q++) {
        assertTrue(createTriple(p, q).isPythagoras());
      }
    }
  }
}

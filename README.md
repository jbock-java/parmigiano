### Defining a 3-cycle

````java
Permutation.cycle(0, 1).apply(List.of("a", "b", "c"))
// => ["b", "a", "c"]
````

### Composition

````java
Permutation.cycle(0, 1).compose(2, 3);
// => (0 1) (2 3)
````

### Getting all permutations of 5 Elements

````java
Permutation.symmetricGroup(5).count();
// => 120
````

Permutation group:

* https://github.com/cicirello/JavaPermutationTools

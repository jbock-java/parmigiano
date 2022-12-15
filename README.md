### Defining a 3-cycle

Permutation p = Permutation.create(0, 1, 2);
Permutation.apply(List.of("a", "b", "c"));
// => ["c", "a", "b"]

### Composition

Permutation.create(0, 1).compose(2, 3);
// => (0 1) (2 3)

### Getting all permutations of 5 Elements

````java
Permutation.symmetricGroup(5).count();
// => 120
````




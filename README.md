### Defining a 3-cycle

````java
Permutation p = Permutation.create(0, 1, 2);
p.apply(List.of("a", "b", "c"));
// => ["c", "a", "b"]
````

### Composition

````java
Permutation.create(0, 1).compose(2, 3);
// => (0 1) (2 3)
````

### Getting all permutations of 5 Elements

````java
Permutation.symmetricGroup(5).count();
// => 120
````


[![parmigiano](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/parmigiano/badge.svg?subject=parmigiano)](https://maven-badges.herokuapp.com/maven-central/io.github.jbock-java/parmigiano)

This library does finite permutations in Java.

### Cycle based

````java
Permutation.cycle(0, 1).apply(List.of("a", "b", "c"));
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

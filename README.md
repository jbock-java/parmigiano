[![parmigiano](https://img.shields.io/maven-central/v/io.github.jbock-java/parmigiano?label=parmigiano)](https://central.sonatype.com/artifact/io.github.jbock-java/parmigiano)

This library does finite permutations in Java.

```
$ make && make run
> (def a 1)
1
> (def b 2)
2
> (def c a)
1
> (a c)
java.lang.IllegalArgumentException: not a cycle
> (b c)
(2 1)
> (c b)
(1 2)
> (def d (b c))
(2 1)
> d
(2 1)
> (d d)
()
> (def e (d d d))
(1 2)
```

Permutation group:

* https://github.com/cicirello/JavaPermutationTools

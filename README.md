# Constants

[![](https://img.shields.io/maven-central/v/com.rezzedup.util/constants?color=ok&label=Maven%20Central)](https://search.maven.org/artifact/com.rezzedup.util/constants "Maven Central")
[![](https://img.shields.io/badge/License-MPL--2.0-blue)](./LICENSE "Project License: MPL-2.0")
[![](https://img.shields.io/badge/Java-11-orange)](# "Java Version: 11")
[![javadoc](https://javadoc.io/badge2/com.rezzedup.util/constants/javadoc.svg?label=Javadoc&color=%234D7A97)](https://javadoc.io/doc/com.rezzedup.util/constants "View Javadocs") 

Utilities for `static final` constants. Use 'em like enums.

```java
public class Example
{
    public static final ComplexObject<String> STRING_CONSTANT =
        ComplexObject.builder("abc").example("xyz").enabled(true).build();
    
    public static final ComplexObject<Integer> INTEGER_CONSTANT =
        ComplexObject.builder(1).example(-1).enabled(true).build();
    
    @NotAggregated
    public static final ComplexObject<Float> FLOAT_CONSTANT =
        ComplexObject.builder(1.0F).example(-1.0F).enabled(false).build();
    
    public static final ComplexObject<Double> DOUBLE_CONSTANT =
        ComplexObject.builder(1.0).example(-1.0).enabled(true).build();
    
    @AggregatedResult
    public static final List<ComplexObject<?>> VALUES =
        Aggregates.list(Example.class, new TypeCapture<ComplexObject<?>>() {});
}
```

## Rationale

This library automates aggregating constants via reflection.
Or, in other words, it allows you to collect constants matching
specific type and name criteria in an orderly, enum-like way.

Now, why not just use enums? While enums are among the most
useful constructs offered by the Java language, they are, however,
deliberately limited in which data they're capable of representing.
Since all enums share a common supertype, they cannot extend any
other class by design. 

Use this library when you need the 'collected' nature of an enum
in conjunction with the flexibility of full, unbridled objects.

In order to adequately support generics, this library also includes
various utilities for handling types, such as: a "super" type token
(`TypeCapture`), casting utility (`Cast`), primitive auto-boxing
utility (`Primitives`), and more. While seemingly unrelated, these
tools are included here to avoid introducing any extra dependencies. 

## Maven

```xml
<dependency>
    <groupId>com.rezzedup.util</groupId>
    <artifactId>constants</artifactId>
    <version><!--release--></version>
</dependency>
```

### Versions

The latest version is the same as the most recently published `tag` on
the releases page of this repository.

Maven Central: https://search.maven.org/artifact/com.rezzedup.util/constants

<details id="note-snapshot-versions">
<summary><b>Note:</b> <i>Snapshot Versions</i></summary>

> [ℹ️](#note-snapshot-versions)
> Snapshot releases are available at the following repository:
>
> ```xml
> <repositories>
>     <repository>
>         <id>ossrh-snapshots</id>
>         <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
>     </repository>
> </repositories>
> ```
</details>

### Shading

If you intend to shade this library, please consider **relocating** the packages
to avoid potential conflicts with other projects. This library also utilizes
nullness annotations, which may be undesirable in a shaded uber-jar. They can
safely be excluded, and you are encouraged to do so.

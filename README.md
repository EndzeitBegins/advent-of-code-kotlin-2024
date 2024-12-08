# advent-of-code-kotlin-2024

Welcome to the Advent of Code[^aoc] 2024 project of [endzeitbegins][github].

The repository contains some of my solutions to this years Advent of Code using the programming langue [Kotlin][kotlin].

I've generated the base of this project initially using the [Advent of Code Kotlin Template][template] of 2023 delivered by JetBrains.
This project was then forked off of [my project for the 2023 edition][aoc-2023].

## Generate files for a day

The Gradle task `generateDay` can be used to scaffold the directory and files for a given day.

```shell
./gradlew generateDay -Pday=6
```

[^aoc]:
    [Advent of Code][aoc] – An annual event of Christmas-oriented programming challenges started December 2015.
    Every year since then, beginning on the first day of December, a programming puzzle is published every day for twenty-five days.
    You can solve the puzzle and provide an answer using the language of your choice.

[aoc]: https://adventofcode.com
[github]: https://github.com/endzeitbegins
[kotlin]: https://kotlinlang.org
[template]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template
[aoc-2023]: https://github.com/EndzeitBegins/advent-of-code-kotlin-2023

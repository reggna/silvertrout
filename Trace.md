# Introduction #

Trace is a plugin for fetching personal information from swedish telefon numbers.
It starts by fetching personal information such as name, address, ZIP code, &c. It also tries to fetch the Social Security number of the person in question.
The bot then goes on fetching telefon operator information.
Last it checks marital status and corporation involvement.

# Instructions #

Simply load the plugin, type !trace together with the telefon number you would like to trace and wait for the bot to collect information from the Internet.

# Example #
```
20:39:33 <@reggna> !trace 0733910407
20:39:33 < bot2> reggna: Ingvar Oldsberg, Vasaplatsen 2, 41134 GÖTEBORG 19450331-XXXX
20:39:37 < bot2> reggna: 0733910407: Telenor Sverige AB
20:39:38 < bot2> reggna: Civilstånd: Gift, Bolagsengagemang: Finns inga registrerade
```
Note that the Social Security number has been shortened in this example.
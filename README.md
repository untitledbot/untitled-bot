# untitled-bot

untitled-bot is a multi-purpose bot that
has a lot of room for customization and
(soon) plugin development.  You can self host it as well.

![Java CI with Maven (UBUNTU)](https://github.com/AlexIsOK/untitled-bot/workflows/Java%20CI%20with%20Maven%20(UBUNTU)/badge.svg)

---

### Features
* Custom permission nodes for users, roles, and the entire guild (adapted from the Unix permission system).
* Level/rank module.
* Per guild permissions.
* Logging

---
### Upcoming features
* Plugin support
* Web UI
* (M)MORPG module (Massive Multiplayer Online Role Playing Game, though probably not that massive).
* Music player
* Message sender (send messages to a user when they join the server).
---
### Discord server
You can join the untitled-bot discord server [here](https://alexisok.dev/ub/discord.html).

---
### Self hosting
NOTE: Binaries for Windows have not been tested, it might not work at all!\
Pre-compiled binaries are available [here](https://github.com/AlexIsOK/untitled-bot/releases/latest). \
or, you can compile the source code as follows:
```console
# download the requirements
# if you're not using a debian-based distro, figure it out yourself
apt install openjdk-8-jdk maven

# clone the repository and cd
git clone https://github.com/alexisok/untitled-bot
cd untitled-bot/

# compile from the source (requires Maven and Java 8)
mvn clean compile assembly:single


# run the bot
cd target/
java -jar untitledbot-jar-with-dependencies.jar "TOKEN"
```

I recommend using [pm2](https://github.com/Unitech/pm2) or screen
if you are self hosting the bot.

# untitled-bot

Untitled bot does things that an untitled bot would do.
<br><br><br>
<div style="text-align: center;"><a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License</a>.</div>

### Features
* Custom permission nodes for users, roles, and the entire guild.
* Level/rank module.
* Music player (play music from YouTube).

### Upcoming features
* Plugin support
* Web UI
* RPG module

### Discord server
You can join the untitled-bot discord server [here](https://alexisok.dev/ub/discord)

### Self hosting
Pre-compiled binaries are available [here](https://github.com/AlexIsOK/untitled-bot/releases/latest) \
Or you can compile the source code as follows:
```shell
# clone the repository and cd
git clone https://github.com/alexisok/untitled-bot
cd untitled-bot/

# (optional) checkout the beta or bleeding edge branch
git checkout edge
git checkout beta

# compile from the source (requires Maven and Java 11+)
mvn clean compile assembly:single

# run the bot
cd target/
java -jar untitledbot-jar-with-dependencies.jar TOKEN
```

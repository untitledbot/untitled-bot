# untitled-bot

untitled-bot is a multi-purpose bot that
has a lot of room for customization.  You can self-host it as well.

![Build](https://github.com/AlexIsOK/untitled-bot/workflows/Java%20CI%20with%20Maven%20(UBUNTU)/badge.svg)
![Version](https://img.shields.io/badge/version-1.3-blue)
---

### Features
* Level/rank module.
* Logging
* <a href="#commands">Many other commands.</a>

---
### Upcoming features
* Cron (send messages on intervals or with events)
* Web UI
* (M)MORPG module (Massive Multiplayer Online Role Playing Game, though probably not that massive).
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
apt install openjdk-11-jdk maven git

# clone the repository and cd
git clone https://github.com/alexisok/untitled-bot
cd untitled-bot/

# compile from the source
mvn clean compile assembly:single

# run the bot (the name of the file may be different)
cd target/
java -jar untitledbot.jar "TOKEN"
```

I recommend using [pm2](https://github.com/Unitech/pm2) or screen
if you are self hosting the bot.

### Privacy
(before anything, you can request your data from the bot by joining the official server and using the `data` command).\
As someone who wants privacy, I respect your right to it as well.  This is a list of everything the bot keeps on users:

```diff
@@ it will keep @@
+ Your Discord Snowflake (public ID)
+ Any data that goes through the bot (balance, xp, level, etc)
+ Shared guilds with the bot
+ A list of all server members by ID in the guild (who have sent a message)

@@ it will NOT @@
- Cache messages
- Log messages
- Save presence history
- Read you a bedtime story

@@ this information could be logged to log channels if the server admins set it up @@
+ Nickname updates
+ Role updates
+ Voice channel join/leave/switch
+ Server role changes
+ Member join/leave guild
@@ the above logs will NOT be saved on my end @@
```

### Commands
<a id="commands"></a>

`[optional argument] <required argument>`

##### Ranking
`rank [user @]` - get the rank of yourself or another user.\
`leaderboard` or `top` - get the highest ranking users in the server.\
`rank-total [user @]` - get the total amount of xp of yourself or another user.\
`rank-settings <setting> <value>` - set the rank settings (see `help rank-settings`).\
`vote-reward` or `vr` - get bonus XP for [voting for the bot on top.gg](https://top.gg/bot/730135989863055472/vote).\
`rank-role <level> <role @ | role ID | role name | none>` - assign roles for users when they level up.\
`rank-roles` - get a list of current rank roles.

##### Utilities
`help [command]` - get help for a command.\
`prefix <prefix 1 to 3 chars>` - set the prefix for the server (mentioning the bot works as well).\
`status` - get the status of the bot.\
`timestamp <snowflake | user @ | channel #>` - get the timestamp of a discord snowflake.\
`uptime` - get the uptime for the bot.\
`avatar <@user>` - get the avatar of a user.\
`bug-report` - report a bug directly to me.\
`lang` - switch the bot language (help wanted for this!).\
`cron <dm | channel #> <@join | @leave | m h d m w> <message...>` - schedule a [cron](https://en.wikipedia.org/wiki/Cron)).\
`crons` - get a list of all crons in place.\
`cron-del` - delete a cron as listed in `crons`.

##### Moderation
`log-channel <text channel #>` - set the logging channel.\
`add-log` - add a log type to the log channel.\
`remove-log` - remove a log type from the log channel.\
`get-log` - get the log types the guild has in use.

##### Fun
`someone` - mention a random user (does NOT ping them).\
`brainfuck` - run [Brainf***](https://en.wikipedia.org/wiki/Brainfuck) code.\
`8ball <question>` - simulate an 8 ball.\
`ship <user A> <user B>` - ship two different server members.\
`20` - roll a 20 sided die.\
`owo <text>` - owofy a stwing of text.

##### Games
`craft <recipe | skin> <item | username>` - get the minecraft crafting recipe of an item or skin of a user.


##### Reactions
`hug` - hug another user (or yourself)...\
`ayaya` - react with ayaya (or something else).\
`hide` - hide behind a wall.\
`disappointed` - display ಠ_ಠ.

### Premium?
As of now, there are not any premium features of the bot, all features come standard for free.\
The music module is not in this bot as it would take too much CPU and memory.\
If you want to self-host a music bot, I recommend using [MusicBot](https://github.com/jagrosh/MusicBot) by [jagrosh](https://github.com/jagrosh). \
There are currently no plans to have any premium or paid-for parts of the bot.

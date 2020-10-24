# untitled-bot

untitled-bot is a multi-purpose bot that
has a lot of room for customization.  You can self-host it as well.

[![Discord Bots](https://top.gg/api/widget/730135989863055472.svg)](https://top.gg/bot/730135989863055472)
<br>
![Build](https://github.com/AlexIsOK/untitled-bot/workflows/Java%20CI%20with%20Maven%20(UBUNTU)/badge.svg)
![Version](https://img.shields.io/badge/version-1.3-blue)
[![GitHub issues](https://img.shields.io/github/issues/AlexIsOK/untitled-bot)](https://github.com/AlexIsOK/untitled-bot/issues)
[![GitHub license](https://img.shields.io/github/license/AlexIsOK/untitled-bot)](https://github.com/AlexIsOK/untitled-bot/blob/master/LICENSE.txt)
---

### Invite
You can invite untitled-bot to your server instantly using
<a href="https://discord.com/oauth2/authorize?client_id=730135989863055472&scope=bot&permissions=3460160">
this link</a>.

### Features
* Level/rank module.
* Logging
* <a href="#commands">Many other commands.</a>

---
### Upcoming features
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

<dl>
<h2>Ranking</h2>
<dd>> <code>rank [user @]</code> - get the rank of yourself or another user.</dd><br>
<dd>> <code>leaderboard</code> - show a list of the highest ranking users in the server.</dd><br>
<dd>> <code>rank-total</code> - show the total amount of xp someone has.</dd><br>
<dd>> <code>rank-settings &lt;setting&gt;</code> - set the rank settings (use <code>help rank-settings</code> for a list of rank settings).</dd><br>
<dd>> <code>rank-role</code> - give users roles when they level up.</dd><br>
<dd>> <code>rank-roles</code> - get a list of roles that are assigned on rankup.</dd><br>

<h2>Utilities</h2>
<dd>> <code>help [command]</code> - get help with a specific command, or display all the commands.</dd><br>
<dd>> <code>prefix</code> - set the prefix for the guild.</dd><br>
<dd>> <code>status</code> - get the status and some statistics of the bot.</dd><br>
<dd>> <code>timestamp &lt;snowflake | user @ | channel #&gt;</code> - get the time a user made their account, a channel was created, or the timestamp of a snowflake.</dd><br>
<dd>> <code>uptime</code> - get the time since the bot started</dd><br>
<dd>> <code>info</code> - get info about the server or a specific user.</dd><br>
<dd>> <code>avatar</code> - get the avatar of yourself or another user.</dd><br>
<dd>> <code>bug-report</code> - report a bug directly to me.</dd><br>
<dd>> <code>discord</code> - get an invite link for the Discord support server.</dd><br>
<dd>> <code>vote</code> - vote for the bot on <a href="https://top.gg">Top.GG</a>.</dd><br>
<dd>> <code>ping</code> - get the ping of the bot.</dd><br>
<dd>> <code>inv</code> - list your inventory.</dd><br>
<dd>> <code>reverse</code> - reverse a string.</dd><br>
<dd>> <code>config</code> - easy configuration for the bot.</dd><br>
<dd>> <code>profile</code> - get the profile of another user.</dd><br>
<dd>> <code>caps</code> - change a string to all capital letters.</dd><br>
<dd>> <code>lowercase</code> - change a string to all lowercase letters.</dd><br>
<dd>> <code>alternate-case</code> - MaKe A mEsSaGe ChAnGe EaCh LeTtEr CaPiTaLiZaTiOn.</dd><br>

<h2>Moderation</h2>
<dd>> <code>log-channel</code> - set the channel where things will be logged.</dd><br>
<dd>> <code>add-log</code> - add a logging type to the channel.</dd><br>
<dd>> <code>remove-log</code> - remove a logging type from the channel.</dd><br>
<dd>> <code>get-log</code> - list all logs currently in use.</dd><br>

<h2>Fun</h2>
<dd>> <code>someone</code> - mention a random user without pinging them.</dd><br>
<dd>> <code>brainfuck</code> - run <a href="https://en.wikipedia.org/wiki/Brainfuck">brainf***</a> code.</dd><br>
<dd>> <code>8ball</code> - simulate an 8 ball.</dd><br>
<dd>> <code>ship</code> - ship two different server members (user requested).</dd><br>
<dd>> <code>20</code> - roll a twenty-sided die.</dd><br>
<dd>> <code>owo &lt;text&gt;</code> - convewt da text u input to make it vewy kawaii &lt;3</dd><br>

<h2>Reactions</h2>
<dd>> <code>hug</code> - send a hug gif.</dd><br>
<dd>> <code>ayaya</code> - I encourage you to not ask questions about this command.</dd><br>
<dd>> <code>hide</code> - hide behind a wall.</dd><br>
<dd>> <code>dis</code> - short for disappointed, display ಠ_ಠ</dd><br>
</dl>

### Contributing

Please help contribute to the bot!  Having multiple people help with the bot makes the repository much better than if
one person did everything.  As of 1.3.21, contributors get a custom background for the `rank` command!

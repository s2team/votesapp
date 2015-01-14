# ![Image of our Solution](diary/onair.png)[ON AIR] until 17.01.2015 -  √ote for s2
The project is started for the [Spring Boot Contest](https://github.com/learning-spring-boot/contest). To support us please

* "Star" the Repository and/or
* Tweet about it using the #VotesApp HashTag

It is submitted by [@d0x (Christian Schneider)](https://github.com/d0x) and [@walery (Walery Strauch)](https://github.com/walery). If you have any ideas or feedback, contact us on Twitter or open a ticket on github.

During the contest we write a small [diary](https://github.com/s2team/contest#the-votesapp-diary) and maintain a [trello board for this project](https://trello.com/b/VzijoLhr/votesapp).
If you are really interested in recent updates, you *could* add this `crontab` :)

```bash
crontab -l | { cat; echo "0 13 * * * firefox https://github.com/s2team/contest#the-votesapp-diary"; } | crontab -
```

Thank you for any kind of support! - s2.

# About VotesApp [![Build Status](https://travis-ci.org/s2team/contest.svg?branch=master)](https://travis-ci.org/s2team/contest)
**VotesApp** is a *WhatsApp* Bot that helps you and your friends to organize Votes in *WhatsApp* groups.

As *WhatsApp* User the probability that you have a group together with some friends is quite huge. If not try it, it's a great feature. When you are in a group, group decision questions requiring votes from group members keep poping up, like, *Who likes to join Karting on Saturday?*, or *What about pizza today after work?*. Maybe you have such voting once a week for a fixed event like playing soccer in the evening? Then it becomes quite hard to manage this.

Now you just invite **VotesApp** into your *WhatsApp* group.
Once joined, **VotesApp** will listen to the Keywords, `in`, `out`, `yes`, `no` to count votes and `status` to give the names of the people who voted for this keyword.

# The VotesApp Diary
This project was triggered because of the [Spring Boot Contest](https://github.com/learning-spring-boot/contest). To keep the judges up to date and show them why we decided like this on some points, we try to keep the diary up to date.

So if you are interested in the huzzle we had, have a look :)


## 13.01.2014 - It's deployed!
Since today it's the full stack is deployed.

Now your can invite our Test-Number ( **+49 1577 0658227** ) into your groups to start some votes.

Basics votes (and some eastereggs) are supported, see:

![Image of our Solution](diary/firstTest_small.png)

Currently you can use the following (essential) instructions:

- Help
- Yes, No, In, Out, Maybe, +1, 2, 3,4 ..., 0
- Reset, Vote
- .+? (to set a question)
- Status

Or this (nice to have) stuff:

- Roll, Dice
- Chuck Norris


### Why it took so long?

Within the last three days we had strange encoding problems within our deployed python application.
It wasn't really clear whether where the problem is comes from.
We had serval differences (OS, Python-Version, Virtualisation) between our development machine and the server.
The final solution was quite easy, we just need to set the `locale` correct.
The identification wasn't obvious because the application was running in an docker container and you need to find the right command to execute `locale`  in our enviorment it's: `docker exec -i -t yourName bash`.

I think we describe the full stack more detailed in the next days.
We wil also try to make a plugin API (using Spring-Plugin) to support 3rd Party commands :)

Happy testing!

## 10.01.2015 - Python brings us back to the basics
**Yes!** We made Yowsup ready to use from our java backend. Here is a screenshot of the communication we did today:

![Image of our first messages](diary/yowsupFiler.png)

We are not quite proud that we need to use FileIO to send and receive messages, but thats the limitation we have by yowsup and our python skills.

In the last days we were learning some Python and understanding the Yowsup architecture (which is really great!). We tried to add a ReST Server to yowsup. Finally we regonized that the communication between WhatsApp and Yowsup stopped working when spinning up the [Bottle Rest Server](http://bottlepy.org/).

Because we don't have more time to get more deep into python, we decided to use JSON-Files to instead of an REST server for now. The result should look like this:

![Image of our file Solution](diary/fileSolution.jpeg)

The goal is to keep the VotesApp solution (which we submit) on clean. It will not have anything todo with the file io that happens on the right side (because yowsup doesn't have the rest interface yet). If you are intereseted in the python code we added you can find it in [this](https://github.com/s2team/yowsup/commit/2b24d3fa4ea1b7f821924a48b7ef60084519553e) commit. But don't take it as an best practice example :).

Now we "only" need to build the middle application what will be not that hard.

## 08.01.2015 - Ping? Pong! - Feature Request :)
Today evening we meet again and summarized the current status.

On the Python we still need to tune some things before releasing it to public. But I can tell you, there is already some nice communication going on.

On the Java we can show already some new things. Since we are using Reactor we also like to use it for our integration tests. The idea is to "replay" incomming WhatsApp Messages and then check the result. In a simple scenario we would like to test that when sending "ping", "pong" should be returned.

```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VotesAppApplication.class)
@WebAppConfiguration
public class VotesAppApplicationTest {

  @Autowired
  Reactor reactor;

  @Test
  public void ping_pong_test() throws InterruptedException {
    reactor.notify("group.inbox", Event.wrap("Ping"));

    // If the integration works well, the business logic should answer with "Pong" to "group.outbox"

    // does not exist like this (yet :( )
    assertThat(reactor, receives("Pong").in("group.outbox"));
  }
}
```

When doing so we haven't found an *elegant* way to `assertThat` a `GroupMessage`-Event was raised.
The cool thing is that the authors @smaldini and @jbrisbin [liked the Idea](https://github.com/reactor/reactor/issues/415#issuecomment-69263171) and we will maybe see it in some time.
Until then we are doing it with some `obj.notify()` and `obj.wait()` magic in our [integration test](src/test/java/de/votesapp/VotesAppUserTests_Test.java)

Another neat experience we had was the integration of an embedded mongodb for our integration tests.

The only change we need made was adding this dependency `com.github.fakemongo:fongo:1.5.9` and putting the following configuration for our tests:
```java
@Configuration
static class MongoDbTestConfiguration extends AbstractMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "test";
  }

  @Override
  public Mongo mongo() {
    return new Fongo("mongo").getMongo();
  }

  @Override
  protected String getMappingBasePackage() {
    return "de.votesapp";
  }
}
```
Because we currently don't need a real database, we decided to put it into the main configuration as long as we haven't finished the database integration and deployed it to p.

## 07.01.2015 - Hello Reactor? Can we vote?
As usual there is a good news and a bad news. Let's start with the bad one, because we learned that the last thing you, tell stays in our minds :)

Bad News: We still have some huzzle with the Python Backend. But we think we can finish it by tomorrow and then fully concentrate on the contest`s goal (Java/Spring Boot)!

Good News: To avoid having nothing by now, we added a simple ["ConsoleClient"](src/main/java/de/votesapp/client/ConsoleClient.java) to fake the WhatsApp Service. That gets loaded when the "yowsup" Profile isn't active. For this we utilized the `!` operator within the `@Profile`-Annotation.

![Image of Not in Profiles](diary/notInProfile.png)

With this we can have our first vote, which proofs that some logic is already working:

![Image of our first vote](diary/succedInTheContest.png)

To do so, we decided to remove the interfaces we had previously between our API and WhatsApp and use [reactor](https://github.com/reactor/reactor) instead.

The ["Bridge"](src/main/java/de/votesapp/api/WhatsAppClientToReactorBridge.java) between our ["ConsoleClient"](src/main/java/de/votesapp/client/ConsoleClient.java) and Reactor looks like this:

![Reactor Example](diary/reactorExample.png)

We are looking forward to do more stuff on the Java site within the next days.

## 04.01.2015 - Parallelprogramming (!= Pairprogramming)
Two days ago we met again on an epic go cart event and we had some time to discuss the next steps. We figured out that the yowsup architecture seems to be pretty nice (in terms of flexibility and readability). So we decided that we take the risk, drop Plan A (from 31.12.) and implement "only" a nice Rest-API on top off yowsup. With this the actual Java Code, what the contest is about, stays much cleaner.

With this in mind we decided that one of us will put a ReST-Layer in Python and the other one will go for the Java Code.

The result of today are a view java packages:

![Image of our first packages](diary/firstPackages.png)

They are far away from production-ready, but they show already the way it will go.

On the python/yowsup side we know how to deal with the native events, we know how to send messages and where to register our own methods. The next step will to dig deeper into how to put the ReST-Layer on-top on it.

## 01.01.2015 - Make it public
Today we decided to put our diary online. Also if there is no code yet, we hope it will be interesting enough :).

Funny background, we just registered the domain for the VotesApp. And this is how we decided who as the pleasure to register it and become the domain owner:

![Image of our Solution](diary/domainOwner.png)

## 31.12.2014 - Whats broken??!?!?!
Over the night, more and more people commented on the [*no_route* ticket mentioned](https://github.com/tgalal/yowsup/issues/370) yesterday. We saw that @tgalal was working hard on yowsup but somehow he isn't giving feedback on the registration bug.

When sit together and talking about this, we decided that the broken registration is a show stopper and we need about alternatives.

It seemed that Androids [uiautomatorviewer](http://developer.android.com/tools/testing/testing_ui.html#uianalysis) is the way to go. From our past we know that working with the Android Emulator isn't that nice for newbees (specially if you need to reboot the device often). Anyway we saw the Challenge and started installing the Android SDK, booted the Emulator and installed *WhatsApp* on it.

And hell, yeah! The uiautomatorviewer was able to parse the emulators screen give us needed ui elements content.

![Image of our UiAutomatorViewer](diary/UiAutomatorViewer.png)

Okay, the screenshot was black, but the ui structure was visible and showing its texts.

But then the next challenge started, how to remotecontrol (navigate) this "thing" now? Since we have great Selenium skills, we thought of using the selendroid library to do this. But before going to deep into Android development we had the idea to register manual (on the phone) and then continue using yowsup.

Sounds easy, but the *simple* solutions we found required us to have a rooted android phone. So we tried to root our Android Emulator but it wasn't possible on windows (at least for us). Luckly we found [this article](http://www.digitalinternals.com/security/extract-*WhatsApp*-password-android/374/) telling us how to access the emulators disk to download *WhatsApp*s highly secured passwordfile and read it. :)

That enabled us to send and receive messages over the yowsup-cli.

Okay, that wasn't the full story. We need also to apply this awesome bugfix (we will not share it with the public of course :):
![Image of our s/rase/print/g](diary/awesomeBugfix.png)

The said story of the day is that the yowsup-cli provides group interaction in the "interactive" mode only. Now the question is how to continue?

### Summary:
* Manual Registration via Emulator with the help of [this](http://www.digitalinternals.com/security/extract-*WhatsApp*-password-android/374/) was possible
* We got in touch with yowsup-cli
* Next steps
 * Plan A: Adjust Yowsup that we can send and receive group messages via the ProcessBuilder
 * Plan B: Use Yowsup as a Template for a small Java Library that only implements the 3 to 4 api calls we need (Group Messages, ...)
* Out of 6h development, we idled 2h watching the Android Emulator booting :(

## 29-30.12.2014 - Showstopper?

On thursday the first huzzle started. In the evening I tried to prepare yowsup (the inoffical *WhatsApp* library written in python) to focus on java when we meet the next time. But already at the installation of the library I got (for a java devs) strange "Tracebacks".

The solution, ... `sudo apt-get install python python-dateutil python-argparse python-setuptools`
... was easy, (if you know it :) ), some common dependencies where missing on my pc.

But it was worth it, I think [our comment](https://github.com/tgalal/yowsup/issues/464#issuecomment-68265182) lead into this commit:
https://github.com/tgalal/yowsup/commit/939af7b9101d6cada50e135710fa6032f67e5921

So our project had the first public benefit :).

But that was not everything.

Once was installed, the first step you need to execute failed because of an *no_route* error. The disappointing thing was, that we found [a ticket](https://github.com/tgalal/yowsup/issues/370) that was filed Nov. 19th and is still not solved.

But who said it should be easy...?

P.S.: My real huzzle was that I'm working on OSX and tried on the same day(s) Docker and Vagrant to get a nice Ubuntu Enviorment to play with :) *-cough-*

## 28.12.2014 - Let's do a Mockup. Damn, it! How are we gonna name this thing?
Today we meet the first time to kick-off the project. We roughly talked about the things we can demonstrate with this idea and made a small analyses which API-Calls we need. When we thought we have everything, we start implementing a first mock in Java.

The list of things we could show looks like this:

Our app will (*hopfully* :) )...,
* be a neat and sweat backend service (focus on spring boot, not on UI)
* have a decent business layer (not too much to program)
* talk/integration another services (advantage of Spring)
* be possible to deploy at Cloud-Foundary (or smth. similiar)
* be easy to test for the judges (common requierement)
* solve a real problem (personal requierment)

Our first API draft looks like this:

![Image of our API 1](diary/api1.jpg)
![Image of our API 2](diary/api2.jpg)

**But, ** when we start mocking, the first problem hit us, - How are we going to call our baby?

We tought about the goal of the project (finding dates) in combination with the servers we are coupled with, - WhatApp. Since we use *doodle* to organize our LaserTag events (ya, thats we the nerds will joins and we can use tools like this, ...) we combined 1 and 1. *WhatsApp* and Doodle is: Whoodle. Later we recongnized the Whoodle is a dog breed so we renamed it to:

**-- VotesApp :)**

After mocking the java side (where we know our challanges)
we start searching for the *WhatsApp* integration.
Since this is not what the contest is about, we hoped for a java libary
that has a nice API and is easy to use.

The good news was, we found the [yowsup](https://github.com/tgalal/yowsup) api which sounds promising (1,5k stars), the "not so good" news was, it's written in Python and doesn't have an easy to use webservice (by now).

Today we also got [the offical](https://github.com/learning-spring-boot/contest/issues/1) "go" to join the contest in a team, - yeye :)

### Summary:
* We have a clear picture what we like to archive
* We now where to start
* We now there is a *WhatsApp* Libary that can be used over a cli

## 27.12.2014 - There is a contest
Today we saw the contest and immediately kicked-off an e-mail
brainstorming on our mailing list. We ask our friends who like to join the competition and who has ideas which things currently suck and we could try to do better in a small project like this. Within a few emails the idea of an *WhatsApp* Group Voting tool was born. Looking to the contest judge characteristics (sweat and neat, popularity, ...) we saw the chance to join the competition.

### Our identified Painpoint
Every week we organize in a *WhatsApp* group how many people like to join our private soccer event. Thats quite hard because you have to scroll though the history and check who likes to attend.
This screenshot shows an real example (*"Bin dabei"="I'm in", "Ich auch"="Me too"*):

![Image of our Painpoint](diary/painpoint.jpg)

### The Solution:
*When we are done*,
you can inviate a Bot into your *WhatsApp* Group that counts keywords (like "*Bin dabei*" or "*I'm in*") and then posts a summary when it gets asked (like *Status* or *Status In*).

![Image of our Solution](diary/solution.png)

From some local radio stations we know that there are using *WhatsApp* for TrafficJam reports. So there should be an easy way to get this messages (**we thought...**).

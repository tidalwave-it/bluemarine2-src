![Maven Central](https://img.shields.io/maven-central/v/it.tidalwave.bluemarine2/bluemarine2.svg)
[![Build Status](https://img.shields.io/jenkins/s/http/services.tidalwave.it/ci/job/blueMarine2_Build_from_Scratch.svg)](http://services.tidalwave.it/ci/view/blueMarine2)
[![Test Status](https://img.shields.io/jenkins/t/http/services.tidalwave.it/ci/job/blueMarine2.svg)](http://services.tidalwave.it/ci/view/blueMarine2)
[![Coverage](https://img.shields.io/jenkins/c/http/services.tidalwave.it/ci/job/blueMarine2.svg)](http://services.tidalwave.it/ci/view/blueMarine2)

blueMarine II
================================

blueMarine II is a media centre based on a semantic database. It is developed on Java 11 and Java FX. It exposes resources via DLNA and REST;
it can run on a Raspberry PI and integrated with CEC for being operated with a TV remote.

Still at the alpha stage of development, it can be used with some hack to reproduce audio files.

The project website is at [http://bluemarine.tidalwave.it](http://bluemarine.tidalwave.it); the developer website is at
[http://tidalwave.it/projects/bluemarine2](http://tidalwave.it/projects/bluemarine2).

Deliverables
------------

+ A desktop application (full-screen style) for
  [macOS](https://search.maven.org/search?q=g:it.tidalwave.bluemarine2%20AND%20a:bluemarine2-application-javafx-macos%20AND%20l:executable) and
  [Linux](https://search.maven.org/search?q=g:it.tidalwave.bluemarine2%20AND%20a:bluemarine2-application-javafx-linux%20AND%20l:bin) (including the Raspbian PI)
+ A headless service serving resources by means of DLNA and REST for any operating system


Architecture
------------

+ [Publish and Subscribe](https://en.wikipedia.org/wiki/Publish%E2%80%93subscribe_pattern) for module integration
+ [Data Context Interaction (DCI)](https://en.wikipedia.org/wiki/Data,_context_and_interaction) for module design
+ A [semantic repository](https://www.ontotext.com/knowledgehub/fundamentals/semantic-repository) for the internal catalog
+ [REST](https://www.redhat.com/en/topics/api/what-is-a-rest-api) for exposing the music catalog
+ [DLNA](https://www.dlna.org) for exposing the music catalog
+ [CEC](https://en.wikipedia.org/wiki/Consumer_Electronics_Control) for integrating with a consumer remote control
+ [MusicBrainz](https://musicbrainz.org) for music metadata


Technologies
------------

+ [OpenJDK11](https://openjdk.java.net/projects/jdk/11) as language and runtime
+ [JavaFX](https://openjfx.io) for the destkop UI
+ [Jakarta XML Binding (JAXB)](https://eclipse-ee4j.github.io/jaxb-ri/) for XML marshalling from XSD
+ [Spring 5](https://spring.io/projects/spring-framework) for Dependency Injection and REST
+ [RDF4J](https://rdf4j.org/) for the semantic store
+ [Cling](https://github.com/4thline/cling) for DLNA
+ [SLF4J](slf4j.org)/[Logback](http://logback.qos.ch) for logging
+ [Lombok](https://projectlombok.org) for language enhancement
+ [Maven](https://maven.apache.org) as the build tool
+ [jaudiotagger](https://bitbucket.org/ijabz/jaudiotagger) and [mp3agic](https://github.com/mpatric/mp3agic) for audio file metadata
+ [TestNG](https://testng.org) and [Mockito](https://site.mockito.org) for testing


Bootstrapping
-------------

In order to build the project, run from the command line:

```mvn -DskipTests```

The project can be opened and built by a recent version of the NetBeans, Eclipse or Idea IDEs.


Documentation
-------------

More information can be found on the [homepage](http://blueMarine.tidalwave.it) of the project.


Contributing
------------

We accept pull requests via Bitbucket or GitHub.

There are some guidelines which will make applying pull requests easier for us:

* No tabs! Please use spaces for indentation.
* Respect the code style.
* Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source
  ode should be reformatted create a separate PR for this change.
* Provide TestNG tests for your changes and make sure your changes don't break any existing tests by running
```mvn clean test```.

If you plan to contribute on a regular basis, please consider filing a contributor license agreement. Contact us for
 more information


License
-------

Code is released under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt).


Additional Resources
--------------------

* [Tidalwave Homepage](http://tidalwave.it)

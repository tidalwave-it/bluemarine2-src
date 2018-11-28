![Maven Central](https://img.shields.io/maven-central/v/it.tidalwave.bluemarine2/bluemarine2.svg)
[![Build Status](https://img.shields.io/jenkins/s/http/services.tidalwave.it/ci/job/blueMarine2_Build_from_Scratch.svg)](http://services.tidalwave.it/ci/view/blueMarine2)
[![Test Status](https://img.shields.io/jenkins/t/http/services.tidalwave.it/ci/job/blueMarine2.svg)](http://services.tidalwave.it/ci/view/blueMarine2)
[![Coverage](https://img.shields.io/jenkins/c/http/services.tidalwave.it/ci/job/blueMarine2.svg)](http://services.tidalwave.it/ci/view/blueMarine2)

blueMarine II
================================

blueMarine II is a small media centre for the Raspberry PI, based on a semantic database. It is developed on Java 8 and
Java FX.

Still at the early stage of development, it can be used with some hack to reproduce audio files.


Bootstrapping
-------------

In order to build the project, run from the command line:

```mvn -DskipTests```

The project can be opened and built by a recent version of the NetBeans, Eclipse or Idea IDEs.


Documentation
-------------

More information can be found on the [homepage](http://blueMarine.tidalwave.it) of the project.


Where can I get the latest release?
-----------------------------------

You can download source and binaries from the [download page](https://bitbucket.org/tidalwave/bluemarine2-src/src).

Alternatively you can pull it from the central Maven repositories:

```xml
<dependency>
    <groupId>it.tidalwave.bluemarine2<groupId>
    <artifactId>bluemarine2</artifactId>
    <version>-- version --</version>
</dependency>
```


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
* [Project Issue Tracker (Jira)](http://services.tidalwave.it/jira/browse/BMT)
* [Project Continuous Integration (Jenkins)](http://services.tidalwave.it/ci/view/blueMarine2)

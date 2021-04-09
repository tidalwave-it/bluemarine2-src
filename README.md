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

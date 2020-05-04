# Gradle-flavoured Flavours Addon Manager
The Flavour Addon Manager for Gradle (henceforth referred to as *FAM Gradle*, or just *FAM*) lets a user add dependencies
to a Gradle-based project. It operates through the [FAM Cli tool](https://github.com/flavours/cli) and writes configuration
to the file `./app.flavour` and the `./.flavour/addons/` directory.

## Installation
Run `docker build . -t fam-gradle` from the project root to build a local Docker image.

## Usage
The FAM can be used to add, check or remove plugin configurations from a project.
* Checking: `flavour check --addonmanager=fam-gradle --package=<addon-file>`
* Adding: `flavour check --addonmanager=fam-gradle --package=<addon-file>`
* Removing: `flavour check --addonmanager=fam-gradle --package=<addon-file>`

Note that the project configuration files do nothing by themselves. 
[The FAM Gradle plugin](https://github.com/flavours/fam-gradle-plugin) is a reference implementation that allows a
gradle build to pick up these Flavour-specific configuration files and add activate them in a project.

## Specification
Examples of valid (and invalid) addon files can be found in `/src/test/resources/addon`

*Please note that for the time being, only the `install` and `meta` properties of a configuration is used by the reference
plugin.*

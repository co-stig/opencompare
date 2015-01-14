Open Compare
===========

Open Compare is a generic open-source compare tool.

As the name suggests, the tool is designed to compare two instances of some system, e.g. SAP, Oracle, Linux, etc. The procedure is straightforward:

1. Create a “Reference snapshot”
2. Create an “Actual snapshot”
3. Obtain the difference between those two

The snapshot contains the following objects, organized in a tree structure:

* Folder structure (folders and filenames)
* ZIP and JAR structure (parsed recursively like a folder)
* (Java specific) Name-value pairs for .properties files
* (Java specific) Method signatures for .class files
* CRC32 checksums for all other files

This list is extensible -- arbitrary object types and structures are supported by flexible plugin architecture (see below).

# Sample usage scenarios

* New upgrade or migration project. Original installation, configuration and customization of the system were performed by 3rd party consultants, the documentation is incomplete and outdated. Comparison to the out-of-the-box (OOTB) snapshot reveals configuration differences and highlights customizations, allowing to validate and fix the documentation.

* The customer has his own IT or consulting team. When your specialists leave the project, they take a snapshot of the working system, and then attach it to the “frozen” project documentation. Next time your team works with this customer, they compare the current system instance to the documented one and update the documentation.

* Cluster. Two servers in a cluster behave differently for unknown reason. Their snapshots are compared to each other to identify configuration misalignment.

* Issues and tech support. Snapshot of the system instance might be useful when troubleshooting some complex customer cases. Snapshots taken at regular intervals can be compared to each other, helping to identify configuration changes that led to an issue.

* Regular updates. For some of the large projects, new customizations and patches are rolled on to the servers very often. Taking snapshots after each deployment can save time debugging some issues.

* Staging environments and virtual machines. Comparing development and pre-production servers with the actual productive environment results in more reliable and stress-free development.

# Screenshots / User guide

TODO

# Command line

TODO

# Installation

TODO

# Supported platforms

TODO

# Changelog

TODO

# Architecture

TODO

# Extending OC

TODO

# Known bugs and limitations

TODO

# Plans

TODO

# Credits

TODO

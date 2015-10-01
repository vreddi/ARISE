#   ARISE Researcher Profiling System

Extensible information gathering and integrating tool, along with source code of its webpage. This software is to help profile any given researcher in a easy and handy way. Extensibility comes from the fact that users can provide java code, without compiling them, or some other related files in order to incorporate any new information source to this software. User codes are compiled and loaded at run time, and some packages for the users' ease of programming have been provided.

## Dependencies

Module dependencies are listed in */src/lib/requiredLibraries.txt*

 - json-lib
 - xom

Note that module json-lib depends on more modules. They also needs to be present in the library when compiling this code.

## Distribution

A distribution of this software should contain a compiled JAR file, as the main program, and two folders located in the same directory as the JAR:

1. *aspect/*
2. *templates/*

Folder *aspect/* is the default location for locally installed aspects and sources, and *templates/* contains templates for files needed by a source wrapper. Templates are used when users create new source (and thus source wrappers), at run time.

## Structure of aspects and sources

An **aspect**, in the terminology of this software, is a real world category of people/researchers that can be described by data. An example would be publications. If someone is interested in a given researcher, he/she may find all sorts of information online describing the publication aspect of that researcher. An aspect is represented by a schema, which is much like schema in database system.

On the other hand, a **source** is a information source where one can obtain instantiated data/knowledge about an aspect. Under the context of publicatoin aspect, it could be Google Scholar, IEEE Xplore, or some other source of data that describe publication aspect. It is easy to see that source is **implementations** of aspect, in the sense that a source obtains external data and converts it to local schema. In order to accomplish this task, the source needs to know that target schema it is aiming at, so sources should come after aspects.

## Modularzied sharing

//  To be continued here.

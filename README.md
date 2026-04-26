# Zyfera

A context based entity component system written in Java, while providing convenient utility wrapper and stream classes. <br>
All processors are also cross-contextable.

## What is Zyfera

Zyfera is a Java ECS. It constructs entities out of components and has processors to act on them. <br>
For that, it uses a context based system, storing entities, which are just ids, inside of those contexts, also ids. <br>
This enables closed systems in order to prevent entities or layers to interact with each other. <br>
Despite this limitation, entities can switch a context easily. <br>

The processors can also be applied to multiple context and use a validation cache for quick entity component updates.

## Why use Zyfera

Zyfera is easy to use with a static API, marked by a leading z. <br>
Expanding on that, Zyfera also provides utility classes for entities and contexts,
which themselve only interact with the static API, reducing overhead significantyly. <br>
In addition, both of those classes also provide stream classes for chained operations.

All in all, Zyfera is a simple but very powerful ECS with cross-context processors, entity context switching
and a static API.

Furthermore, Zyfera is well documented and the code is easy to read.

## Features

<ul>
  <li> Component access type [SHARED / UNIQUE] </li>
  <li> Entity context switching </li>
  <li> Static base API </li>
  <li> Utility context and entity classes </li>
  <li> Context and entity stream classes </li>
  <li> Processor lazy entity validation caching </li>
  <li> Cross-context processor usage </li>
  <li> Disposable entities and contexts </li>
</ul>

## Getting started

The classes used in this example, are in the project, so you can copy-paste this code.

```
Context context;
Entity entity;

context = Zyfera.zCreateContext();
entity = context.createEntity();

// --- //

ExamplePositionProcessor processor;
ExamplePositionComponent<Integer> component;

processor = new ExamplePositionProcessor();
component = new ExamplePositionComponent<>(A_Component.ACCESS_TYPE.SHARED, 4, 4);

// --- //

context.attach(processor);
entity.attach(component, false);

// --- //

context.update();

// --- //

entity.dispose(); // Is recommended for better error messages, but not required.
context.dispose();
```

This code will produce a console output of "Example component 'component object' with x (4) and y (4)!"

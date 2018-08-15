# NetPaint (JavaFX)
A basic graphics application that supports client-server networking.

Created By: Rajeev Ram (2018)

##  Overview
This graphics application is written with JavaFX and the standard Java I/O Library.  Once the server is started, any number of clients can join. Drawings are encapsulated and transmitted as Serializable PaintObjects. When one client draws to his or her screen, all other client views are updated in real time.

## Tasks (User Stories)

* [X] client can access a area on which to draw different shapes
* [X] client can create one of four types of paint objects
* [X] client can choose colors from a color picker
* [X] paint objects are encapulated and serializable
* [X] a server to support multipel clients can be started
* [X] all client views are updated in real time

<img src='https://imgur.com/wmnGxwd.gif' title='NetPaint GUI Walkthrough' width='' alt='Graphics Editor Network' />

## Structure and Design

For this application, the Model-View-Controller (MVC) architecture is used via client and server input and output sockets with nested while loops. Each Client is its own JavaFX application with a stage, pane, canvas, buttons, etc.

## Code Coverage (JUnit)

There is no code coverage for this project. Extensive system tests were performed for the client-server functionality.

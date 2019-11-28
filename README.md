# sgit

### A Scala-based git-like code source manager
>By Audrey Samson, From 5 to 20 October, 2019

*This project is part of the functional programming course. It uses sbt assembly and Scala.*

### Prerequisites
You should have :
* scala version = 2.13.0
* sbt version = 1.3.2
* java version >= 1.8 

### Installation
* clone this repository
* move to the project directory (```cd sgit```)
* run ```source install.sh```

You can now use sgit commands


### Available sbt commands:

* ```sbt assembly``` : to create a fat JAR of your project with all of its dependencies. It can be executed as a standalone application.
* ```sbt test```: to run the tests.
* ```sbt run```: to run the projects.
* ```sbt compile```: to compile the main source code.

### Commands
#### Create:
* sgit init

#### Local Changes:
* sgit status
* sgit diff
* sgit add <filename/filenames or .>
* sgit commit

#### Commit History:
* sgit log : Show all commits started with newest


#### Branches and Tags
* sgit branch <branch name> : Create a new branch
* sgit branch -av : List all existing branches and tags
* sgit checkout <branch> 
* sgit tag <tag name>


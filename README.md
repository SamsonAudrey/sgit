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
First step : clone this repository

Second step : you have to export the path to the executable sgit, to do that :
* open a terminal and go to the clone directory, in the sgit folder
* add the path : 
```
export PATH="`pwd`/target/scala-2.13:$PATH"
```

Now you can call sgit in your terminal.

>Be careful, if you open a new terminal, you will need to write the export command again to use sgit.



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


import commands.init
import commands.add
import commands.diff

object main extends App {
  println("BEGINING")
  //MainLoop()

  diff.diffBetweenTexts(Seq("ee"), Seq("ee","rr"))

  def MainLoop(): Unit = {

    //GET INPUT
    val userInput = io.StdIn.readLine()
    val userInputList = userInput.split(" ").map(_.trim).toList

    //MATCHING
    userInputList(0) match {
      case "init" => {
        println(init.initDirectory())
        MainLoop()
      }
      case "status" => {
        println(init.initDirectory())
        MainLoop()
      }
      case "add" => {
        if (userInputList.length == 1) {
          println("Which files do you want to add ? ")
          MainLoop()
        }
        else {
          userInputList(1) match {
            case "." => { //add all files

            }
            case "regexp" => {

            }
            case _ => {

              add.addMultipleFiles(userInputList.tail)
            }
          }
          MainLoop()
        }

      }
      case "diff" => {

        println("diff")
        MainLoop()
      }
      case "commit" => {
        println("commit")
        MainLoop()
      }
      case "log" => {
        println("log")
        MainLoop()
      }

      case _ => {
            println("END")
        }
      }
    }
}

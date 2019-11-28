package actions

import actions.commit.{isFirstCommit, recCommitAndMessage}
import tools.{commitTools, printerTools}

object log {

  /**
    * Display all commits started with newest
    */
  def log(): Unit = {
    if (!isFirstCommit) {
      val allCommit = recCommitAndMessage(commitTools.lastCommitHash())
      printerTools.printMessage(formatLog(allCommit))
    }
  }

  /**
    * Display all commits started with newest
    */
  def logP(): Unit = {
    if (!isFirstCommit) {
      val allCommit = recCommitAndMessage(commitTools.lastCommitHash())
      println(allCommit)
      printerTools.printMessage(formatLog(allCommit))
    }
  }

  /**
    * Apply the good format
    * @param log : List[String]
    * @return
    */
  def formatLog(log : List[String]): String = {
    var content = ""
    log.map( l => {
      content += "commit: " +l.split("-").toList(0) + "\n" +
        "date: " + l.split("-").toList(1) + "\n" +
        "message: " + l.split("-").toList(2) + "\n\n"
    })
    content
  }
}

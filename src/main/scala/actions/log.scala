package actions

import java.io.File
import actions.commit.{isFirstCommit, recCommitAndMessage}
import tools.diffTools.formatDiffLine
import tools._
import scala.Console.RESET

object log {

  /**
    * Display all commits started with newest
    */
  def log(): Unit = {
    if (!isFirstCommit) {
      val allCommits = recCommitAndMessage(commitTools.lastCommitHash())
      printerTools.printMessage("\n" + formatLog(allCommits))
    }
  }

  /**
    * Display all commits started with newest
    */
  def logP(): Unit = {
    if (!isFirstCommit) {
      val allCommits = recCommitAndMessage(commitTools.lastCommitHash())
      allCommits.map(c => {
        val index = fileTools.getIndex(allCommits, 0 ,c)
          // PRINT NORMAL LOG
          printerTools.printMessage("\n" + formatLog(List(allCommits(index.get))))

          // PROCESS TO PRINT DIFF
          var head = ""
          var content = ""
          var lines = ""

          val commitHash = c.split("-").toList(0)
          // GET ALL FILES OF THE COMMIT
          val allFiles = repoTools.getAllFilesFromCommit(commitHash)
          // FOREACH FILE, PRINT ITS DIFF
          allFiles.map(f => {
            head = ""
            content = ""
            lines = ""
            val fileHash = f.split(" ").toList(0)
            val commitedFile = new File(repoTools.rootPath + "/.sgit/objects/"+fileHash.slice(0,2) + "/" + fileHash.drop(2))

            if (commitedFile.exists()) {
              val path = f.split(" ").toList(1).split("/")
              val name = path.toList(path.length - 1)
              val parentCommitHash = repoTools.getParentHashCommit(commitHash)
              if (parentCommitHash != "") {
                val previousCommitedFiles = repoTools.getAllFilesFromCommit(parentCommitHash)
                val previousFile = previousCommitedFiles.filter(f => {
                  val fl = f.split(" ").toList(1).split("/")
                  val fileName = fl.toList(path.length - 1)
                  fileName == name
                })
                if (previousFile.nonEmpty) {
                  val hash = previousFile(0).split(" ").toList(0)
                  val previousCommitedFile = new File(repoTools.rootPath + "/.sgit/objects/"+hash.slice(0,2) + "/" + hash.drop(2))
                  val allDiff = diffTools.diff(commitedFile, previousCommitedFile)
                  head += s"diff --git a/$name b/$name \n"+
                    s"--- a/$name\n" +
                    s"+++ b/$name"
                  content += "@@ " + (allDiff(0).index + 1) + "," + allDiff.length + " @@ "
                  allDiff.map(d => lines += formatDiffLine(d) + "\n")
                  // PRINT DIFF
                  printerTools.printMessage(head)
                  printerTools.printColorMessage(Console.CYAN,content)
                  printerTools.printColorMessage(Console.GREEN,lines)
                }
              } else {

                // FOR FIRST COMMIT

                printerTools.printMessage(s"diff --git a/$name b/$name \n"+
                  "new file")
              }
            }
          })
        })
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
      content += s"${RESET}${Console.YELLOW}" + "commit: " +l.split("-").toList(0) + s"${RESET}" + "\n" +
        "date: " + l.split("-").toList(1) + "\n" +
        "message: " + l.split("-").toList(2) + "\n\n"
    })
    content
  }
}

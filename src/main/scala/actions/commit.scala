package actions

import java.io.{File, PrintWriter}
import java.util.Date
import org.apache.commons.io.FileUtils
import tools.{commitTools, fileTools, printerTools, repoTools}

object commit {

  /**
    * Return true if there is not yet a commit done
    * @return
    */
  def isFirstCommit: Boolean = {
    fileTools.getContentFile(repoTools.rootPath + "/.sgit/refs/heads/master") == ""
  }

  /**
    * Save the stage area
    * @param message : String
    */
  def commit(message: String): Unit = {
    var parent = ""
    if (!isFirstCommit) {
      parent = commitTools.lastCommitHash()
    }
    val path = repoTools.rootPath + "/.sgit/objects"
    val changes: List[List[File]] = commitTools.getFilesChanges // val changes : List(updatedFiles, removedFiles, addedFiles)

    if (changes(0).nonEmpty || changes(1).nonEmpty || changes(2).nonEmpty) {
      val content = createContentCommitFile(changes)
      val newCommitHash = fileTools.hash(content + parent.slice(0,4))

      // Create object
      repoTools.createFile(path, newCommitHash, parent, message + "\n" + content)
      // Update ref
      updateRefCommit(newCommitHash)
      // Clean STAGE
      FileUtils.cleanDirectory(new File(repoTools.rootPath + "/.sgit/STAGE"))
      printerTools.printMessage("[" + branch.currentBranch + " "+ newCommitHash.slice(0,7) + "] "+message)
      printerTools.printMessage(changes(2).length + " file changed, 0 insertions(+), 0 deletions(-)")
    }
  }


  /**
    * Change the commit ref from the head branch
    * @param lastCommitHash : String
    */
  def updateRefCommit(lastCommitHash: String): Unit = {
    val branch = fileTools.firstLine(new File(repoTools.rootPath + "/.sgit/HEAD/branch"))
    val path = repoTools.rootPath + "/.sgit/refs/heads/" + branch.get
    val pw = new PrintWriter(new File(path))
    pw.write(lastCommitHash)
    pw.close()
  }

  /**
    * Create the commit content, with all file refs
    * @param changes : List[List[File] ] = List(updatedFiles, removedFiles, addedFiles)
    * @return
    */
  def createContentCommitFile(changes : List[List[File]]): String = {
    var content = ""
    val lastCommit = commitTools.lastCommitHash()
    if (!isFirstCommit) {
      var files = fileTools.getContentFile(repoTools.rootPath + "/.sgit/objects/" + lastCommit)
        .mkString.split("\n").map(_.trim).toList.drop(2) // remove parent commit and message (2 first lines)

      // Updated files
      if (changes(0).nonEmpty ) {
        //remove old
        files = files.filter(f =>
          !changes(0).map(file => fileTools.getContentFile(file.getAbsolutePath).split("\n").map(_.trim).toList(0))
            .contains(f.split(" ").map(_.trim).toList(1)))

        //add new
        changes(0).map(f => content += f.getName // HASH
          + " "
          + fileTools.getContentFile(f.getAbsolutePath).split("\n").map(_.trim).toList(0)// PATH
          + "\n"
        )
      }
      if ( changes(1).nonEmpty) { // removed files
        files = files.filter(f => !changes(1).map(f => f.getAbsolutePath).contains(f.split(" ").map(_.trim).toList(1)))
      }
      // Add parent commit's files
      files.map(f => content += f + "\n")
    }

    if (changes(2).nonEmpty) {
      changes(2).map(f => content += f.getName // HASH
        + " "
        + fileTools.getContentFile(f.getAbsolutePath).split("\n").map(_.trim).toList(0)// PATH
        + "\n"
      )
    }
    content
  }

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
    * Get all commits hash + commit date + commit message
    * @param commitHash : String
    * @return
    */
  def recCommitAndMessage(commitHash: String): List[String] = {
    if (new File(repoTools.rootPath + "/.sgit/objects/" + commitHash).exists()) {
      val file = new File(repoTools.rootPath + "/.sgit/objects/" + commitHash)
      val content = fileTools.getContentFile(file.getAbsolutePath)
        .split("\n")
        .toList
        .map(_.trim)(1) // message line
      val firstLine = fileTools.firstLine(file)
      if (firstLine.get != "") {
        List(commitHash + "-" + new Date(file.lastModified()) + "-" + content.mkString("")) ::: recCommitAndMessage(firstLine.get)
      } else  List(commitHash + "-" + new Date(file.lastModified()) + "-" + content.mkString("") )
    } else List()
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

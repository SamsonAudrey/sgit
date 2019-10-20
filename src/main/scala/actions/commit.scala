package actions

import java.io.{File, PrintWriter}

import org.apache.commons.io.FileUtils
import tools.{commitTools, fileTools, repoTools}

import scala.io.Source

object commit {

  /**
    * Return true if there is not yet a commit done
    * @return
    */
  def isFirstCommit: Boolean = {
    fileTools.getContentFile(repoTools.rootPath + "/.git/refs/heads/master") == ""
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
    val path = repoTools.rootPath + "/.git/objects"
    val changes: List[List[File]] = commitTools.getFilesChanges // val changes : List(updatedFiles, removedFiles, addedFiles)

    if (changes(0).nonEmpty || changes(1).nonEmpty || changes(2).nonEmpty) {
      val content = createContentCommitFile(changes)
      val newCommitHash = fileTools.hash(content + parent.slice(0,4))

      // Create object
      repoTools.createFile(path, newCommitHash, parent, message + "\n" + content)
      // Update ref
      updateRefCommit(newCommitHash)
      // Clean STAGE
      FileUtils.cleanDirectory(new File(repoTools.rootPath + "/.git/STAGE"))
    }
  }


  /**
    * Change the commit ref from the head branch
    * @param lastCommitHash : String
    */
  def updateRefCommit(lastCommitHash: String): Unit = {
    val branch = fileTools.firstLine(new File(repoTools.rootPath + "/.git/HEAD/branch"))
    val path = repoTools.rootPath + "/.git/refs/heads/" + branch.get
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
      var files = fileTools.getContentFile(repoTools.rootPath + "/.git/objects/" + lastCommit)
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
        + Source.fromFile(f.getAbsolutePath).mkString.split("\n").map(_.trim).toList(0)// PATH
        + "\n"
      )
    }
    content
  }
}

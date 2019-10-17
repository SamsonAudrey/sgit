package actions

import java.io.{File, PrintWriter}

import org.apache.commons.io.FileUtils
import tools.{commitTools, fileTools, repoTools}

import scala.io.Source

object commit {

  def commit(): Unit = {
    var parent = ""
    if (!isFirst()) {
      parent = commitTools.lastCommitHash()
    }
    val path = repoTools.currentPath + "sgitRepo/.git/objects"
    val changes = commitTools.getFilesChanges() //changes = List(updatedFiles, removedFiles, addedFiles)
    if (changes(0).nonEmpty || changes(1).nonEmpty || changes(2).nonEmpty) {
      val content = createContentCommitFile(changes)
      val newCommitHash = add.hash(content + parent.slice(0,4))

      //create object
      repoTools.createFile(path, newCommitHash, parent, content)
      //update ref
      updateRefCommit(newCommitHash)
      //clean STAGE
      FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgitRepo/.git/STAGE"))
    } else {
      println(">> Nothing to commit <<")
    }

  }

  def isFirst(): Boolean = {
    val lastCommit = Source.fromFile(repoTools.currentPath + "sgitRepo/.git/refs/heads/master").mkString
    lastCommit == ""
  }

  def updateRefCommit(lastCommitHash: String): Unit = {
    val branch = fileTools.firstLine(new File(repoTools.currentPath + "sgitRepo/.git/HEAD/branch"))
    val path = repoTools.currentPath + "sgitRepo/.git/refs/heads/" + branch.get
    val pw = new PrintWriter(new File(path))
    pw.write(lastCommitHash)
    pw.close
  }

  def createContentCommitFile(changes : List[List[File]]): String = {
    var content = ""
    val lastCommit = commitTools.lastCommitHash()
    if (!isFirst()) {
      var files = Source.fromFile(repoTools.currentPath + "sgitRepo/.git/objects/" + lastCommit)
        .mkString.split("\n").map(_.trim).filter(f => f != "").toList
      if (changes(0).nonEmpty ) {
        // UPDATED
        //remove old
        files = files.filter(f =>
          !changes(0).map(file => Source.fromFile(file.getAbsolutePath).mkString.split("\n").map(_.trim).toList(0))
            .contains(f.split(" ").map(_.trim).toList(1)))

        //add new
        changes(0).map(f => content += f.getName // HASH
          + " "
          + Source.fromFile(f.getAbsolutePath).mkString.split("\n").map(_.trim).toList(0)// PATH
          + "\n"
        )
      }
      if ( changes(1).nonEmpty) { // removed files
        files = files.filter(f => !changes(1).map(f => f.getAbsolutePath).contains(f.split(" ").map(_.trim).toList(1)))
      }
      // Add parent commit's files
      files.foreach(f => content += f + "\n")
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

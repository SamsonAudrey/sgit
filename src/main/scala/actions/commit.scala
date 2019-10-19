package actions

import java.io.{File, PrintWriter}

import org.apache.commons.io.FileUtils
import tools.{commitTools, fileTools, repoTools}

import scala.io.Source

object commit {

  def commit(message: String): Unit = {
    var parent = ""
    if (!isFirstCommit()) {
      parent = commitTools.lastCommitHash()
    }
    val path = repoTools.rootFile + "/.git/objects"
    val changes = commitTools.getFilesChanges() //changes = List(updatedFiles, removedFiles, addedFiles)
    if (changes(0).nonEmpty || changes(1).nonEmpty || changes(2).nonEmpty) {
      val content = createContentCommitFile(changes)
      val newCommitHash = add.hash(content + parent.slice(0,4))

      //create object
      repoTools.createFile(path, newCommitHash, parent, message + "\n" + content)
      //update ref
      updateRefCommit(newCommitHash)
      //clean STAGE
      FileUtils.cleanDirectory(new File(repoTools.rootFile + "/.git/STAGE"))
      println(">> Commit done <<")
    } else {
      println(">> Nothing to commit <<")
    }

  }

  def isFirstCommit(): Boolean = {
    val lastCommit = Source.fromFile(repoTools.rootFile + "/.git/refs/heads/master").mkString
    lastCommit == ""
  }

  def updateRefCommit(lastCommitHash: String): Unit = {
    val branch = fileTools.firstLine(new File(repoTools.rootFile + "/.git/HEAD/branch"))
    val path = repoTools.rootFile + "/.git/refs/heads/" + branch.get
    val pw = new PrintWriter(new File(path))
    pw.write(lastCommitHash)
    pw.close
  }

  def createContentCommitFile(changes : List[List[File]]): String = {
    var content = ""
    val lastCommit = commitTools.lastCommitHash()
    if (!isFirstCommit()) {
      var files = Source.fromFile(repoTools.rootFile + "/.git/objects/" + lastCommit)
        .mkString.split("\n").map(_.trim).toList.drop(2) // remove parent commit and message (2 first lines)
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

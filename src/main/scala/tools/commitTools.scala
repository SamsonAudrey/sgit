package tools

import java.io.File

import actions.commit
import tools.statusTools.isCommited

import scala.io.Source

object commitTools {

  def getFilesChanges(): List[List[File]] = {
    val allFiles = repoTools.getAllStagedFiles()
    val allUpdatedFiles = allFiles.filter(f => isUpdatedFromStageToCommit(f)) // Updated don't care if stage but HAS TO BE UPDATEED
    val allAddedFiles = allFiles.filter(f => !isCommited(f))

    var allFilesLastCommit = getLastCommitFiles()
    val allRemovedFiles = allFilesLastCommit.filter(f => !f.exists())

    List(allUpdatedFiles, allRemovedFiles, allAddedFiles)
  }


  //file de STAGE donc name = hash
  def isUpdatedFromStageToCommit(file: File): Boolean = {
    val path = fileTools.firstLine(file).getOrElse("")
    if (!statusTools.isCommited(file)) false
    else {
      val allCommitedHash = getLastCommitHash()
      !allCommitedHash.contains(file.getName)
    }
  }

  def lastCommitHash(): String = {
    val branch = fileTools.firstLine(new File(repoTools.currentPath + "sgitRepo/.git/HEAD/branch"))
    val path = repoTools.currentPath + "sgitRepo/.git/refs/heads/" + branch.get
    Source.fromFile(new File(path)).mkString
  }

  def lastCommitParentHash() : String = {
    val branch = branchTools.currentBranch()
    val pathLastCommit = repoTools.currentPath + "sgitRepo/.git/refs/heads/" + branch
    val pathParentCommit = repoTools.currentPath + "sgitRepo/.git/objects/"
    if (commit.isFirst() || fileTools.firstLine(new File(pathParentCommit + commitTools.lastCommitHash())) == "") ""
    else {
      val lastCommitHash = Source.fromFile(new File(pathLastCommit)).mkString
      val seqHash = Source.fromFile(pathParentCommit + lastCommitHash).mkString
        .split("\n")
        .toSeq
        .map(_.trim)
      seqHash(0) // first line = parent hash

    }

  }


  def isRemoved(file: File): Boolean = {
    !fileTools.exist(file.getName) && statusTools.wasCommited(file)
  }


  def getLastCommitFiles(): List[File] = {
    if (lastCommitHash() == "") List()
    else {
      repoTools.getAllCommitedFileHash(lastCommitHash()).map(f => new File(f.split(" ").map(_.trim).toList(1)))
    }
  }

  def getLastCommitHash(): List[File] = {
    if (lastCommitHash() == "") List()
    else {
      repoTools.getAllCommitedFileHash(lastCommitHash()).map(f => new File(f.split(" ").map(_.trim).toList(0)))
    }
  }
}

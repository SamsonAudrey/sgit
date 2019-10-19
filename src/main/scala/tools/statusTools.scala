package tools

import java.io.File
import actions.{add, commit}
import scala.io.Source

object statusTools {

  /**
    * Check if the file has been added and is now on the .git/STAGE folder
    * Doesn't matter if its content has been updated
    * @param file : file from User file
    * @return
    */
  def isStaged(file : File): Boolean = {
    if (!fileTools.exist(file.getName)) {
      false
    }
    else {
      val allStagedFiles = repoTools.getAllStagedFiles().map(f => fileTools.firstLine(f).get)
      allStagedFiles.contains(file.getAbsolutePath)
    }
  }

  /**
    * Return true if the file has been stage and the updated form the working directory
    * @param file
    * @return
    */
  def isStagedAndUpdatedContent(file : File): Boolean = {
    val stagedFile = fileTools.getLinkedStagedFile(file)
    if (stagedFile.nonEmpty) {
      Source.fromFile(stagedFile.get).mkString != file.getAbsolutePath + "\n" + Source.fromFile(file).mkString
    } else false
  }

  def isCommitedAndUpdatedContent(file : File): Boolean = {
    val commitedFile = commitTools.getLastCommitFiles()
    val commitedHash = commitTools.getLastCommitHash()
    val stagedHash = repoTools.getAllStagedFiles()

    val hash = add.getFileHash(file)

    val linkedStagedFile = fileTools.getLinkedStagedFile(file)

    if (commitedFile.nonEmpty) {
      var stageUpdate = false
      if (linkedStagedFile.nonEmpty) {
        stageUpdate = commitedFile.contains(Source.fromFile(linkedStagedFile.get).mkString) && !commitedHash.contains(linkedStagedFile.get.getName)
      }
      val freeUpdate = commitedFile.contains(file) && !commitedHash.contains(hash) && !stagedHash.map(f => f.getName).contains(hash)
      freeUpdate || stageUpdate
    } else false
  }

  /**
    * Check if the file is in the last commit
    * @param file
    * @return
    */
  def isCommited(file : File): Boolean = {
    var path =""
    if (commit.isFirstCommit()) false
    else {
      if (file.getAbsolutePath.contains("STAGE")) {
        path = fileTools.firstLine(file).getOrElse(" - ")
      }
      else {
        path = file.getAbsolutePath()
      }
      val allCommitedHash = repoTools.getAllCommitedFileHash(commitTools.lastCommitHash())
        .map(f => f.split(" ").map(_.trim).toList(1)) // get the file's path
      file.exists() && allCommitedHash.contains(path)
    }
  }

  def isCommitedButUpdated(file : File): Boolean = {
    var hash = ""
    if (commit.isFirstCommit() || !isCommited(file)) false
    else {
      if (file.getAbsolutePath.contains("STAGE")) {
        val filepath = fileTools.firstLine(file).getOrElse("")
        hash = add.getFileHash(new File(filepath))
      }
      else {
        hash = add.getFileHash(file)
      }
      val allCommitedHash = repoTools.getAllCommitedFileHash(commitTools.lastCommitHash())
        .map(f => f.split(" ").map(_.trim).toList(0)) // get the file's path

      file.exists() && !allCommitedHash.contains(hash)
    }
  }

  /**
    * Check if the file is in the commit before the last commit
    * @param file
    * @return
    */
  def wasCommited(file : File): Boolean = {
    if (isCommited(file)) true
    else {
      val parents = commitTools.lastCommitParentHash()
      if (parents == "") {
        false
      }
      else {
        val allCommitedHash = repoTools.getAllCommitedFileHash(parents)
          .map(f => f.split(" ").map(_.trim).toList(1)) // get the file's path
        allCommitedHash.contains(file.getAbsolutePath)
      }
    }
  }

  /**
    * Check is the file is free, it means if it has never been staged or commited
    * @param file
    * @return
    */
  def isFree(file: File): Boolean = {
    !isStaged(file) && !isCommited(file)
  }

  /**
    * Check is the file has been deleted from the working directory
    * @param file : file from STAGE
    * @return
    */
  def isRemoveFromStage(file: File): Boolean = {
    val f = fileTools.getLinkedStagedFile(file)
    if (f.isEmpty) true else false
  }


  def isRemoveFromCommit(file: File): Boolean = {
    true
  }

  /**
    * Check if the file has been updated whereas it was staged
    * @param file : file from User files, working directory (not from STAGE)
    * @return
    */
  def hasBeenUpdated(file: File): Boolean = {
    if (!isStaged(file)) {
      false
    } else {

      if (!fileTools.exist(file.getName)) {
        false
      } else {
        var allFiles = List[File]()
        allFiles = repoTools.getAllStagedFiles()
          .filter(f => f.getName == add.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString))
        if (allFiles.isEmpty) true
        else {
          /*val newContent = Source.fromFile(file.getAbsolutePath).mkString
          val nC = newContent.split("\n")
            .toSeq
            .map(_.trim)
            .filter(_ != "")
          val oldContent = Source.fromFile(allFiles(0).getAbsolutePath).mkString
          val oC = oldContent.split("\n")
            .toSeq
            .map(_.trim)
            .filter(x=> x != "" && x != file.getAbsolutePath)*/

          val listDiff = diffTools.diff(file,allFiles(0))
          listDiff.nonEmpty
        }
      }
    }
  }

}

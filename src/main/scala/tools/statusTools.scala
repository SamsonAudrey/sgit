package tools

import java.io.File
import actions.{add, commit}
import scala.io.Source

object statusTools {

  /**
    * Return true if the file has been added and is now on the stage area
    * @param file : File
    * @return
    */
  def isStaged(file : File): Boolean = {
    if (!fileTools.exist(file.getName)) {
      false
    }
    else {
      val allStagedFiles = repoTools.getAllStagedFiles.map(f => fileTools.firstLine(f).get)
      allStagedFiles.contains(file.getAbsolutePath)
    }
  }

  /**
    * Return true if the file has been staged and updated from the working directory
    * @param file : File
    * @return
    */
  def isStagedAndUpdatedContent(file : File): Boolean = {
    if (isStaged(file)) {
      fileTools.getContentFile(fileTools.getLinkedStagedFile(file).get.getAbsolutePath) != file.getAbsolutePath + "\n" + fileTools.getContentFile(file.getAbsolutePath)
    } else false
  }

  def isCommitedAndUpdatedContent(file : File): Boolean = {
    val commitedFile = commitTools.getLastCommitFiles
    if (commitedFile.nonEmpty) {
      val commitedHash = commitTools.getLastCommitFileHashs
      val stagedHash = repoTools.getAllStagedFiles
      val hash = fileTools.getFileHash(file)
      val linkedStagedFile = fileTools.getLinkedStagedFile(file)
      var stageUpdate = false

      if (linkedStagedFile.nonEmpty) {
        stageUpdate = commitedFile.contains(fileTools.getContentFile(linkedStagedFile.get.getAbsolutePath)) && !commitedHash.contains(linkedStagedFile.get.getName)
      }
      val freeUpdate = commitedFile.contains(file) && !commitedHash.contains(hash) && !stagedHash.map(f => f.getName).contains(hash)
      freeUpdate || stageUpdate
    } else false
  }

  /**
    * Return true if the file has been commited in the last commit
    * @param file : File
    * @return
    */
  def isCommited(file : File): Boolean = {
    var path =""
    if (commit.isFirstCommit) false
    else {
      if (file.getAbsolutePath.contains("STAGE")) {
        path = fileTools.firstLine(file).getOrElse(" - ")
      }
      else {
        path = file.getAbsolutePath
      }
      val allCommitedFileNames = repoTools.getAllFilesFromCommit(commitTools.lastCommitHash())
        .map(f => f.split(" ").map(_.trim).toList(1)) // get the// file's path
      file.exists() && allCommitedFileNames.contains(path)
    }
  }

  /**
    * Return true if the file has been commited in the last commit and then updated in the working directory
    * @param file : File
    * @return
    */
  def isCommitedButUpdated(file : File): Boolean = {
    var hash = ""
    if (commit.isFirstCommit || !isCommited(file)) false
    else {
      if (file.getAbsolutePath.contains("STAGE")) {
        val filepath = fileTools.firstLine(file).getOrElse("")
        hash = fileTools.getFileHash(new File(filepath))
      }
      else {
        hash = fileTools.getFileHash(file)
      }
      val allCommitedHash = repoTools.getAllFilesFromCommit(commitTools.lastCommitHash())
        .map(f => f.split(" ").map(_.trim).toList(0)) // get the file's path

      file.exists() && !allCommitedHash.contains(hash)
    }
  }

  /**
    * Return true if the file has been commited in the last commit parent
    * @param file : File
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
        val allCommitedHash = repoTools.getAllFilesFromCommit(parents)
          .map(f => f.split(" ").map(_.trim).toList(1)) // get the file's path
        allCommitedHash.contains(file.getAbsolutePath)
      }
    }
  }

  /**
    * Return true is the file is free, it means if it has never been staged or commited
    * @param file : File
    * @return
    */
  def isFree(file: File): Boolean = {
    !isStaged(file) && !isCommited(file)
  }

  /**
    * Return true is the file has been deleted from the working directory
    * @param file : File
    * @return
    */
  def isRemoveFromStage(file: File): Boolean = {
    val f = fileTools.getLinkedStagedFile(file)
    if (f.isEmpty) true else false
  }


}

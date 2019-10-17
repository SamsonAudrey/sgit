package tools

import java.io.File

import actions.{add, commit}

import scala.io.Source

object statusTools {

  val staged = "STAGED"
  val commited = "COMMITED"
  val free = "FREE"

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

  /**
    * Check if the file is in the last commit
    * @param file
    * @return
    */
  def isCommited(file : File): Boolean = {
    var path =""
    if (commit.isFirst()) false
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
    !isStaged(file) //&& !isCommited(file)
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

  /**
    * Print all the files status
    */
  def showGeneralStatus(): Unit = {
    val list = generalStatus()

    if (list(0).nonEmpty){
      println("Untracked files:\n  (use \"git add <file>...\" to include in what will be committed)")
      list(0).foreach(f => println(f.getName))
    }
    if (list(1).nonEmpty) {
      println("Changes not staged for commit:\n  (use \"git add <file>...\" " +
        "to update what will be committed)")
      list(1).foreach(f => println(f.getName))
    }
    if (list(2).nonEmpty) {
      println("Changes not staged for commit:\n  (use \"git add <file>...\" " +
        "to update what will be committed)")
      list(2).foreach(f => println(f.getName))
    }

    if (list(3).nonEmpty) {
      println("Changes to be committed:")
      list(3).foreach(f => println(f.getName))
    }

    if(list(0).isEmpty && list(1).isEmpty && list(2).isEmpty) {
      println("nothing to commit or add")
    }
  }


  /**
    * Get the list of the different files according to their status
    * @return
    */
  def generalStatus(): List[List[File]] = {
    val allFiles = repoTools.getAllUserFiles()
    val allFreeFiles = allFiles.filter(f => isFree(f))
    val allUpdatedStagedFiles = allFiles.filter(f => isStagedAndUpdatedContent(f))
    val allUpdatedCommitedFiles = List() //allFiles.filter(f => isCommited(f) ) // don't care if stage but HAS TO BE UPDATEED
    val allStagedUnCommitedFiles = List() //allFiles.filter(f => !isCommited(f) && isStaged(f) && !isStagedAndUpdatedContent(f))
    List(allFreeFiles, allUpdatedStagedFiles, allUpdatedCommitedFiles, allStagedUnCommitedFiles)
  }
}

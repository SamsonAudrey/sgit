package actions

import java.io.File

import tools.{repoTools, statusTools}
import tools.statusTools.{isFree, isStagedAndUpdatedContent}

object status {

  /**
    * Print all the files status
    */
  def showGeneralStatus(): Unit = {
    val list = generalStatus()

    if (list(0).nonEmpty){
      println(">> Untracked files:\n  (use \"git add <file>...\" to include in what will be committed)")
      list(0).map(f => println(f.getName))
    }
    if (list(1).nonEmpty) {
      println(">> Changes not staged for commit:\n  (use \"git add <file>...\" " +
        "to update what will be committed)")
      list(1).filter(f => !list(0).contains(f)).map(f => println(f.getName))
    }
    val list2 = list(2).filter(f => !list(0).contains(f) && !list(1).contains(f))
    if (list2.nonEmpty) {
      println(">> Changes not staged for commit:\n  (use \"git add <file>...\" " +
        "to update what will be committed)")
      list2.map(f => println(f.getName))
    }
    val list3 = list(3).filter(f => !list(1).contains(f) && !list(2).contains(f))
    if (list(3).nonEmpty) {
      println(">> Changes to be committed:")
      list3.map(f => println(f.getName))
    }

    if(list(0).isEmpty && list(1).isEmpty && list(2).isEmpty && list(3).isEmpty) {
      println(">> Nothing to commit or add")
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
    val allUpdatedCommitedFiles = allFiles.filter(f => statusTools.isCommited(f) && statusTools.isCommitedAndUpdatedContent(f) ) // don't care if stage but HAS TO BE UPDATEED
    val allStagedUnCommitedFiles = allFiles.filter(f => !statusTools.isCommited(f) && statusTools.isStaged(f) && !isStagedAndUpdatedContent(f))
    List(allFreeFiles, allUpdatedStagedFiles, allUpdatedCommitedFiles, allStagedUnCommitedFiles)
  }
}

package actions

import java.io.File
import java.nio.file.Files

import tools.{fileTools, printerTools, repoTools, statusTools}
import tools.statusTools.{isFree, isStagedAndUpdatedContent}

object status {

  /**
    * Print all the files status
    */
  def showGeneralStatus(): Unit = {
    val list = generalStatus()

    if (list(0).nonEmpty){
      printerTools.printMessage(">> Untracked files:\n  (use \"sgit add <file>...\" to include in what will be committed)")
      list(0).map(f => printerTools.printColorMessage(Console.RED, f.getName))
    }
    val list2 = list(2).filter(f => !list(0).contains(f) && !list(1).contains(f) && !list(3).contains(f))
    val list1 = list(1).filter(f => !list(0).contains(f))

    if (list1.nonEmpty || list2.nonEmpty) {
      printerTools.printMessage(">> Changes not staged for commit:\n  (use \"sgit add <file>...\" " +
        "to update what will be committed)")
      list1.map(f => printerTools.printColorMessage(Console.RED, f.getName))
      if (list2.nonEmpty) {
        list2.map(f => printerTools.printColorMessage(Console.RED, f.getName))
      }
    }
    val list3 = list(3).filter(f => !list(1).contains(f))
    if (list(3).nonEmpty) {
      printerTools.printMessage(">> Changes to be committed:")
      list3.map(f => printerTools.printColorMessage(Console.GREEN, "new file: "+f.getName))
    }

    if(list(0).isEmpty && list(1).isEmpty && list(2).isEmpty && list(3).isEmpty) {
      printerTools.printMessage(">> Nothing to commit or add")
    }
  }


  /**
    * Get the list of the different files according to their status
    * @return
    */
  def generalStatus(): List[List[File]] = {
    val workingDirect = new File(repoTools.currentPath).listFiles.filter(f => f.getName != ".sgit").toList

    if (repoTools.containsOnlyFiles(new File(repoTools.currentPath))) { // ONLY FILES
      val allFiles = workingDirect

      if (allFiles.nonEmpty) {
        // FREE FILES:
        val allFreeFiles = allFiles.filter(f => isFree(f))
        // DIFF BETWEEN WORKING DIRECTORY AND STAGE:
        val allUpdatedStagedFiles = allFiles.filter(f => isStagedAndUpdatedContent(f))
        // DIFF BETWEEN COMMIT AND WORKING AREA:
        val allUpdatedCommitedFiles = allFiles.filter(f => statusTools.isCommited(f) && statusTools.isCommitedButUpdated(f) )
        // STAGE BUT NOT COMMIT:
        val allStagedUnCommitedFiles = allFiles.filter(f => (statusTools.isCommitedButUpdated(f) || !statusTools.isCommited(f)) && statusTools.isStaged(f) && !isStagedAndUpdatedContent(f))

        List(allFreeFiles, allUpdatedStagedFiles, allUpdatedCommitedFiles, allStagedUnCommitedFiles)

      } else {
        List(List(),List(),List(),List())
      }

    } else { // AT LEAST 1 FOLDER
      val allFiles = workingDirect.filter(f => f.isFile)

      val freeFolders = workingDirect.filter(f => f.isDirectory && repoTools.isFreeFolder(f))

      if (allFiles.nonEmpty) {
        // FREE FILES:
        val allFreeFiles = allFiles.filter(f => isFree(f)) ++ freeFolders
        // DIFF BETWEEN WORKING DIRECTORY AND STAGE:
        val allUpdatedStagedFiles = allFiles.filter(f => isStagedAndUpdatedContent(f))
        // DIFF BETWEEN COMMIT AND WORKING AREA:
        val allUpdatedCommitedFiles = allFiles.filter(f => statusTools.isCommited(f) && statusTools.isCommitedButUpdated(f) )
        // STAGE BUT NOT COMMIT:
        val allStagedUnCommitedFiles = allFiles.filter(f => (statusTools.isCommitedButUpdated(f) || !statusTools.isCommited(f)) && statusTools.isStaged(f) && !isStagedAndUpdatedContent(f))

        List(allFreeFiles, allUpdatedStagedFiles, allUpdatedCommitedFiles, allStagedUnCommitedFiles)

      } else {
        List(List(),List(),List(),List())
      }
    }
  }
}

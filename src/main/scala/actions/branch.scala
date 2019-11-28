package actions

import java.io.File
import tools._

object branch {

  /**
    * Create a new branch
    * @param branchName : String
    */
  def newBranch(branchName : String): Unit= {
    val file = new File(repoTools.rootPath + "/.sgit/refs/heads/" + branchName)
    val currentCommit = commitTools.lastCommitHash()
    fileTools.updateFileContent(file, currentCommit)
  }

  /**
    * Get list of all branches
    * @return
    */
  def allBranchesTags(): List[File] = {
    repoTools.getListOfFiles(new File(repoTools.rootPath + "/.sgit/refs/heads/")) ::: repoTools.getListOfFiles(new File(repoTools.rootPath + "/.sgit/refs/tags/"))
  }

  /**
    * Display all the branches
    */
  def showAllBranches(): String = {
    var toPrint = ""
    allBranchesTags().map(b => {
      if (branch.currentBranch() == b.getName) {
        toPrint += "-> "
      }
      if (fileTools.getContentFile(b.getAbsolutePath) != ""){
        val commitMessage = fileTools.getContentFile(repoTools.rootPath + "/.sgit/objects/" + fileTools.getContentFile(b.getAbsolutePath))
        toPrint += b.getName + ", last commit message : " + commitMessage.split("\n").toList.map(_.trim)(1) + "\n"
      } else toPrint += b.getName + "\n"


    })
    toPrint
  }

  /**
    * Rename the current branch
    * @param newName : String
    * @return
    */
  def renameCurrentBranch(newName: String): Boolean = {
    val currentB = currentBranch()
    val path = repoTools.rootPath + "/.sgit/refs/heads/"
    // Rename the file
    if (new File(path + currentB).renameTo(new File(path + newName)) ) {
      // Update the ref
      val pathRef = repoTools.rootPath + "/.sgit/HEAD/branch"
      fileTools.updateFileContent(new File(pathRef), newName)
      true
    } else false
  }

  /**
    * Get current branch
    * @return
    */
  def currentBranch(): String = {
    fileTools.firstLine(new File(repoTools.rootPath + "/.sgit/HEAD/branch")).get
  }

  /**
    * Checkout to the other branch
    * @param branchName : String
    * @return
    */
  def checkoutBranch(branchName: String): Boolean = {
    val allB = repoTools.getListOfFiles(new File(repoTools.rootPath + "/.sgit/refs/heads/")).map(b => b.getName)
    if (allB.contains(branchName)) {

      // change the working directory
      val branchPath = repoTools.rootPath + "/.sgit/refs/heads/" + branchName
      val objPath = repoTools.rootPath + "/.sgit/objects/"
      val commitHash = fileTools.getContentFile(branchPath)
      val hashFileToAdd = repoTools.getAllFilesFromCommit(commitHash).map(f => f.split(" ").map(_.trim).toList(0) )

      val fileToRemove = repoTools.getAllWorkingDirectFiles.filter(!statusTools.isFree(_))

      val fileToAddPath = hashFileToAdd.map(h => {
        new File(objPath + "/" + h.slice(0,2) + "/" + h.drop(2))
      })

      fileToRemove.map(f => f.delete())

      fileToAddPath.map(f => {
        val content = fileTools.getContentFile(f.getAbsolutePath).split("\n")
          .toList
          .map(_.trim)

        fileTools.updateFileContent(new File(fileTools.firstLine(f).getOrElse("")), content.drop(1).mkString("\n"))
      })


      val path = repoTools.rootPath + "/.sgit/HEAD/branch"
      fileTools.updateFileContent(new File(path), branchName)

      fileTools.firstLine(new File(path)).get == branchName

    } else false
  }


}

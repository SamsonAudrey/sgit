package actions

import java.io.{File, PrintWriter}

import tools._

object branch {

  /**
    * Create a new branch
    * @param branchName : String
    */
  def newBranch(branchName : String): Unit= {
    val file = new File(repoTools.rootPath + "/.git/refs/heads/" + branchName)
    val currentCommit = commitTools.lastCommitHash()
    fileTools.updateFileContent(file, currentCommit)
  }

  /**
    * Get list of all branches
    * @return
    */
  def allBranches(): List[File] = {
    repoTools.getListOfFiles(new File(repoTools.rootPath + "/.git/refs/heads/"))
  }

  /**
    * Display all the branches
    */
  def showAllBranches(): String = {
    var toPrint = ""
    allBranches().map(b => {
      toPrint += b.getName
      if (fileTools.getContentFile(b.getAbsolutePath) != ""){
        val commitMessage = fileTools.firstLine(new File(repoTools.rootPath + "/.git/objects/" + fileTools.getContentFile(b.getAbsolutePath))).getOrElse("")
        toPrint += " " + commitMessage + "\n"
      }

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
    val path = repoTools.rootPath + "/.git/refs/heads/"
    // Rename the file
    if (new File(path + currentB).renameTo(new File(path + newName)) ) {
      // Update the ref
      val pathRef = repoTools.rootPath + "/.git/HEAD/branch"
      fileTools.updateFileContent(new File(pathRef), newName)
      true
    } else false
  }

  /**
    * Get current branch
    * @return
    */
  def currentBranch(): String = {
    fileTools.firstLine(new File(repoTools.rootPath + "/.git/HEAD/branch")).get
  }

  /**
    * Checkout to the other branch
    * @param branchName : String
    * @return
    */
  def checkoutBranch(branchName: String): Boolean = {
    val allB = allBranches().map(b => b.getName) //VERIFY if  ID is a  BRANCH
    if (allB.contains(branchName)) {

      // change the working directory
      val fileToRemove = repoTools.getAllWorkingDirectFiles.filter(!statusTools.isFree(_))

      val branchPath = repoTools.rootPath + "/.git/refs/heads/" + branchName
      val objPath = repoTools.rootPath + "/.git/objects/"
      val commitHash = fileTools.getContentFile(branchPath)
      val hashFileToAdd = repoTools.getAllFilesFromCommit(commitHash).map(f => f.split(" ").map(_.trim).toList(0) )

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


      val path = repoTools.rootPath + "/.git/HEAD/branch"
      fileTools.updateFileContent(new File(path), branchName)

      fileTools.firstLine(new File(path)).get == branchName

    } else false // verif TAGS and commit hash
  }


}

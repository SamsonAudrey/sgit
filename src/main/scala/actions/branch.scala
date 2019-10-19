package actions

import java.io.{File, PrintWriter}

import tools._

object branch {

  /**
    * Create a new branch
    * @param branchName : String
    */
  def newBranch(branchName : String): Unit= {
    val pw = new PrintWriter(new File(repoTools.rootPath + "/.git/refs/heads/" + branchName))
    val currentCommit = commitTools.lastCommitHash()
    pw.write(currentCommit)
    pw.close()
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
  def showAllBranches(): Unit = {
    var toPrint = ""
    allBranches().map(b => toPrint += b.getName)
    printerTools.printMessage(toPrint)
  }

  /**
    * Rename the current branch
    * @param newName : String
    * @return
    */
  def renameCurrentBranch(newName: String): Boolean = {
    val currentB = currentBranch()
    val path = repoTools.rootPath + "/.git/refs/heads/"
    new File(path + currentB).renameTo(new File(path + newName)) && checkoutBranch(newName)
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
    val allB = allBranches().map(b => b.getName()) //VERIFY if  ID is a  BRANCH
    if (allB.contains(branchName)) {
      val path = repoTools.rootPath + "/.git/HEAD/branch"
      val pw = new PrintWriter(new File(path))
      pw.write(branchName)
      pw.close()
      fileTools.firstLine(new File(path)).get == branchName

      // change the working directory

    } else false // verif TAGS and commit hash
  }


}

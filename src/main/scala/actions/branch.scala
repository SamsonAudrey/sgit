package actions

import java.io.{File, PrintWriter}

import tools.{branchTools, commitTools, fileTools, repoTools}

object branch {

  def newBranch(branchName : String): Unit= {
    val pw = new PrintWriter(new File(repoTools.currentPath + "sgitRepo/.git/refs/heads/" + branchName))
    val currentCommit = commitTools.lastCommitHash()
    pw.write(currentCommit)
    pw.close
  }

  //sgit branch -av
  def allBranches(): List[File] = {
    repoTools.getListOfFiles(new File(repoTools.currentPath + "sgitRepo/.git/refs/heads/"))
  }

  def showAllBranches(): Unit = {
    val allB = allBranches().map(b => println(b.getName()))
  }

  def renameCurrantBranch(newName: String): Boolean = {
    val currentB = branchTools.currentBranch()
    val path = repoTools.currentPath + "sgitRepo/.git/refs/heads/"
    new File(path + currentB).renameTo(new File(path + newName)) && checkoutBranch(newName)
  }

  def checkoutBranch(branch: String): Boolean = {
    val allB = allBranches().map(b => b.getName()) //VERIFY if  ID is a  BRANCH
    if (allB.contains(branch)) {
      val path = repoTools.currentPath + "sgitRepo/.git/HEAD/branch"
      val pw = new PrintWriter(new File(path))
      pw.write(branch)
      pw.close
      fileTools.firstLine(new File(path)).get == branch

      // change the working directory
      
    } else false // verif TAGS and commit hash
  }


}

package actions

import java.io.{File, PrintWriter}

import tools.{branchTools, commitTools, fileTools, repoTools}

object branch {

  def newBranch(branchName : String): Unit= {
    val pw = new PrintWriter(new File(repoTools.rootFile + "/.git/refs/heads/" + branchName))
    val currentCommit = commitTools.lastCommitHash()
    pw.write(currentCommit)
    pw.close
  }

  //sgit branch -av
  def allBranches(): List[File] = {
    repoTools.getListOfFiles(new File(repoTools.rootFile + "/.git/refs/heads/"))
  }

  def showAllBranches(): Unit = {
    val allB = allBranches().map(b => println(b.getName()))
  }

  def renameCurrantBranch(newName: String): Boolean = {
    val currentB = branchTools.currentBranch()
    val path = repoTools.rootFile + "/.git/refs/heads/"
    new File(path + currentB).renameTo(new File(path + newName)) && checkoutBranch(newName)
  }

  def checkoutBranch(branchName: String): Boolean = {
    val allB = allBranches().map(b => b.getName()) //VERIFY if  ID is a  BRANCH
    if (allB.contains(branchName)) {
      val path = repoTools.rootFile + "/.git/HEAD/branch"
      val pw = new PrintWriter(new File(path))
      pw.write(branchName)
      pw.close
      fileTools.firstLine(new File(path)).get == branchName

      // change the working directory

    } else false // verif TAGS and commit hash
  }


}

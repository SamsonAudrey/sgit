package actions

import java.io.{File, PrintWriter}

import actions.branch.allBranches
import tools.{commitTools, repoTools}

object tag {

  def newTag(tagName : String): Boolean= {
    val allB = allBranches().map(b => b.getName)
    if (allB.contains(tagName)) { false } // Cannot add a tags with same name of a branch
    else {
      val pw = new PrintWriter(new File(repoTools.rootFile + "/.git/refs/tags/" + tagName))
      val currentCommit = commitTools.lastCommitHash()
      pw.write(currentCommit)
      pw.close
      true
    }
  }
}

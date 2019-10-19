package actions

import java.io.{File, PrintWriter}

import actions.branch.allBranches
import tools.{commitTools, fileTools, repoTools}

object tag {

  def newTag(tagName : String): Boolean= {
    val allB = allBranches().map(b => b.getName)
    if (allB.contains(tagName)) { false } // Cannot add a tags with same name of a branch
    else {
      val currentCommit = commitTools.lastCommitHash()
      fileTools.updateFileContent(new File(repoTools.rootPath + "/.git/refs/tags/" + tagName),currentCommit)
      true
    }
  }
}

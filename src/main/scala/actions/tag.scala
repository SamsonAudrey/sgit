package actions

import java.io.File
import actions.branch.allBranchesTags
import tools.{commitTools, fileTools, repoTools}

object tag {

  /**
    * Create a Tag
    * @param tagName
    * @return
    */
  def newTag(tagName : String): Boolean= {
    val allB = allBranchesTags().map(b => b.getName)
    if (allB.contains(tagName)) { false } // Cannot add a tags with same name of a branch
    else {
      val currentCommit = commitTools.lastCommitHash()
      fileTools.updateFileContent(new File(repoTools.rootPath + "/.sgit/refs/tags/" + tagName),currentCommit)
      true
    }
  }
}

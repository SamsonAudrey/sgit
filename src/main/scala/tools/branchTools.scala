package tools

import java.io.File

object branchTools {

  def currentBranch(): String = {
    fileTools.firstLine(new File(repoTools.currentPath + "sgitRepo/.git/HEAD/branch")).get
  }
}

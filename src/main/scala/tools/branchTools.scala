package tools

import java.io.File

object branchTools {

  def currentBranch(): String = {
    fileTools.firstLine(new File(repoTools.rootFile + "/.git/HEAD/branch")).get
  }
}

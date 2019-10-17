package actions

import java.io.{File, PrintWriter}

import tools.repoTools

object tag {

  def newTag(tagName : String):Unit= {
    new PrintWriter(new File(repoTools.currentPath + "sgitRepo/.git/refs/tags/" + tagName))
  }
}

package actions

import tools.{fileTools, repoTools}
import java.io.{File, PrintWriter}

object init {

  /**
    * Create the SGIT repository with all sub-folders
    * @param path : String
    * @return
    */
  def initDirectory(path: String) : Unit = {
    //create new repo sgit
    val sgitPath = path + "/sgit"
    new File(sgitPath).mkdir()

    //create .git folder and sub-folders
    val gitPath = sgitPath+ "/.git"
    repoTools.createDirectory(gitPath)

    List("HEAD", "STAGE", "refs", "objects") //, "config", "description")
      .map(folder => repoTools.createDirectory(gitPath + "/" + folder))
    repoTools.createDirectory(gitPath + "/refs/tags")
    repoTools.createDirectory(gitPath + "/refs/heads")
    new PrintWriter(new File(gitPath + "/refs/heads/master"))
    fileTools.updateFileContent(new File(gitPath + "/HEAD/branch"), "master")
  }

}

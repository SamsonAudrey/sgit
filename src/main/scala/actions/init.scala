package actions

import tools.repoTools
import java.io.{File, PrintWriter}


object init extends App{

  /**
    * Create the SGIT repository with all sub-folders
    * @return
    */
  def initDirectory() : Boolean = {

    val currentPath = repoTools.currentPath //"/Users/audreysamson/Desktop/SGITREPO/"

    //create new repo sgit
    if (new File(currentPath + "sgitRepo").mkdir()) {
      val sgitPath = currentPath+"sgitRepo"

      //create .git folder and sub-folders
      new File(sgitPath+"/.git").mkdir()
      val gitPath = sgitPath+"/.git"
      List("HEAD", "STAGE", "refs", "objects") //, "config", "description")
        .map(folder => new File(gitPath + "/" + folder).mkdir())
      new File(gitPath+"/refs/tags").mkdir()
      new File(gitPath+"/refs/heads").mkdir()
      new PrintWriter(new File(gitPath+"/refs/heads/master"))
      val pw = new PrintWriter(new File(gitPath+"/HEAD/branch"))
      pw.write("master") // DEFAULT
      pw.close()

      println(">> Initialized empty Git repository in" + repoTools.currentPath + "<<")
      true
    } else false
  }

}

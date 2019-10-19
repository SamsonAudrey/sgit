package actions

import tools.repoTools
import java.io.{File, PrintWriter}

import tools.repoTools.recursiveListFolders


object init {

  /**
    * Create the SGIT repository with all sub-folders
    * @return
    */
  def initDirectory(path: String) : Unit = {
    //create new repo sgit
    //if (!recursiveListFolders(new File(path)).toList.contains(new File(path + "/sgit/.git")) && repoTools.getRoot(new File(path)).isEmpty || path == repoTools.currentPath + "RepoTest") { // Some(root)

      // create sgit repo
      val sgitPath = path + "/sgit"
      new File(sgitPath).mkdir()

      //create .git folder and sub-folders
      val gitPath = sgitPath+ "/.git"
      new File(gitPath).mkdir()


      List("HEAD", "STAGE", "refs", "objects") //, "config", "description")
        .map(folder => new File(gitPath + "/" + folder).mkdir())
      new File(gitPath + "/refs/tags").mkdir()
      new File(gitPath + "/refs/heads").mkdir()
      new PrintWriter(new File(gitPath + "/refs/heads/master"))
      val pw = new PrintWriter(new File(gitPath + "/HEAD/branch"))
      pw.write("master") // DEFAULT
      pw.close()

      println(">> Initialized empty Git repository in" + repoTools.currentPath + "<<")
  }

}

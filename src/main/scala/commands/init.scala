package commands
import java.io.File
import java.nio.file.Paths

object init extends App{

  def initDirectory() : Boolean = {
    var path = pathToList(getCurrentPath())
    path = path :+ "sgitRepo" // add the default folder name

    //create new repo sgit
    if (createGitRepo(path)) {

      //create .git folder and sub-folders
      path = path :+ ".git"
      createGitRepo(path)
      val allFolders = List("HEAD", "STAGE", "refs", "objects", "config", "description")
      allFolders.map(folder => createFolder(path, folder))
      initRefsFolder(path)
      println("Initialized empty Git repository in" + getCurrentPath())
      true
    } else false
  }

  def createGitRepo(path: List[String]): Boolean = {
    mkdirs(path)
  }

  def createFolder(path: List[String], folderName: String): Boolean = {
    mkdirs(path :+ folderName)
  }

  def mkdirs(path: List[String]): Boolean = { // return true if path was created
    path.tail.foldLeft(new File(path.head)){(a,b) => a.mkdir; new File(a,b)}.mkdir
  }

  def initRefsFolder(path: List[String]): Boolean = { //TODO : usefull ?
    val refsPath = path :+ "refs" // add the default folder name at last position
      val headsRefPath = refsPath :+ "heads"
      if (mkdirs(headsRefPath)) {
        val tagsRefPath = refsPath :+ "tags"
        mkdirs(tagsRefPath)
      } else false
  }

  def getCurrentPath(): String= {
    Paths.get(".").toAbsolutePath.toString //Get current path were the app is called
  }

  def pathToList(currentPath: String): List[String] = {
    var path = currentPath.split("/").map(_.trim).toList // string to list
    path = path.tail.dropRight(1) // remove first and last item, because first is empty and last is "."
    path = path.updated(0,"/".concat(path(0))) // add "/" to the first one
    path
  }

}

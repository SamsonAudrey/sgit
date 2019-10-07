package commands

import java.io.File
import java.nio.file.Paths

class Init2 {

  def initDirectory() : Boolean = {
    var path = pathToList(getCurrentPath())
    path = path :+ "sgitRepo" // add the default folder name

    //create new repo sgit
    if (createGitRepo(path)) {

      //create .git folder and sub-folders
      path = path :+ ".git"
      createGitRepo(path)
      createHeadFolder(path)
      createRefsFolder(path)
      createObjectsFolder(path)
      createConfigFolder(path)
      createDescriptionFolder(path)

      println("Initialized empty Git repository in" + getCurrentPath())
      true
    } else false
  }

  def createGitRepo(path: List[String]): Boolean = {
    mkdirs(path)
  }

  def mkdirs(path: List[String]): Boolean = { // return true if path was created
    path.tail.foldLeft(new File(path.head)){(a,b) => a.mkdir; new File(a,b)}.mkdir
  }

  def createHeadFolder(path: List[String]): Boolean = {
    val headPath = path :+ "HEAD" // add the default folder name at last position
    mkdirs(headPath)
  }

  def createRefsFolder(path: List[String]): Boolean = {
    val refsPath = path :+ "refs" // add the default folder name at last position
    if (mkdirs(refsPath)) {
      val headsRefPath = refsPath :+ "heads"
      if (mkdirs(headsRefPath)) {
        val tagsRefPath = refsPath :+ "tags"
        mkdirs(tagsRefPath)
      } else false
    } else false
  }


  def createObjectsFolder(path: List[String]): Boolean = {
    val objectsPath = path :+ "objects" // add the default folder name at last position
    mkdirs(objectsPath)
  }

  def createConfigFolder(path: List[String]): Boolean = {
    val configPath = path :+ "config" // add the default folder name at last position
    mkdirs(configPath)
  }

  def createDescriptionFolder(path: List[String]): Boolean = {
    val descriptionPath = path :+ "description" // add the default folder name at last position
    mkdirs(descriptionPath)
  }

  def getCurrentPath(): String= {
    Paths.get(".").toAbsolutePath.toString //Get current path were the app is called
  }

  def pathToList(currentPath: String): List[String] = {
    var path = currentPath.split("/").map(_.trim).toList // string to list
    path = path.drop(1).dropRight(1) // remove first and last item, because first is empty and last is "."
    path = path.updated(0,"/".concat(path(0))) // add "/" to the first one
    path
  }
}

package tools

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.reflect.io.{File => ScalaFile}

object repoTools {

  /**
    * GET Current Path
    * @return
    */
  val currentPath: String= "/Users/audreysamson/Desktop/SGITREPO/"//Paths.get(".").toAbsolutePath.toString.dropRight(1) //Get current path were the app is called }


  /**
    * Create a file
    * @param path
    * @param name
    * @param content
    */
  def createFile(path: String, hash: String, firstLine: String, content: String): Boolean = {
    try {
      val pw = new PrintWriter(new File(path + "/" + hash)) // create the file containing the blob's content
      pw.write(firstLine + "\n" + content)
      pw.close
      true
    } catch {
      case _: Throwable => false
    }
  }

  /**
    * Create a folder
    * @param path
    * @return
    */
  def createDirectory(path: String): Boolean = {
    new File(path).mkdir()
  }

  /**
    * Get all folders and sub-folders of f
    * @param f : f is a folder
    * @return
    */
  def recursiveListFolders(f: File): Array[File] = {
    if (!f.isDirectory) Array()
    else {
      val these = f.listFiles
      these ++ these.filter(_.isDirectory).flatMap(recursiveListFolders(_))
    }
  }

  /**
    * Get all folders and sub-folders of f (except .git folder and .DS_STORE folder)
    * @param f
    * @return
    */
  def recursiveListUserFolders(f: File): Array[File] = {
    if (!f.isDirectory || f.getName == ".git" || f.getName == ".DS_Store") Array()
    else {
      val these = f.listFiles
      these ++ these.filter(_.isDirectory && !(f.getName.charAt(0).toString == ".")).flatMap(recursiveListUserFolders(_))
    }
  }

  /**
    * get list of files into directory dir
    * @param dir : directory
    * @return
    */
  def getListOfFiles(dir: File): List[File] = {
    val d = new File(dir.toString)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  /**
    * Return all the user files (all the files in the working directory)
    * @return
    */
  def getAllUserFiles(): List[File] = {
    val allFolders = recursiveListUserFolders(new File(repoTools.currentPath + "sgitRepo"))
    allFolders.map(f => repoTools.getListOfFiles(f))
    allFolders.toList.filter(_.isFile)
  }

  /**
    * Return all the staged files (all the files in the /.git/STAGE folder)
    * @return
    */
  def getAllStagedFiles(): List[File] = {
    repoTools.getListOfFiles(new File(repoTools.currentPath + "sgitRepo/.git/STAGE"))
  }

  def getAllCommitedFileHash(commitHash: String) : List[String] = {
    val commitContent = Source.fromFile(new File(repoTools.currentPath + "sgitRepo/.git/objects/" + commitHash)).mkString
    var filesHash = commitContent.split("\n")
      .map(_.trim)
      .filter(x => x != "")
      .toList
    if (!fileTools.firstLine(new File(repoTools.currentPath + "sgitRepo/.git/objects/" + commitHash )).contains("")) {
      filesHash = filesHash.drop(1) // first line = commit parent
    }
    filesHash = filesHash.drop(1) // second line = commit message

    filesHash
  }

  /**
    * Delete a directory (and sub-folders/files) if it exists
    * @param directory
    */
  def deleteDirectory(directory: String): Unit = {
    val dir = ScalaFile(directory)
    if (dir.isDirectory && dir.exists) {
      dir.deleteRecursively()
    }
  }
}

package tools

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.reflect.io.{File => ScalaFile}

object repoTools {

  /**
    * GET Current Path
    * @return
    */
  def currentPath: String = {//"/Users/audreysamson/Workspace/cloneSamedi/sgit/"
    val current = new File(".").getCanonicalPath() + "/"
    current
  }

  val rootFile: String = getRoot(new File(currentPath)).getOrElse(new File("")).getAbsolutePath //"/Users/audreysamson/Workspace/cloneSamedi/sgit/RepoTest/sgit"//getRoot(new File(currentPath + "sgit")).getOrElse(new File(currentPath + "sgit")).getAbsolutePath

  def getRoot(directory: File): Option[File] = {

    if (directory.isDirectory) {
      if (directory.listFiles().toList.contains(new File(directory.getAbsolutePath + "/.git")) ||
        directory.listFiles().toList.contains(new File(directory.getAbsolutePath + "/sgit")) &&
        directory.getName == "sgit") {

        if (!new File(directory.getAbsolutePath + "/.git/HEAD/branch").exists()) {
          Some(new File(directory.getAbsolutePath + "/sgit"))
        } else Some(directory)
      } else {
        if (directory.getParentFile == null) {
          None
        } else {
          getRoot(directory.getParentFile)
        }
      }
    } else None

    /*if (new File("/Users/audreysamson/Workspace/cloneSamedi/sgit/sgit/.git").exists()) {
      Some(new File("/Users/audreysamson/Workspace/cloneSamedi/sgit/sgit"))
    } else None*/
  }

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
    val allFolders = recursiveListUserFolders(new File(repoTools.rootFile))
    allFolders.map(f => repoTools.getListOfFiles(f))
    allFolders.toList.filter(f => f.isFile && f.getName != ".DS_Store")
  }

  /**
    * Return all the staged files (all the files in the /.git/STAGE folder)
    * @return
    */
  def getAllStagedFiles(): List[File] = {
    repoTools.getListOfFiles(new File(repoTools.rootFile + "/.git/STAGE"))
  }

  def getAllCommitedFileHash(commitHash: String) : List[String] = {
    val commitContent = Source.fromFile(new File(repoTools.rootFile + "/.git/objects/" + commitHash)).mkString
    var filesHash = commitContent.split("\n")
      .map(_.trim)
      .filter(x => x != "")
      .toList
    if (!fileTools.firstLine(new File(repoTools.rootFile + "/.git/objects/" + commitHash )).contains("")) {
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

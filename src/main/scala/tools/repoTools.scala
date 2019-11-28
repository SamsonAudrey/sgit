package tools

import java.io.{File, PrintWriter}

import actions.init

import scala.reflect.io.{File => ScalaFile}

object repoTools {

  /**
    * GET Current Path
    * @return
    */
  def currentPath: String = new File(".").getCanonicalPath + "/"

  /**
    * Get root path
    * @return
    */
  def rootPath: String = getRoot(new File(currentPath)).getOrElse(new File(".")).getAbsolutePath

  /**
    * Get root file
    * @param directory : File
    * @return
    */
  def getRoot(directory: File): Option[File] = {
    if (directory.isDirectory) {
      if (directory.listFiles().toList.contains(new File(directory.getAbsolutePath + "/.sgit"))) {
        if (!new File(directory.getAbsolutePath + "/.sgit/HEAD/branch").exists()) {
          Some(new File(directory.getAbsolutePath + "/.sgit"))
        } else Some(directory)
      } else {
        if (directory.getParentFile == null) {
          None
        } else {
          getRoot(directory.getParentFile)
        }
      }
    } else None
  }

  /**
    * Create a file
    * @param path : String
    * @param hash : String
    * @param firstLine : String
    * @param content : String
    */
  def createFile(path: String, hash: String, firstLine: String, content: String): Boolean = {
    try {
      val pw = new PrintWriter(new File(path + "/" + hash))
      pw.write(firstLine + "\n" + content)
      pw.close()
      true
    } catch {
      case _: Throwable => false
    }
  }

  /**
    * Create a folder
    * @param path : String
    * @return
    */
  def createDirectory(path: String): Boolean = {
    new File(path).mkdir()
  }

  /**
    * Get all folders and sub-folders of f
    * @param f : File
    * @return
    */
  def recursiveListFolders(f: File): Array[File] = {
    if (!f.isDirectory) Array()
    else {
      val these = f.listFiles.filter(_.isDirectory)
      these ++ these.filter(_.isDirectory).flatMap(recursiveListFolders(_))
    }
  }

  /**
    * Get all folders and sub-folders of f (except .sgit folder)
    * @param f : File
    * @return
    */
  def recursiveListUserFolders(f: File): Array[File] = {
    if (!f.isDirectory || f.getName == ".sgit") Array()
    else {
      val these = f.listFiles
      these ++ these.filter(_.isDirectory).flatMap(recursiveListUserFolders(_))
    }
  }

  /**
    * Get list of files into directory dir
    * @param dir : File
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
    *
    * @param dir
    * @return
    */
  def containsOnlyFiles(dir: File): Boolean = {
    if (!dir.isDirectory) true
    else {
      val allFiles = dir.listFiles.filter(f => f.getName != ".sgit")
      val res = allFiles.filter(f => f.isDirectory)
      res.isEmpty
    }
  }

  // check if the folder contains (at first step) only freefiles
  /**
    *
    * @param dir
    * @return
    */
  def isFreeFolder(dir: File): Boolean = {
    if (!dir.isDirectory) false
    else {
      val allFiles = dir.listFiles.filter(f => f.isFile || f.getName != ".sgit")
      val res = allFiles.filter(f => !statusTools.isFree(f))
      res.isEmpty
    }
  }

  /**
    * Return all the working directory files
    * @return
    */
  def getAllWorkingDirectFiles: List[File] = {
    val allFolders = recursiveListUserFolders(new File(repoTools.rootPath))
    allFolders.map(f => repoTools.getListOfFiles(f))
    allFolders.toList.filter(f => f.isFile)
  }

  def getAllWorkingDirectFolders: List[File] = {
    val allFolders = recursiveListUserFolders(new File(repoTools.rootPath))
    allFolders.map(f => repoTools.getListOfFiles(f))
    allFolders.toList.filter(f => f.isDirectory)
    //allFolders.toList.filter(f => f.isFile && f.getName != ".DS_Store")
  }

  /**
    * Return all the working directory file names
    */
  def getAllWorkingDirectFileNames: List[String] = getAllWorkingDirectFiles.map(f => f.getName)


  /**
    * Return all the staged files
    * @return
    */
  def getAllStagedFiles: List[File] = {
    repoTools.getListOfFiles(new File(repoTools.rootPath + "/.sgit/STAGE"))
  }

  /**
    * Get the files in the commit
    * @param commitHash : String
    * @return
    */
  def getAllFilesFromCommit(commitHash: String) : List[String] = {
    if (commitHash != "") {
      var filesHash = fileTools.getContentFile(repoTools.rootPath + "/.sgit/objects/" + commitHash)
        .split("\n")
        .map(_.trim)
        .filter(x => x != "")
        .toList
      if (!fileTools.firstLine(new File(repoTools.rootPath + "/.sgit/objects/" + commitHash)).contains("")) {
        filesHash = filesHash.drop(1) // first line = commit parent
      }
      filesHash = filesHash.drop(1) // second line = commit message
      filesHash
    } else List()
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

package tools

import java.math.BigInteger
import java.security.{MessageDigest, NoSuchAlgorithmException}
import java.io.{File, FileOutputStream, PrintWriter}

import scala.annotation.tailrec
import scala.io.Source

object fileTools {

  /**
    * Return true if the file exists in the working directory
    * @param fileName : String
    * @return
    */
  def exist(fileName: String): Boolean = {
    val allUserFiles = repoTools.getAllWorkingDirectFiles.map(f => f.getName)
    allUserFiles.contains(fileName)
  }

  /**
    * Return a hash
    * @param str : String
    * @return
    */
  def encryptThisString(str: String): String = try {
    // getInstance() method is called with algorithm SHA-1
    val md = MessageDigest.getInstance("SHA-1")
    // digest() method is called
    // to calculate message digest of the input string
    // returned as array of byte
    val messageDigest = md.digest(str.getBytes)
    // Convert byte array into signum representation
    val no = new BigInteger(1, messageDigest)
    // Convert message digest into hex value
    var hashtext = no.toString(16)
    // Add preceding 0s to make it 32 bit
    while ( {
      hashtext.length < 32
    }) hashtext = "0" + hashtext
    // return the HashText
    hashtext
  } catch {
    case e: NoSuchAlgorithmException =>
      throw new RuntimeException(e)
  } // For specifying wrong message digest algorithms


  /**
    * Add a text line into at the end of the file content
    * @param text : String
    * @param file : String
    */
  def addLineIntoFile(text: String, file: File): Unit = {
    val write = new PrintWriter(new FileOutputStream(file,true))
    write.write("\n"+text)
    write.close()
  }

  /**
    * Return the file in the Working directory, corresponding with the filename given
    * @param fileName : String
    * @return
    */
  def findFile(fileName: String): Option[File] = {
    if (!exist(fileName)) {
      None
    } else {
      val allUserFiles = repoTools.getAllWorkingDirectFiles.filter(f => f.getName == fileName)
      if (allUserFiles.length == 1) {
        Some(allUserFiles(0))
      } else {
        None
      }
    }
  }

  /**
    * Return the first line of the file
    * @param f : File
    * @return
    */
  def firstLine(f: File): Option[String] = {
    val src = io.Source.fromFile(f)
    try {
      src.getLines.find(_ => true)
    } finally {
      src.close()
    }
  }

  /**
    * Get the staged file linked to the working directory file given
    * @param file : File
    * @return
    */
  def getLinkedStagedFile(file : File): Option[File] = {
    if (statusTools.isStaged(file)) {
      val allStagedFiles = repoTools.getAllStagedFiles
      val allStagedFirstLine = allStagedFiles.map(f => fileTools.firstLine(f).get)
      val index = getIndex(allStagedFirstLine, 0, file.getAbsolutePath)
      if (allStagedFiles.nonEmpty && index.nonEmpty) Some(allStagedFiles(index.get))
      else None
    } else None
  }

  /**
    * Get the commited file linked to the working directory file given
    * @param file
    * @return
    */
  def getLinkedCommitFile(file: File): Option[File] = {
    if (statusTools.isCommited(file)) {
      val lastCommitedFiles = commitTools.getLastCommitFiles
      if(lastCommitedFiles.map(f => f.getAbsolutePath).contains(file.getAbsolutePath)) {
        val index = getIndex(lastCommitedFiles.map(f => f.getAbsolutePath), 0, file.getAbsolutePath)
        val hash = commitTools.getLastCommitFileHashs(index.get).getName
        Some(new File(repoTools.rootPath + "/.sgit/objects/"+hash.slice(0,2) + "/" + hash.drop(2)))
      }
      else None
    } else None
  }

  /**
    * Return the position of the toFind string in the list of string
    * @param allStagedFirstLine : List[String]
    * @param index : Int
    * @param toFind : String
    * @return
    */
  @tailrec
  def getIndex(allStagedFirstLine: List[String], index: Int, toFind: String): Option[Int] = {
    if (allStagedFirstLine.isEmpty) None
    if(allStagedFirstLine(0) == toFind) Some(index)
    else {
      getIndex(allStagedFirstLine.tail, index + 1, toFind)
    }
  }

  /**
    * Update the content of the file
    * @param file : File
    * @param newContent : String
    */
  def updateFileContent(file: File, newContent: String): Unit = {
    val fw = new PrintWriter(file)
    try {
      fw.print("")
      fw.write(newContent)
    }
    finally fw.close()
  }

  /**
    * Get the file's content
    * @param path
    * @return
    */
  def getContentFile(path: String): String = {
    Source.fromFile(path).mkString
  }

  /**
    * Hash a String
    * @param str : String to hash
    * @return
    */
  def hash(str: String): String ={
    fileTools.encryptThisString(str)
  }

  /**
    * Get corresponding hash with the file
    * @param file : File
    * @return
    */
  def getFileHash(file: File): String = {
    hash(file.getAbsolutePath + Source.fromFile(file).mkString)
  }
}

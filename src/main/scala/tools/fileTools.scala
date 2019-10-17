package tools

import java.math.BigInteger
import java.security.{MessageDigest, NoSuchAlgorithmException}
import java.io.{File, FileOutputStream, PrintWriter}

object fileTools {

  /**
    * Check if the file exists in the User files
    * @param fileName
    * @return
    */
  def exist(fileName: String): Boolean = {
    val allUserFiles = repoTools.getAllUserFiles().map(f => f.getName).filter(f => f != ".DS_Store")
    allUserFiles.contains(fileName)
  }

  /**
    * Return a hash
    * @param str
    * @return
    */
  def encryptThisString(str: String): String = try { // getInstance() method is called with algorithm SHA-1
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
    * Add a line to a file (at the end)
    * @param text
    * @param file
    */
  def addLineIntoFile(text: String, file: File): Unit = {
    val write = new PrintWriter(new FileOutputStream(file,true))
    write.write("\n"+text)
    write.close()
  }

  /**
    * Return the file in Working directory corresponding with the filename given
    * @param fileName
    * @return
    */
  def findFile(fileName: String): Option[File] = {
    if (!exist(fileName)) {
      None
    } else {
      val allUserFiles = repoTools.getAllUserFiles().filter(f => f.getName == fileName)
      if (allUserFiles.length == 1) {
        Some(allUserFiles(0))
      } else {
        None
      }
    }
  }

  /**
    * Return first line of a file
    * @param f
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
    * Return the staged file linked to the user file given
    * @param file
    * @return
    */
  def getLinkedStagedFile(file : File): Option[File] = {
    if (statusTools.isStaged(file)) {
      val allStagedFiles = repoTools.getAllStagedFiles()
      val allStagedFirstLine = allStagedFiles.map(f => fileTools.firstLine(f).get)
      val index = getIndex(allStagedFirstLine, 0, file.getAbsolutePath)
      if (allStagedFiles.nonEmpty && index.nonEmpty) Some(allStagedFiles(index.get))
      else None
    } else None
  }

  /**
    * Return the position of the toFind string in the list of string
    * @param allStagedFirstLine
    * @param index
    * @param toFind
    * @return
    */
  def getIndex(allStagedFirstLine: List[String], index: Int, toFind: String): Option[Int] = {
    if (allStagedFirstLine.isEmpty) None
    if(allStagedFirstLine(0) == toFind) Some(index)
    else {
      getIndex(allStagedFirstLine.tail, index + 1, toFind)
    }
  }

  /**
    * Update the content of a file
    * @param file
    * @param newContent
    */
  def updateFileContent(file: File, newContent: String): Unit = {
    val fw = new PrintWriter(file)
    try {
      fw.print("")
      fw.write(newContent)
    }
    finally fw.close()
  }
}

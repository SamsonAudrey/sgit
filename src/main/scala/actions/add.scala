package actions

import tools.{fileTools, repoTools, statusTools}
import objects.Blob
import java.io._
import scala.io.Source

object add {

  private val md = java.security.MessageDigest.getInstance("SHA-1")

  /**
    * Add all files which need it to STAGE
    */
  def addAll(): Unit = {
    val list = repoTools.getAllUserFiles()
    val allUserFiles = list.map(f => f.getName).filter(f => f != ".DS_Store")
    addMultipleFiles(allUserFiles)
  }

  /**
    * add all files of the list to STAGE
    * @param filesNameList
    */
  def addMultipleFiles(filesNameList: List[String]): Unit = {
    if (filesNameList.isEmpty) {
      println("Empty")
    } else {
      val file: Option[File] = fileTools.findFile(filesNameList(0))
      if(file.isEmpty) {
        println(">>" + filesNameList(0) +" not found. <<")
      }
      if (file.nonEmpty && statusTools.isStaged(file.get)){
        if (statusTools.isStagedAndUpdatedContent(file.get)){
          updateStagedFile(file.get.getName)
        } else {
          println(">>staged and no updated :" + filesNameList(0) +"<<") // do nothing
        }
      } else if (file.nonEmpty) {
        addAFile(filesNameList(0))
      } else {
        //do nothing
      }
      if (filesNameList.length > 1) {
        addMultipleFiles(filesNameList.tail)
      }
    }
  }

  /**
    * Add one file to STAGE
    * Check if the file exists
    * @param fileName
    */
  def addAFile(fileName: String): Unit= {
    //find the file with fileName
    val optionFile:Option[File] = fileTools.findFile(fileName)
    if(optionFile.isEmpty) {
      println(">>" + fileName +" not found. <<")
    } else {
      val file = optionFile.get
      val path = file.getAbsolutePath
      val contentFile = Source.fromFile(path).mkString
      val fileHash = hash(path + contentFile)
      val newBlob = createBlob(path,fileHash,contentFile) //create new blob
      if (stageABlob(newBlob)) {
        println(">> File " + fileName + " added")
      } else println(">> Not possible <<")// TODO : error
    }
  }

  def updateStagedFile(fileName: String): Unit = {
    val optionFile: Option[File] = fileTools.findFile(fileName)
    if(optionFile.isEmpty) {
      println(">>" + fileName +" not found. <<")
    } else {
      val file = optionFile.get
      val linkedStageFile = fileTools.getLinkedStagedFile(file)
      addAFile(fileName) // create a new blob
      if (linkedStageFile.nonEmpty) {
        val stagePath = repoTools.rootFile + "/.git/STAGE/" + linkedStageFile.get.getName // remove hold blob from STAGE
        new File(stagePath).delete()
      }
    }
  }

  /**
    * Hash a String (file's name)
    * @param str
    * @return
    */
  def hash(str: String): String ={
    fileTools.encryptThisString(str)

  }

  /**
    * Create a blob Object
    * @param fileName
    * @param hash
    * @param content
    * @return
    */
  def createBlob(filePath: String, hash: String, content: String): Blob = {
    val blob = Blob(filePath,hash,content)//create new blob
    blob
  }

  /**
    * Stage a Blob
    * @param blob
    * @return
    */
  def stageABlob(blob: Blob): Boolean = {
    val path = repoTools.rootFile+ "/.git/objects/"+ blob.hash.slice(0,2)

    repoTools.createDirectory(path) // create the directory
    repoTools.createFile(path, blob.hash.drop(2), blob.filePath, blob.content) //create blob file

    //add to .git/STAGE folder
    val stagePath = repoTools.rootFile + "/.git/STAGE"
    repoTools.createFile(stagePath, blob.hash, blob.filePath, blob.content) //create blob file
  }

}

package actions

import tools.{fileTools, repoTools, statusTools}
import objects.Blob
import java.io._

import scala.annotation.tailrec

object add {

  private val md = java.security.MessageDigest.getInstance("SHA-1")

  /**
    * Add all files in the stage area
    */
  def addAll(): Unit = {
    addMultipleFiles(repoTools.getAllWorkingDirectFileNames)
  }

  /**
    * Add all files of the list names to the stage area
    * @param fileNamesList : List[String]
    */
  @tailrec
  def addMultipleFiles(fileNamesList: List[String]): Unit = {
    fileNamesList match {
      case List() => {
        // no files to add
      }
      case _ => {
        val file: Option[File] = fileTools.findFile(fileNamesList(0))
        if (file.nonEmpty && statusTools.isStaged(file.get)){
          if (statusTools.isStagedAndUpdatedContent(file.get)){
            updateStagedFile(file.get.getName)
          }
        } else if (file.nonEmpty) {
          addAFile(fileNamesList(0))
        }
        if (fileNamesList.length > 1) {
          addMultipleFiles(fileNamesList.tail)
        }
      }
    }
  }

  /**
    * Add a file to the stage area
    * Check if the file exists
    * @param fileName : String
    */
  def addAFile(fileName: String): Unit= {
    //find the file with fileName
    val optionFile: Option[File] = fileTools.findFile(fileName)
    optionFile match {
      case None => {
        // no files to add
      }
      case Some(file) => {

        if (statusTools.isStaged(file) && statusTools.isStagedAndUpdatedContent(file)){
          val linkedStageFile = fileTools.getLinkedStagedFile(file)
          val stagePath = repoTools.rootPath + "/.git/STAGE/" + linkedStageFile.get.getName // remove hold file from STAGE
          new File(stagePath).delete()
        }
        val path = file.getAbsolutePath
        val contentFile = fileTools.getContentFile(path)
        val fileHash = fileTools.hash(path + contentFile)
        val newBlob = createBlob(path,fileHash,contentFile)
        stageABlob(newBlob)
      }
    }
  }

  /**
    * Update file already staged.
    * Remove the old version from the stage area, and add the new one
    * @param fileName : String
    */
  def updateStagedFile(fileName: String): Unit = {
    val optionFile: Option[File] = fileTools.findFile(fileName)
    optionFile match {
      case Some(file) => {
        val linkedStageFile = fileTools.getLinkedStagedFile(file)
        addAFile(fileName)
        if (linkedStageFile.nonEmpty) {
          val stagePath = repoTools.rootPath + "/.git/STAGE/" + linkedStageFile.get.getName // remove hold blob from STAGE
          new File(stagePath).delete()
        }
      }
      case None => {
        // no files to add
      }
    }
  }



  /**
    * Create a blob
    * @param filePath : String
    * @param hash : String
    * @param content : String
    * @return
    */
  def createBlob(filePath: String, hash: String, content: String): Blob = {
    Blob(filePath,hash,content)//create new blob
  }

  /**
    * Add the blob to the stage area
    * @param blob : Blob
    * @return
    */
  def stageABlob(blob: Blob): Boolean = {
    val path = repoTools.rootPath+ "/.git/objects/"+ blob.hash.slice(0,2)
    // create the directory
    repoTools.createDirectory(path)
    //create blob file
    repoTools.createFile(path, blob.hash.drop(2), blob.filePath, blob.content)
    //add to the stage area
    val stagePath = repoTools.rootPath + "/.git/STAGE"
    repoTools.createFile(stagePath, blob.hash, blob.filePath, blob.content)
  }

}

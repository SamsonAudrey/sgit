package commands
import objects.Blob_Class
import java.io._

object add {

  private val md = java.security.MessageDigest.getInstance("SHA-1")

  def addAll(): Unit = {
    // TODO
    // get list of all files which have "not staged for commit"
    // addMultipleFiles

  }

  def addMultipleFiles(filesNameList: List[String]): Unit = {
    if (filesNameList.isEmpty) {
      println("Empty")
    } else if (filesNameList.length == 1) {
      addAFile(filesNameList(0))
    } else {
      addAFile(filesNameList(0))
      addMultipleFiles(filesNameList.tail)
    }
  }

  def addAFile(fileName: String): Unit= {
    val content = "content File" // TODO : GET the content of the file
    val fileNameHash = hash(fileName)

    val newBlob = createBlob(fileName,fileNameHash,content) //create new blob
    println("2: " + newBlob.hash)
    if (stageABlob(newBlob)) {
      println("3: " + newBlob.hash)

    } else println("Not possible") // TODO : error
  }

  def hash(content: String): String ={
    new sun.misc.BASE64Encoder().encode(md.digest(content.getBytes))
  }

  def createBlob(fileName: String, hash: String, content: String): Blob_Class = {
    val blob = new Blob_Class(fileName,hash,content)//create new blob
    println("1: " +blob.hash)
    blob
  }

  def stageABlob(blob: Blob_Class): Boolean = {
    val path = init.getCurrentPath().dropRight(1) + "sgitRepo" + "/.git" + "/objects" + "/"+ blob.hash.slice(0,2)
    println(path)
    init.mkdirs(init.pathToList(init.getCurrentPath()) :+ "sgitRepo" :+ ".git" :+ "objects" :+ blob.hash.slice(0,2)) // create the directory

    val pw = new PrintWriter(new File(path + "/" + blob.hash.drop(2) ))
    pw.write(blob.content)
    pw.close

    true

  }

}

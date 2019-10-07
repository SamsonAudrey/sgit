package objects

object blob extends App{

  private var fileNameHash = "" // = ID
  private var content = ""
  private var fileName = ""

  def addFileRef(refFileName: String, hash: String, fileContent: String): Unit ={
    fileName = refFileName
    fileNameHash = hash
    content = fileContent
  }



}

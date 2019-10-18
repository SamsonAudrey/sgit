package objects

case class Blob(var filePath: String, var hash: String, var content: String) {

  object Blob_Class {
    def create(filePath: String, hash: String, content: String) = Blob(filePath, hash, content)
  }


}

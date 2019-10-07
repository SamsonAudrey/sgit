package objects

case class Blob_Class(var fileName: String, var hash: String, var content: String) {

  object Blob_Class {
    def create(fileName: String, hash: String, content: String) = new Blob_Class(fileName, hash, content)
  }


}

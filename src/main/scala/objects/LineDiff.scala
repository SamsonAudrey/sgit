package objects

case class LineDiff(ope: String, index: Int, content: String) {

  object diff {
    def create(ope: String, index: Int, content: String) = LineDiff(ope,index,content)
  }
}

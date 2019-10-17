package objects

case class Commit(var hash: String, var tree: String, var parent: String, var message: String) {

  object commit {
    def create(hash: String, tree: String, parent: String, message: String) = Commit(hash, tree, parent, message)
  }

}

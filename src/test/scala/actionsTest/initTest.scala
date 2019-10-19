package actionsTest

import java.io.File

import org.scalatest._
import _root_.tools.repoTools
import actions.init
import org.apache.commons.io.FileUtils


class initTest  extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  before{
    new File(repoTools.currentPath + "RepoTest").mkdir()
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "RepoTest"))
    init.initDirectory(repoTools.currentPath + "RepoTest")
  }

  describe("If you run init") {
    describe("and there is not yet a sgit repository ") {
      it("it should return true and create a directory sgitRepo.") {
        init.initDirectory(repoTools.currentPath + "RepoTest/sgit")
        val existHEAD = new File(repoTools.currentPath + "RepoTest/sgit/.git/HEAD").exists()
        val existSTAGE = new File(repoTools.currentPath + "RepoTest/sgit/.git/STAGE").exists()
        val existObjects = new File(repoTools.currentPath + "RepoTest/sgit/.git/objects").exists()
        val existRef = new File(repoTools.currentPath + "RepoTest/sgit/.git/refs").exists()
        //val existDescription= new File(repoTools.currentPath + "sgitREPO/.git/description").exists()
        //val existConfig = new File(repoTools.currentPath + "sgitREPO/.git/config").exists()
        val allExist = existHEAD && existSTAGE && existObjects && existRef // && existDescription && existConfig
        assert( allExist === true)
      }
    }

    describe("and there is already a sgit repository ") {
      it("it should return false and not create a directory sgitRepo.") {
        val path = "/Users/audreysamson/Workspace/RepoTest2"
        new File(path).mkdir()

        init.initDirectory(path) // first init
        var sndInit = init.initDirectory(path) //second init

        FileUtils.cleanDirectory(new File(path))
        new File(path).delete()

        assert(sndInit === false)
      }
    }
  }
}

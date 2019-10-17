package actionsTest

import java.io.File

import org.scalatest._
import _root_.tools.repoTools
import actions.init
import org.apache.commons.io.FileUtils


class initTest  extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  before{
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgitRepo"))
    new File(repoTools.currentPath + "sgitRepo").delete()
  }

  describe("If you run init") {
    describe("and there is not yet a sgit repository ") {
      it("it should return true and create a directory sgitRepo.") {
        init.initDirectory()
        val existHEAD = new File(repoTools.currentPath + "sgitREPO/.git/HEAD").exists()
        val existSTAGE = new File(repoTools.currentPath + "sgitREPO/.git/STAGE").exists()
        val existObjects = new File(repoTools.currentPath + "sgitREPO/.git/objects").exists()
        val existRef = new File(repoTools.currentPath + "sgitREPO/.git/refs").exists()
        //val existDescription= new File(repoTools.currentPath + "sgitREPO/.git/description").exists()
        //val existConfig = new File(repoTools.currentPath + "sgitREPO/.git/config").exists()
        val allExist = existHEAD && existSTAGE && existObjects && existRef // && existDescription && existConfig
        assert( allExist === true)
      }
    }

    describe("and there is already a sgit repository ") {
      it("it should return false and not create a directory sgitRepo.") {
        init.initDirectory() // first init
        var sndInit = init.initDirectory() //second init
        assert(sndInit === false)
      }
    }
  }
}

package actionsTest

import java.io.File
import org.scalatest._
import _root_.tools.repoTools
import actions.init
import org.apache.commons.io.FileUtils


class initTest  extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  before{
    new File(repoTools.currentPath + "/sgit").mkdir() //TestReop
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
  }

  describe("If you run init") {
    describe("and there is not yet a sgit repository ") {
      it("it should return true and create a directory sgitRepo.") {
        init.initDirectory(repoTools.currentPath)

        val existHEAD = new File(repoTools.currentPath + "/sgit/.git/HEAD/branch").exists()
        val existSTAGE = new File(repoTools.currentPath + "/sgit/.git/STAGE").exists()
        val existObjects = new File(repoTools.currentPath + "/sgit/.git/objects").exists()
        val existRef = new File(repoTools.currentPath + "/sgit/.git/refs").exists()

        val allExist = existHEAD && existSTAGE && existObjects && existRef

        assert( allExist === true)
      }
    }
  }
}

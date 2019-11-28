package actionsTest

import java.io.File
import org.scalatest._
import _root_.tools.repoTools
import actions.init
import org.apache.commons.io.FileUtils


class initTest  extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {
  val pathTest = repoTools.currentPath + "sgit"

  before{
    new File(pathTest).mkdir() //TestReop
    FileUtils.cleanDirectory(new File(pathTest))
  }

  after {
    FileUtils.cleanDirectory(new File(pathTest + "/.sgit"))
    new File(pathTest + "/.sgit").delete()
  }

  describe("If you run init") {
    describe("and there is not yet a .sgit repository ") {
      it("it should return true and create a directory sgitRepo.") {
        init.initDirectory(pathTest)

        val existHEAD = new File(pathTest + "/.sgit/HEAD/branch").exists()
        val existSTAGE = new File(pathTest + "/.sgit/STAGE").exists()
        val existObjects = new File(pathTest + "/.sgit/objects").exists()
        val existRef = new File(pathTest + "/.sgit/refs").exists()

        val allExist = existHEAD && existSTAGE && existObjects && existRef

        assert( allExist === true)
      }
    }
  }
}

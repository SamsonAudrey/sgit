package toolsTest

import java.io.File

import actions.init
import objects.Blob
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.repoTools

class blobTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgitRepo"))
    new File(repoTools.currentPath + "sgitRepo").delete()
    init.initDirectory()
  }

  describe("If you create a blob") {
    describe("with empty String params") {
      it("it should create it ") {
        val blob = Blob("","","")
        assert(blob.hash === "" && blob.filePath === "")
      }
    }

    describe("with all arguments") {
      it("it should create it ") {
        val blob2 = Blob("a","b","c")
        assert(blob2.hash === "b" && blob2.filePath === "a")
      }
    }
  }
}

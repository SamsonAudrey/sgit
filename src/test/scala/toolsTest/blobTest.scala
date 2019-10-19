package toolsTest

import java.io.File

import actions.init
import objects.Blob
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{fileTools, repoTools}

class blobTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    new File(repoTools.currentPath + "RepoTest").mkdir()
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "RepoTest"))
    init.initDirectory(repoTools.currentPath + "RepoTest")
  }

  /*describe("Test ultime") {
    it("it should be true ") {
      assert(repoTools.currentPath  === "")
    }
  }*/

  describe("If you create a blob") {
    describe("with empty String params") {
      it("it should create it ") {
        val blob = objects.Blob("","","")
        assert(blob.hash === "" && blob.filePath === "")
      }
    }

    describe("with all arguments") {
      it("it should create it ") {
        val blob2 = objects.Blob("a","b","c")
        assert(blob2.hash === "b" && blob2.filePath === "a")
      }
    }
  }
}

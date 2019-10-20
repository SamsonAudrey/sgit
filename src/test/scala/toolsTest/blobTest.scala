package toolsTest

import objects.Blob
import java.io.File
import actions.init
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.repoTools

class blobTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    new File(repoTools.currentPath + "/sgit").mkdir() //TestRepo
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
    init.initDirectory(repoTools.currentPath)
  }

  describe("If you create a blob") {
    describe("with empty String params") {
      it("it should create it ") {
        
      }
    }

    describe("with all arguments") {
      it("it should create it ") {
       
      }
    }
  }
}

package actionsTest

import java.io.{File, PrintWriter}
import actions._
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{commitTools, fileTools, repoTools}

class tagTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
  val pathTest = repoTools.currentPath + "sgit"
  val currentPath = repoTools.currentPath

  before{
    new File(pathTest).mkdir() //TestReop
    FileUtils.cleanDirectory(new File(pathTest))
    init.initDirectory(currentPath)
  }

  after {
    FileUtils.cleanDirectory(new File(currentPath + ".sgit"))
    new File(currentPath + ".sgit").delete()
  }

  describe("If you add a tag") {
    it("it should create a file and point to the last commit of the current branch") {
      new PrintWriter(new File(pathTest + "/testFreeFile1.txt"))
      add.addAFile("testFreeFile1.txt")
      commit.commit("message")

      val tagName = "newTag"
      tag.newTag(tagName)

      val fileTag = new File(currentPath + "/.sgit/refs/tags/" + tagName)

      val exist = fileTag.exists()
      val firstLine = fileTools.firstLine(fileTag).getOrElse("")

      assert(exist && firstLine == commitTools.lastCommitHash())
    }
  }

  describe("If you add a tag with the same name of a branch") {
    it("it should not do anything ") {
      new PrintWriter(new File(pathTest + "/file.txt"))
      add.addAFile("testFreeFile1.txt")
      commit.commit("message")

      branch.newBranch("sameName")
      val res = tag.newTag("sameName") // should be false
      val fileTag = new File(currentPath + "/.sgit/refs/tags/" + "sameName")
      val exist = fileTag.exists()

      assert(!exist && !res)
    }
  }
}

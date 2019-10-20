package actionsTest

import java.io.{File, PrintWriter}

import actions._
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{commitTools, fileTools, repoTools}

class tagTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    new File(repoTools.currentPath + "/sgit").mkdir()
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
    init.initDirectory(repoTools.currentPath)
  }

  describe("If you add a tag") {
    it("it should create a file and point to the last commit of the current branch") {
      new PrintWriter(new File(repoTools.rootPath + "/testFreeFile1.txt"))
      add.addAll()
      commit.commit("message")
      val tagName = "newTag"
      tag.newTag(tagName)
      val fileTag = new File(repoTools.rootPath + "/.git/refs/tags/" + tagName)
      val exist = fileTag.exists()
      val ff = fileTools.firstLine(fileTag).get
      assert(exist && ff == commitTools.lastCommitHash())
    }
  }

  describe("If you add a tag with the same nam of a branch") {
    it("it should not do anything ") {
      new PrintWriter(new File(repoTools.rootPath + "/file.txt"))
      add.addAll()
      commit.commit("message")

      branch.newBranch("sameName")
      val res = tag.newTag("sameName") // should be false
      val fileTag = new File(repoTools.rootPath + "/.git/refs/tags/" + "sameName")
      val exist = fileTag.exists()

      assert(!exist && !res)
    }
  }
}

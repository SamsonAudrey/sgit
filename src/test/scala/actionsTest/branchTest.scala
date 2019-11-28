package actionsTest

import java.io.File

import actions.{add, branch, commit, init}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{fileTools, repoTools, statusTools}

class branchTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
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

  describe("If you create a new branch") {
    it("it should be create in the add in the branch directory") {
      val branchName = "newBranch"
      branch.newBranch(branchName)
      val exists = new File(currentPath + "/.sgit/refs/heads/" + branchName).exists()

      assert(exists)
    }
  }

  describe("If you create multiple branches") {
    it("it should be create in the branch directory") {
      val branchName1 = "branch1"
      val branchName2 = "branch2"
      val branchName3 = "branch3"
      branch.newBranch(branchName1)
      branch.newBranch(branchName2)
      branch.newBranch(branchName3)

      val allBranches = branch.allBranchesTags()

      assert(allBranches.length == 4) // 3 new branches + the master (default branch)
    }
  }

  /*describe("If you checkout to a branch") {
    it("the HEAD file should point to it") {
      val branchName = "branch"
      branch.newBranch(branchName)
      branch.checkoutBranch(branchName)

      val currentBranch = branch.currentBranch()

      assert(currentBranch == "branch")
    }
  }

  describe("If you rename a branch") {
    it("it should rename it without deleting content") {
      val branchName1 = "branch1"
      branch.newBranch(branchName1)
      branch.checkoutBranch(branchName1)

      branch.renameCurrentBranch("renamedBranch")

      val currentBranch = branch.currentBranch()

      assert(currentBranch == "renamedBranch")
    }
  }

  describe("If you checkout to a branch") {
    it("it should change your working directory") {
      repoTools.createDirectory(pathTest + "/test")

      val f1 = new File(pathTest + "/test/f1")
      fileTools.updateFileContent(f1, "first version")

      val f3 = new File(pathTest + "/test/f3")
      fileTools.updateFileContent(f3, "commit file")

      add.addAll()
      commit.commit("message")

      val branchName = "branch"
      branch.newBranch(branchName)

      val f2 = new File(pathTest + "/f2")
      fileTools.updateFileContent(f2, "free file")

      val f4 = new File(pathTest + "/f4")
      fileTools.updateFileContent(f4, "add file")

      add.addAFile(f4.getName)

      commit.commit("message")

      fileTools.updateFileContent(f1, "second version")

      branch.checkoutBranch(branchName)

      assert(f2.exists() && f3.exists() && !f4.exists() && fileTools.getContentFile(f1.getAbsolutePath) == "first version")
    }
  }*/


}

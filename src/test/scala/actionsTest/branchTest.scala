package actionsTest

import java.io.{File, PrintWriter}

import actions.{add, branch, commit, init}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{branchTools, commitTools, repoTools}

class branchTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    new File(repoTools.currentPath + "/sgit").mkdir()
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
    init.initDirectory(repoTools.currentPath)
  }

  describe("If you create a new branch") {
    it("it should be create in the .git/refs/heads directory") {
      val branchName = "newBranch"
      branch.newBranch(branchName)
      val exists = new File(repoTools.rootFile + "/.git/refs/heads/" + branchName).exists()
      assert(exists)
    }
  }

  describe("If you create multiple branches") {
    it("it should be create in the .git/refs/heads directory") {
      val branchName1 = "branch1"
      val branchName2 = "branch2"
      val branchName3 = "branch3"
      branch.newBranch(branchName1)
      branch.newBranch(branchName2)
      branch.newBranch(branchName3)
      val allBranches = branch.allBranches()
      branch.showAllBranches()
      assert(allBranches.length == 4) // 3 new branches and the master
    }
  }

  describe("If you checkout to a branch") {
    it("the HEAD file should point to it") {
      val branchName = "branch"
      branch.newBranch(branchName)
      branch.checkoutBranch(branchName)
      val currentBranch = branchTools.currentBranch()
      assert(currentBranch == "branch")
    }
  }

  describe("If you rename a branch") {
    it("it should rename it without deleting content") {
      val branchName1 = "branch1"
      branch.newBranch(branchName1)
      branch.checkoutBranch(branchName1)
      branch.renameCurrantBranch("renamedBranch")
      val currentBranch = branchTools.currentBranch()
      branch.showAllBranches()
      assert(currentBranch == "renamedBranch")
    }
  }



}

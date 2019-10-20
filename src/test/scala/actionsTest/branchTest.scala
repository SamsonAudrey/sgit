package actionsTest

import java.io.{File, PrintWriter}

import actions.{add, branch, commit, init}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{commitTools, fileTools, repoTools, statusTools}

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
      val exists = new File(repoTools.rootPath + "/.git/refs/heads/" + branchName).exists()
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
      val f3 = new File(repoTools.rootPath + "/f3")
      fileTools.updateFileContent(f3, "commit file")
      add.addAll()
      commit.commit("message")
      branch.newBranch(branchName3)
      val allBranches = branch.allBranches()
      println("///////////////////")
      println(branch.showAllBranches())
      assert(allBranches.length == 4) // 3 new branches + the master
    }
  }

  describe("If you checkout to a branch") {
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
      branch.showAllBranches()
      assert(currentBranch == "renamedBranch")
    }
  }

  describe("If you checkout to a branch") {
    it("it should change your working directory") {
      repoTools.createDirectory(repoTools.rootPath + "/test")
      val f1 = new File(repoTools.rootPath + "/test/f1")
      fileTools.updateFileContent(f1, "first version")
      val f3 = new File(repoTools.rootPath + "/test/f3")
      fileTools.updateFileContent(f3, "commit file")
      add.addAll()
      commit.commit("message")

      val branchName = "branch"
      branch.newBranch(branchName)

      val f2 = new File(repoTools.rootPath + "/f2")
      fileTools.updateFileContent(f2, "free file")
      val f4 = new File(repoTools.rootPath + "/f4")
      fileTools.updateFileContent(f4, "add file")
      add.addAFile(f4.getName)
      val s = statusTools.isStaged(f4)
      commit.commit("message")
      val c = statusTools.isCommited(f4)

      fileTools.updateFileContent(f1, "second version")

      branch.checkoutBranch(branchName)

      assert(f2.exists() && f3.exists() && s  && !f4.exists() && fileTools.getContentFile(f1.getAbsolutePath) == "first version")
    }
  }



}

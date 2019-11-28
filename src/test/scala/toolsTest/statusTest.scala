package toolsTest

import java.io.{File, FileWriter, PrintWriter}
import actions.{add, commit, init, status}
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{fileTools, repoTools, statusTools}
import org.apache.commons.io.FileUtils
import tools.statusTools.isStagedAndUpdatedContent


class statusTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
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

  describe("If you add a file") {
    it("it should be a staged file") {
      val path = pathTest + "/testStatusFile.txt"
      val content = "Test status function"
      val file = new File(path)
      fileTools.updateFileContent(file,content)
      add.addAFile("testStatusFile.txt")

      assert(statusTools.isStaged(new File(path)))
    }
  }

/*
  describe("If you stage a file") {
    describe("and then you modify his content") {
      it("it should be an updated file") {
        val path = pathTest + "/testModifiedFile.txt"
        val file = new File(path)
        fileTools.updateFileContent(file,"Line 1")
        add.addAFile("testModifiedFile.txt")

        fileTools.updateFileContent(file,"New content")

        assert(statusTools.isStagedAndUpdatedContent(file))
      }
    }

    describe("and you don't modify his content") {
      it("it should be an non updated file") {
        val file = new File(pathTest + "/testUnmodifiedFile.txt")
        fileTools.updateFileContent(file,"Hello Test")
        add.addAFile("testModifiedFile.txt")

        assert(!statusTools.isStagedAndUpdatedContent(file))
      }
    }
  }

  describe("If you ask for general status ") {
    it("it should return a list of files depending of their status") {
      val path = pathTest + "/testStatus_updatedCommit.txt"
      val file = new File(path)
      fileTools.updateFileContent(file,"File 1 Line 1")
      add.addAFile("testStatus_updatedCommit.txt")

      commit.commit("message")

      fileTools.addLineIntoFile("Line 2", file)

      val path2 = pathTest + "/testStatus_updatedStage.txt"
      val file2 = new File(path2)
      fileTools.updateFileContent(file2,"File 2 Line 1")
      add.addAFile("testStatus_updatedStage.txt")

      fileTools.addLineIntoFile("Line 2", file2)

      val pathFree = pathTest + "/testStatus_free.txt"
      new PrintWriter(new File(pathFree))

      val genStatus = status.generalStatus()
      val listFree = genStatus(0)
      val listUpdatedStaged= genStatus(1)
      val listUpdatedCommit = genStatus(2)
      val listUnCommited = genStatus(3)

      status.showGeneralStatus()

      assert(listFree.length == 1 && listFree(0) == new File(pathFree)
        && listUpdatedStaged.length == 1 && listUpdatedStaged(0) == file2
        && listUpdatedCommit.nonEmpty && listUnCommited.isEmpty)
    }
  }

  describe("If you commit a file") {
    describe("and then you modify his content and add it") {
      it("it should be an staged file not yet commited") {
        val path = pathTest + "/testCommitStatus.txt"
        val file = new File(path)
        fileTools.updateFileContent(file,"Line 1")
        add.addAFile("testCommitStatus.txt")

        val allStagedFiles = repoTools.getAllStagedFiles

        commit.commit("message")

        fileTools.updateFileContent(file,"Line1 \nLine 2 \nLine3")
        add.addAFile("testCommitStatus.txt")

        assert(statusTools.isCommited(file) && statusTools.isCommitedButUpdated(file) && statusTools.isStaged(file) && !isStagedAndUpdatedContent(file))
      }
    }
  }*/
}

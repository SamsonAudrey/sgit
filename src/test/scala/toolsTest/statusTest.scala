package toolsTest

import java.io.{File, FileWriter, PrintWriter}

import actions.{add, commit, init, status}
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{fileTools, repoTools, statusTools}
import org.apache.commons.io.FileUtils
import tools.statusTools.isStagedAndUpdatedContent

import scala.io.Source


class statusTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{


  before{
    new File(repoTools.currentPath + "/sgit").mkdir()
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
    init.initDirectory(repoTools.currentPath)
  }

  describe("If you add a file") {
    it("it should be a staged file") {
      val path = repoTools.rootPath + "/testStatusFile.txt"
      val content = "Test status function"

      val pw = new PrintWriter(new File(path))
      pw.write(content)
      pw.close
      add.addAFile("testStatusFile.txt")

      assert(statusTools.isStaged(new File(path)))
    }
  }

  describe("If you stage a file") {
    describe("and then you modify his content") {
      it("it should be an updated file") {
        val path = repoTools.rootPath + "/testModifiedFile.txt"
        val file = new File(path)
        val pw = new PrintWriter(file)
        pw.write("Line 1")
        pw.close
        add.addAFile("testModifiedFile.txt")

        val pwu = new PrintWriter(file)
        pwu.write("\n Line2")
        pwu.write("\n line 3")
        pwu.close

        assert(statusTools.isStagedAndUpdatedContent(file))
      }
    }

    describe("and you don't modify his content") {
      it("it should be an non updated file") {
        val file = new File(repoTools.rootPath + "/testUnmodifiedFile.txt")
        val pw = new PrintWriter(file)
        pw.write("Hello Test")
        pw.close
        add.addAFile("testModifiedFile.txt")
        val hash = fileTools.hash("testModifiedFile.txt")

        assert(!statusTools.isStagedAndUpdatedContent(file))
      }
    }

    describe("and then you modify his content v2") {
      it("it should be an updated file") {
        val path = repoTools.rootPath + "/testModifiedFile2.txt"
        val pw = new PrintWriter(new File(path))
        pw.write("Line test")
        pw.close
        add.addAFile("testModifiedFile2.txt")

        val pwu = new FileWriter(new File(path), true) // update file's content
        pwu.write("\n line 1")
        pwu.write("\n line 2 - added")
        pwu.close

        assert(statusTools.isStagedAndUpdatedContent(new File(repoTools.rootPath + "/testModifiedFile2.txt")))
      }
    }
  }

  describe("If you ask for general status ") {
    it("it should return a list of files ") {
      val path2 = repoTools.rootPath + "/testStatus_updatedCommit.txt"
      val pwc = new PrintWriter(new File(path2)) // add and commit a file
      pwc.write("Line 1")
      pwc.close()
      add.addAFile("testStatus_updatedCommit.txt")
      commit.commit("message")
      val pwcu = new FileWriter(new File(path2), true) // update commited file's content
      pwcu.write("\n Line 2")
      pwcu.close()

      val path = repoTools.rootPath + "/testStatus_updatedStage.txt"
      val pw = new PrintWriter(new File(path))
      pw.write("Line test")
      pw.close()
      add.addAFile("testStatus_updatedStage.txt")

      val pwu = new FileWriter(new File(path), true) // update file's content
      pwu.write("\n line 1")
      pwu.write("\n line 2 - added")
      pwu.close()

      val pathFree = repoTools.rootPath + "/testStatus_free.txt"
      new PrintWriter(new File(pathFree))

      val genStatus = status.generalStatus()
      val listFree = genStatus(0)
      val listUpdatedStaged= genStatus(1)
      val listUpdatedCommit = genStatus(2)
      val listUnCommited = genStatus(3)

      status.showGeneralStatus()

      assert(listFree.length == 1 && listFree(0) == new File(pathFree)
        && listUpdatedStaged.length == 1 && listUpdatedStaged(0) == new File(path)
        && listUpdatedCommit.nonEmpty && listUnCommited.isEmpty)
    }
  }

  describe("If you commit a file") {
    describe("and then you modify his content and add it") {
      it("it should be an staged file not yes commited") {
        val path = repoTools.rootPath + "/testCommitStatus.txt"
        val file = new File(path)
        val pw = new PrintWriter(file)
        pw.write("Line 1")
        pw.close

        add.addAFile("testCommitStatus.txt")
        val allStagedFiles = repoTools.getAllStagedFiles


        commit.commit("message")

        val pwu = new PrintWriter(file)
        pwu.write("\n Line2")
        pwu.write("\n line 3")
        pwu.close
        val tt = statusTools.isCommitedButUpdated(file)

        add.addAFile("testCommitStatus.txt")
        val allStagedFiles2 = repoTools.getAllStagedFiles

        assert(tt && statusTools.isCommited(file) && statusTools.isCommitedButUpdated(file) && statusTools.isStaged(file) && !isStagedAndUpdatedContent(file))
      }
    }
  }
}

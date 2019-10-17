package toolsTest

import java.io.{File, FileWriter, PrintWriter}

import actions.{add, init}
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{fileTools, repoTools, statusTools}
import org.apache.commons.io.FileUtils
import scala.io.Source


class statusTools extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{


  before{
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgitRepo"))
    new File(repoTools.currentPath + "sgitRepo").delete()
    init.initDirectory()
  }

  describe("If you add a file") {
    it("it should be a staged file") {
      val path = repoTools.currentPath + "sgitRepo/testStatusFile.txt"
      val content = "Test status function"

      val pw = new PrintWriter(new File(path))
      pw.write(content)
      pw.close
      add.addAFile("testStatusFile.txt")

      assert(statusTools.isStaged(new File(path)) === true)
    }
  }

  describe("If you stage a file") {
    describe("and then you modify his content") {
      it("it should be an updated file") {
        val path = repoTools.currentPath + "sgitRepo/testModifiedFile.txt"
        val file = new File(path)
        val pw = new PrintWriter(file)
        pw.write("Line 1")
        pw.close
        add.addAFile("testModifiedFile.txt")
        println(Source.fromFile(fileTools.getLinkedStagedFile(file).get).mkString)

        val pwu = new PrintWriter(file)
        pwu.write("\n Line2")
        pwu.write("\n line 3")
        pwu.close

        assert(statusTools.hasBeenUpdated(file))
      }
    }

    describe("and you don't modify his content") {
      it("it should be an non updated file") {
        val file = new File(repoTools.currentPath + "sgitRepo/testUnmodifiedFile.txt")
        val pw = new PrintWriter(file)
        pw.write("Hello Test")
        pw.close
        add.addAFile("testModifiedFile.txt")
        val hash = add.hash("testModifiedFile.txt")

        assert(!statusTools.hasBeenUpdated(file) && !statusTools.isStagedAndUpdatedContent(file))
      }
    }

    describe("and then you modify his content v2") {
      it("it should be an updated file") {
        val path = repoTools.currentPath + "sgitRepo/testModifiedFile2.txt"
        val pw = new PrintWriter(new File(path))
        pw.write("Line test")
        pw.close
        add.addAFile("testModifiedFile2.txt")

        val pwu = new FileWriter(new File(path), true) // update file's content
        pwu.write("\n line 1")
        pwu.write("\n line 2 - added")
        pwu.close

        assert(statusTools.isStagedAndUpdatedContent(new File(repoTools.currentPath + "sgitRepo/testModifiedFile2.txt")))
      }
    }
  }

  describe("If you ask for general status ") {
    it("it should return a list of files ") {
      val path = repoTools.currentPath + "sgitRepo/testStatus_updatedStage.txt"
      val pw = new PrintWriter(new File(path))
      pw.write("Line test")
      pw.close
      add.addAFile("testStatus_updatedStage.txt")

      val pwu = new FileWriter(new File(path), true) // update file's content
      pwu.write("\n line 1")
      pwu.write("\n line 2 - added")
      pwu.close

      val pathFree = repoTools.currentPath + "sgitRepo/testStatus_free.txt"
      new PrintWriter(new File(pathFree))

      val genStatus = statusTools.generalStatus()
      val listFree = genStatus(0)
      val listUpdatedStaged= genStatus(1)
      val listUpdatedCommit = genStatus(2)
      val listUnCommited = genStatus(3)

      statusTools.showGeneralStatus()

      assert(listFree.length == 1 && listFree(0) == new File(pathFree)
        && listUpdatedStaged.length == 1 && listUpdatedStaged(0) == new File(path)
        && listUpdatedCommit.isEmpty && listUnCommited.isEmpty)
    }
  }
}

package toolsTest

import java.io.{File, PrintWriter}

import actions.{add, init}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{fileTools, repoTools, statusTools}

import scala.io.Source


class fileToolsTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
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

  describe("If you hash a specific string") {
    it("it should return a specific hash") {
      val myString = "test"
      val myStringHash = fileTools.encryptThisString(myString)

      assert(myStringHash === fileTools.encryptThisString("test"))
    }
  }

  describe("If you create a file") {
    it("it should exist") {
      new PrintWriter(new File(pathTest + "/realFile.txt"))
      val res1 = fileTools.exist("realFile.txt")
      val res2 = fileTools.exist("noExistFile.txt")

      assert(res1 && !res2)
    }
  }


  describe("If you stage a file") {
      it("it should have the first line equals to the path") {
        val file = new File(pathTest + "/testLinkedStage.txt")
        val pw = new PrintWriter(file) // create the file containing the blob's content
        pw.write("blablabla")
        pw.close
        add.addAFile(file.getName)

        val firstLine = fileTools.firstLine(fileTools.getLinkedStagedFile(file).get)

        assert(firstLine.get == file.getAbsolutePath)
    }
  }

  describe("If you want to re-add a file") {
    describe("with a new content") {
      it("it should updated the staged file ") {
        val file = new File(pathTest + "/updateStagedFile.txt")
        val pw = new PrintWriter(file) // create the file containing the blob's content
        pw.write("blablabla")
        pw.close
        add.addAFile(file.getName)

        fileTools.updateFileContent(file,"new content")
        add.updateStagedFile(file.getName)

        val newStagedContent = fileTools.getContentFile(fileTools.getLinkedStagedFile(file).get.getAbsolutePath)

        val sameContent = newStagedContent == file.getAbsolutePath + "\n" + "new content"
        assert( sameContent&& !statusTools.isStagedAndUpdatedContent(file))

      }
    }
  }
}

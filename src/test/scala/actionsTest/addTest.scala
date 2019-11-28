package actionsTest

import java.io.{File, FileWriter, PrintWriter}
import tools.{statusTools,fileTools}
import org.scalatest._
import _root_.tools.repoTools
import actions.{add, init}
import org.apache.commons.io.FileUtils

class addTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
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
    describe("but this file doesn't exist ") {
      it("it should not do anything.") {
        add.addAFile(pathTest + "/testFakeFile.txt")
        val hash = fileTools.hash("testFakeFile.txt")

        assert(!new File(currentPath + "/.sgit/STAGE" + hash).exists())
      }
    }

    describe("and the file exists") {
      it("it should add it on the stage area") {
        val file = new File(pathTest + "/testAddFile.txt")
        fileTools.updateFileContent(file,"Test add function")
        add.addAFile("testAddFile.txt")
        val hash = fileTools.hash(file.getAbsolutePath + fileTools.getContentFile(file.getAbsolutePath))

        val existOnStage = new File(currentPath + "/.sgit/STAGE/" + hash).exists()
        val newCreatedObject = new File(currentPath + "/.sgit/objects/" + hash.slice(0,2)).exists()

        assert(newCreatedObject && existOnStage)
      }
    }
  }

  describe("If you add the two files ") {
    it("it should add all of them to the stage area") {
      val file1 = new File(pathTest + "/testFreeFile1.txt")
      val file2 = new File(pathTest + "/testFreeFile2.txt")
      new PrintWriter(file1)
      new PrintWriter(file2)
      add.addMultipleFiles(List("testFreeFile1.txt","testFreeFile2.txt"))

      assert(statusTools.isStaged(file1) && statusTools.isStaged(file2))
    }
  }

  describe("If you add a file on the stage area") {
    describe("and then you modify his content and re-add it") {
      it("it should delete the hold staged file and add the new one") {
        val file = new File(pathTest + "/testUpdate.txt")
        fileTools.updateFileContent(file,"Test update function")
        add.addAFile("testUpdate.txt")

        val hash1 = fileTools.hash(file.getAbsolutePath + fileTools.getContentFile(file.getAbsolutePath))

        val pwUpdate = new FileWriter(file, true)
        pwUpdate.write("\n Update")
        pwUpdate.close
        add.addAFile("testUpdate.txt")

        val hash2 = fileTools.hash(file.getAbsolutePath + fileTools.getContentFile(file.getAbsolutePath))

        val existsInObject = new File(currentPath + "/.sgit/objects/" + hash2.slice(0,2)).exists()
        val existsInStage = new File(currentPath + "/.sgit/STAGE/" + hash2).exists()
        val existsHoldFileInStage = new File(currentPath + "/.sgit/STAGE/" + hash1).exists()

        assert(statusTools.isStaged(file) && existsInStage && existsInObject && !existsHoldFileInStage)
      }
    }
  }


}

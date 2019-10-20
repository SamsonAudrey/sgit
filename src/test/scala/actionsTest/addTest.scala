package actionsTest

import java.io.{File, FileWriter, PrintWriter}
import tools.{statusTools,fileTools}
import scala.io.Source
import org.scalatest._
import _root_.tools.repoTools
import actions.{add, init}
import org.apache.commons.io.FileUtils

class addTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    new File(repoTools.currentPath + "/sgit").mkdir() //Test Repo
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
    init.initDirectory(repoTools.currentPath)
  }

  describe("If you add a file") {
    describe("but this file doesn't exist ") {
      it("it should not do anything.") {
        add.addAFile(repoTools.rootPath + "/testFakeFile.txt")
        val hash = fileTools.hash("testFakeFile.txt")

        assert(!new File(repoTools.rootPath + "/.git/STAGE" + hash).exists())
      }
    }

    describe("and the file exists") {
      it("it should add it on the stage area") {
        val file = new File(repoTools.rootPath + "/testAddFile.txt")
        fileTools.updateFileContent(file,"Test add function")
        add.addAFile("testAddFile.txt")

        val hash = fileTools.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)

        val existOnStage = new File(repoTools.rootPath + "/.git/STAGE/" + hash).exists()
        val newCreatedObject = new File(repoTools.rootPath + "/.git/objects/" + hash.slice(0,2)).exists()

        assert(existOnStage && newCreatedObject)
      }
    }
  }

  describe("If you add the two files ") {
    it("it should add all of them to the stage area") {
      val file1 = new File(repoTools.rootPath + "/testFreeFile1.txt")
      val file2 = new File(repoTools.rootPath + "/testFreeFile2.txt")
      new PrintWriter(file1)
      new PrintWriter(file2)
      add.addMultipleFiles(List("testFreeFile1.txt","testFreeFile2.txt"))

      assert(statusTools.isStaged(file1) && statusTools.isStaged(file2))
    }
  }

  describe("If you create different files and add all") {
    it("it should add all the files") {
      val file1 = new File(repoTools.rootPath + "/testAddAll1.txt")
      val file2 = new File(repoTools.rootPath + "/testAddAll2.txt")
      new PrintWriter(file1)
      new PrintWriter(file2)
      add.addAll()

      assert(statusTools.isStaged(file1) && statusTools.isStaged(file2))
    }
  }

  describe("If you add a file on the stage area") {
    describe("and then you modify his content and re-add it") {
      it("it should delete the hold staged file and add the new one") {
        val file = new File(repoTools.rootPath + "/testUpdate.txt")
        fileTools.updateFileContent(file,"Test update function")
        add.addAFile("testUpdate.txt")

        val hash1 = fileTools.hash(file.getAbsolutePath + fileTools.getContentFile(file.getAbsolutePath))

        val pwUpdate = new FileWriter(file, true)
        pwUpdate.write("\n Update")
        pwUpdate.close
        add.addAFile("testUpdate.txt")

        val hash2 = fileTools.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)

        val existsInObject = new File(repoTools.rootPath + "/.git/objects/" + hash2.slice(0,2)).exists()
        val existsInStage = new File(repoTools.rootPath + "/.git/STAGE/" + hash2).exists()
        val existsHoldFileInStage = new File(repoTools.rootPath + "/.git/STAGE/" + hash1).exists()

        assert(statusTools.isStaged(file) && existsInStage && existsInObject && !existsHoldFileInStage)
      }
    }
  }


}

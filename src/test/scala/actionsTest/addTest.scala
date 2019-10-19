package actionsTest

import java.io.{File, FileWriter, PrintWriter}

import tools.statusTools

import scala.io.Source
import org.scalatest._
import _root_.tools.repoTools
import actions.{add, init}
import org.apache.commons.io.FileUtils


class addTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    new File(repoTools.currentPath + "/sgit").mkdir()
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
    init.initDirectory(repoTools.currentPath)
  }

  describe("If you add a file") {
    describe("but the file doesn't exist ") {
      it("it should not do anything") {
        add.addAFile(repoTools.rootFile + "/testFakeFile.txt")
        val hash = add.hash("testFakeFile.txt")

        assert(new File(repoTools.rootFile + "/.git/STAGE" + hash).exists()  === false)
      }
    }

    describe("and the file exists ") {
      it("it should add it on .git/STAGE directory") {
        val file = new File(repoTools.rootFile + "/testAddFile.txt")
        val pw = new PrintWriter(file)
        pw.write("Test add function")
        pw.close

        add.addAFile("testAddFile.txt")
        val hash = add.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)

        assert(new File(repoTools.rootFile + "/.git/STAGE/" + hash).exists()
          && new File(repoTools.rootFile + "/.git/objects/" + hash.slice(0,2)).exists() )
      }
    }
  }

  describe("If you add the two files ") {
    it("it should add all the two files") {
      new PrintWriter(new File(repoTools.rootFile + "/testFreeFile1.txt"))
      new PrintWriter(new File(repoTools.rootFile + "/testFreeFile2.txt"))
      add.addMultipleFiles(List("testFreeFile1.txt","testFreeFile2.txt"))
      assert(statusTools.isStaged(new File(repoTools.rootFile + "/testFreeFile1.txt"))  === true)
    }
  }

  describe("If you create different files ") {
    it("it should add all the files") {
      val file1 = new File(repoTools.rootFile + "/testAddAll1.txt")
      val file2 = new File(repoTools.rootFile + "/testAddAll2.txt")
      new PrintWriter(file1)
      new PrintWriter(file2)
      add.addAll()
      assert(statusTools.isStaged(file1) && statusTools.isStaged(file2))
    }
  }

  describe("If you add a file (STAGE)") {
    describe("and then you modify his content and add it") {
      it("it should delete the hold Staged File and add the new one") {
        val file = new File(repoTools.rootFile + "/testUpdate.txt")
        val pw = new PrintWriter(file)
        pw.write("Test update function")
        pw.close

        add.addAFile("testUpdate.txt")
        val hash1 = add.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)

        val pwUpdate = new FileWriter(file, true)
        pwUpdate.write("\n Update")
        pwUpdate.close
        add.addAll() //add.updateStagedFile("testUpdate.txt")

        val hash2 = add.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)
        val existsInObject = new File(repoTools.rootFile + "/.git/objects/" + hash2.slice(0,2)).exists()
        val existsInStage = new File(repoTools.rootFile + "/.git/STAGE/" + hash2).exists()
        val existsHoldFileInStage = new File(repoTools.rootFile + "/.git/STAGE/" + hash1).exists()

        assert(statusTools.isStaged(file) && existsInStage && existsInObject && !existsHoldFileInStage)
      }
    }
  }


}

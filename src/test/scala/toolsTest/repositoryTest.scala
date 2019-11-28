package toolsTest

import java.io.{File, PrintWriter}
import actions.init
import org.apache.commons.io.FileUtils
import tools.{fileTools, repoTools}
import org.scalatest._
import scala.reflect.io.{File => ScalaFile}

class repositoryTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {
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

  describe("If you want to get folders in a directory") {
    describe("but there is no folders ") {
      it("it should return an empty list") {
        new File(pathTest + "/testRepo1").mkdir()
        val listFolders1 = repoTools.recursiveListFolders(new File(pathTest + "/testRepo1"))

        assert(listFolders1.isEmpty)
      }
    }

    describe("and there is 3 direct sub-folders and one sub-sub-folder") {
      it("it should return a list with this four folders ") {
        new File(pathTest + "/testRepo2").mkdir()
        new File(pathTest + "/testRepo2/folder1").mkdir() // folder 1
        new File(pathTest + "/testRepo2/folder2").mkdir() // folder 2
        new File(pathTest + "/testRepo2/testRepo2BIS").mkdir() // folders 3
        new File(pathTest + "/testRepo2/testRepo2BIS/folder3").mkdir() // folders 4
        val listFolders2 = repoTools.recursiveListFolders(new File(pathTest + "/testRepo2"))

        assert(listFolders2.length === 4)
      }
    }
  }

  describe("If you want to get files in a directory") {
    describe("and there is no files ") {
      it("it should return an empty list") {
        new File(pathTest + "/testRepoEmpty").mkdir()
        val listFoldersEmpty = repoTools.getListOfFiles(new File(pathTest + "/testRepoEmpty"))

        assert(listFoldersEmpty === List())
      }
    }

    describe("and there is three direct files ") {
      it("it should return a list with these three files ") {
        new File(pathTest + "/testRepo3").mkdir()
        new File(pathTest + "/testRepo3/testRepo4").mkdir()
        new PrintWriter(new File(pathTest + "/testRepo3/testRepo4/folder1" ))
        new PrintWriter(new File(pathTest + "/testRepo3/folder1" ))
        new PrintWriter(new File(pathTest + "/testRepo3/folder2" ))
        new PrintWriter(new File(pathTest + "/testRepo3/folder3" ))
        val listFolders3 = repoTools.getListOfFiles(new File(pathTest + "/testRepo3"))

        assert(
          listFolders3 ===
            List(new File(pathTest + "/testRepo3/folder2"),
              new File(pathTest + "/testRepo3/folder3" ),
              new File(pathTest + "/testRepo3/folder1" )))
      }
    }
  }

  describe("If you want to delete a directory") {
    describe("and it exists and contains a sub-folder ") {
      it("it should delete the sub-folder") {
        new File(pathTest + "/testRepo4").mkdir()
        new File(pathTest + "/testRepo4/testRepoToDelete").mkdir()
        new PrintWriter(new File(pathTest + "/testRepo4/testRepoToDelete/subFolder1"))
        repoTools.deleteDirectory("testRepoToDelete")

        assert(!fileTools.exist("subFolder1.txt"))
      }
    }

    describe("and it exist and contains a sub-folder ") {
      it("it should delete the main folder") {
        new File("/testRepo1BIS").mkdir()
        new File("/testRepo1BIS/subFolder1").mkdir()
        repoTools.deleteDirectory("/testRepo1BIS")

        assert(!ScalaFile("/testRepo1BIS").exists)
      }
    }

    describe("and it exist and contains nothing") {
      it("it should return false") {
        new File("/testRepo2").mkdir()
        repoTools.deleteDirectory("/testRepo2")

        assert(!ScalaFile("/testRepo2").exists)
      }
    }

  }
}

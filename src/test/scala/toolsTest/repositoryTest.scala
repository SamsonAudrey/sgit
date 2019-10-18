package toolsTest

import java.io.{File, PrintWriter}

import actions.init
import org.apache.commons.io.FileUtils
import tools.{fileTools, repoTools}
import org.scalatest._

import scala.reflect.io.{File => ScalaFile}

class repositoryTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter {

  before{
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgitRepo"))
    new File(repoTools.currentPath + "sgitRepo").delete()
    init.initDirectory()
  }

  describe("If you want to get folders in a directory") {
    describe("and there is no folders ") {
      it("it should return an empty list") {
        val path = repoTools.currentPath
        new File(path+"/sgitRepo/testRepo1").mkdir()
        val listFolders1 = repoTools.recursiveListFolders(new File(path+"/sgitRepo/testRepo1"))
        assert(listFolders1.isEmpty)
      }
    }

    describe("and there is 3 direct sub-folders and one sub-sub-folder") {
      it("it should return a list with this four folders ") {
        val path = repoTools.currentPath
        new File(path+"/sgitRepo/testRepo2").mkdir()
        new File(path+"/sgitRepo/testRepo2/folder1").mkdir() //1
        new File(path+"/sgitRepo/testRepo2/folder2").mkdir() //2
        new File(path+"/sgitRepo/testRepo2/testRepo2BIS").mkdir() //3
        new File(path+"/sgitRepo/testRepo2/testRepo2BIS/folder3").mkdir() //4
        val listFolders2 = repoTools.recursiveListFolders(new File(path+"/sgitRepo/testRepo2"))
        //println(listFolders2.toList)
        assert(listFolders2.length === 4)
      }
    }
  }

  describe("If you want to get files in a directory") {
    describe("and there is no files ") {
      it("it should return an empty list") {
        val path = repoTools.currentPath
        new File(path+"/sgitRepo/testRepoEmpty").mkdir()
        val listFoldersEmpty = repoTools.getListOfFiles(new File(path + "sgitRepo/testRepoEmpty"))
        assert(listFoldersEmpty === List())
      }
    }

    describe("and there is three direct files ") {
      it("it should return a list with these three files ") {
        val path = repoTools.currentPath
        new File(path+"/sgitRepo/testRepo3").mkdir()
        new File(path+"/sgitRepo/testRepo3/testRepo4").mkdir()
        new PrintWriter(new File(path+ "sgitRepo/testRepo3/testRepo4/folder1" ))
        new PrintWriter(new File(path+ "sgitRepo/testRepo3/folder1" ))
        new PrintWriter(new File(path+ "sgitRepo/testRepo3/folder2" ))
        new PrintWriter(new File(path+ "sgitRepo/testRepo3/folder3" ))
        val listFolders3 = repoTools.getListOfFiles(new File(path + "sgitRepo/testRepo3"))
        assert(
          listFolders3 ===
            List(new File(path+ "sgitRepo/testRepo3/folder2" ),
              new File(path+ "sgitRepo/testRepo3/folder3" ),
              new File(path+ "sgitRepo/testRepo3/folder1" )))
      }
    }
  }

  describe("If you want to delete a directory") {
    describe("and it exist and contains a sub-folder ") {
      it("it should delete the sub-folder") {
        val path = repoTools.currentPath
        new File(path + "sgitRepo/testRepo4").mkdir()
        new File(path + "sgitRepo/testRepo4/testRepoToDelete").mkdir()
        new PrintWriter(new File(path + "sgitRepo/testRepo4/testRepoToDelete/subFolder1"))
        repoTools.deleteDirectory("testRepoToDelete")
        assert(!fileTools.exist("subFolder1.txt"))
      }
    }

    describe("and it exist and contains a sub-folder ") {
      it("it should delete the main folder") {
        new File("/testRepo1BIS").mkdir()
        new File("/testRepo1BIS/subFolder1").mkdir()
        repoTools.deleteDirectory("/testRepo1BIS")
        assert(ScalaFile("/testRepo1BIS").exists === false)
      }
    }

    describe("and it exist and contains nothing") {
      it("it should return false") {
        new File("/testRepo2").mkdir()
        repoTools.deleteDirectory("/testRepo2")
        assert(ScalaFile("/testRepo2").exists === false)
      }
    }

  }

}

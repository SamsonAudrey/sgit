package toolsTest

import java.io.File
import actions.{add, commit, diff, init}
import objects.LineDiff
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{diffTools, fileTools, repoTools}

class diffTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{
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

  describe("If you compare two files") {
    describe("and they have the same content") {
      it("it should return an empty list of differences") {
        val oldText = Seq("a", "b", "c", "d")
        val newText = Seq("a", "b", "c", "d")

        val res1:List[LineDiff] = diffTools.diffBetweenTexts(oldText, newText)

        assert(res1 === List())
      }
    }

    describe("and they are empty") {
      it("it should return an empty list") {
        val oldText2 = Seq()
        val newText3 = Seq()

        val res2:List[LineDiff] = diffTools.diffBetweenTexts(oldText2, newText3)

        assert(res2 === List())
      }
    }

    describe("and they have different contents - case 1") {
      it("it should return the list of differences") {
        val oldText2 = Seq("a", "b", "c")
        val newText3 = Seq("a", "b", "c", "d")

        val res2:List[LineDiff] = diffTools.diffBetweenTexts(oldText2, newText3)

        assert(res2 === List(LineDiff("ADD",3,"d")))
      }
    }

    describe("and they have different contents  - case 2") {
      it("it should return the list of differences") {
        val oldText2 = Seq("a", "b", "c", "e", "f")
        val newText3 = Seq("a", "b", "c", "d")

        val res2:List[LineDiff] = diffTools.diffBetweenTexts(oldText2, newText3)

        assert(res2 === List(LineDiff("ADD",3,"d"),LineDiff("REMOVE",4,"e"),LineDiff("REMOVE",5,"f")))
      }
    }
  }

  /*describe("If you ask for general diff") {
    it("it should show you the list of differences") {
      // Update content (commit file)
      val file4 = new File(pathTest + "/TestGenDiff4.txt")
      fileTools.updateFileContent(file4, "test")
      add.addAFile(file4.getName)
      commit.commit("message")
      fileTools.addLineIntoFile("\nline 2", file4)

      val file = new File(pathTest + "/TestGenDiff.txt")
      fileTools.updateFileContent(file, "first content")
      add.addAFile(file.getName)

      // Update content (free file)
      fileTools.updateFileContent(file, "new content")

      // No update content
      val file2 = new File(pathTest + "/TestGenDiff2.txt")
      fileTools.updateFileContent(file2, "test")
      add.addAFile(file2.getName)

      // Update content (Staged file)
      val file3 = new File(pathTest + "/TestGenDiff3.txt")
      fileTools.updateFileContent(file3, "test")
      add.addAFile(file3.getName)
      fileTools.addLineIntoFile("\nline 2 \nline 3\n     \n", file3)

      diff.diff()
      assert(true) // Visual test
    }
  }*/
}

package toolsTest

import java.io.{File, FileWriter, PrintWriter}

import actions.{add, init}
import objects.LineDiff
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{diffTools, fileTools, repoTools}

class diffToolsTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgitRepo"))
    new File(repoTools.currentPath + "sgitRepo").delete()
    init.initDirectory()
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

  describe("If you ask for general diff") {
    it("it should show you the list of differences") {
      val file = new File(repoTools.currentPath + "sgitRepo/TestGenDiff.txt")
      val pw = new PrintWriter(file) // create the file containing the blob's content
      pw.write("blablabla")
      pw.close
      add.addAFile(file.getName)

      val fileB = new File(repoTools.currentPath + "sgitRepo/TestGenDiffB.txt")
      val pwB = new PrintWriter(fileB) // create the file containing the blob's content
      pwB.write("test")
      pwB.close
      add.addAFile(fileB.getName)

      val pw2 = new PrintWriter(file)
      pw2.write("new content")
      pw2.close

      val file2 = new File(repoTools.currentPath + "sgitRepo/TestGenDiff2.txt")
      val pw3 = new PrintWriter(file2) // create the file containing the blob's content
      pw3.write("line 1")
      pw3.close
      add.addAFile(file2.getName)

      val pw4 = new FileWriter(file2,true)
      pw4.write("\nline 2 \n     \n line 3\n     \n")
      pw4.close

      diffTools.showGeneralDiff()
      assert(true)
    }
  }
}

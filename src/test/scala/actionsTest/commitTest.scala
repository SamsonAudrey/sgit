package actionsTest

import java.io.{File, FileWriter, PrintWriter}

import actions.{add, commit, init}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{commitTools, fileTools, repoTools, statusTools}

import scala.io.Source

class commitTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgitRepo"))
    new File(repoTools.currentPath + "sgitRepo").delete()
    init.initDirectory()
  }

  describe("If you never had commit already") {
    describe("and you commit") {
      it("it should be your first commit") {
        assert(commit.isFirst() === true)
      }
    }
  }

  describe("If you add one file and then commit") {
      it("commit file should contains the file's path") {
        val file = new File(repoTools.currentPath + "sgitRepo/testCommit.txt")
        new PrintWriter(file)
        add.addAFile("testCommit.txt")

        val hash = add.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)

        commit.commit()

        val commitHash = commitTools.lastCommitHash()

        val createdCommitObject = new File(repoTools.currentPath + "sgitRepo/.git/objects/" + commitHash).exists()
        val content = Source.fromFile(repoTools.currentPath + "sgitRepo/.git/objects/" + commitHash).mkString
        val supposedContent = "\n" + hash + " " + file.getAbsolutePath+"\n"

        assert(createdCommitObject && content == supposedContent )
      }
  }

  describe("If it is not your first commit") {
    describe("and you commit") {
      it("commit file should contains the parent's hash") {
        // FIRST COMMIT
        val file = new File(repoTools.currentPath + "sgitRepo/testCommit2.txt")
        new PrintWriter(file)
        add.addAFile("testCommit2.txt")
        val hash = add.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)
        commit.commit()

        val lastCommitHash = commitTools.lastCommitHash()

        // SECOND COMMIT
        val file2 = new File(repoTools.currentPath + "sgitRepo/testCommit3.txt")
        new PrintWriter(file2)
        add.addAFile("testCommit3.txt")
        val hash2 = add.hash(file2.getAbsolutePath + Source.fromFile(file2.getAbsolutePath).mkString)
        commit.commit()

        val commitHash =  commitTools.lastCommitHash()
        val firstLineContent = fileTools.firstLine(new File(repoTools.currentPath + "sgitRepo/.git/objects/" + commitHash)).get

        assert(firstLineContent == lastCommitHash)
      }
    }
  }

  describe("If you add one file and then commit") {
    it("the file's status should be 'commited'") {
      val file = new File(repoTools.currentPath + "sgitRepo/testCommit3.txt")
      new PrintWriter(file)
      add.addAFile("testCommit3.txt")
      commit.commit()

      assert(statusTools.isCommited(file))
    }
  }

  describe("If you commit") {
    it("the stage file should be empty") {
      val file = new File(repoTools.currentPath + "sgitRepo/testCommit3.txt")
      new PrintWriter(file)
      add.addAFile("testCommit3.txt")
      commit.commit()

      val stagePath = repoTools.currentPath + "sgitRepo/.git/STAGE"
      val stageContent = repoTools.getListOfFiles(new File(stagePath))

      assert(stageContent.isEmpty)
    }
  }

  describe("If you add one file and then commit") {
    it("the commit should contain past commited files + the new one") {
      // FIRST COMMIT
      val file = new File(repoTools.currentPath + "sgitRepo/testCommit4.txt")
      new PrintWriter(file)
      add.addAFile("testCommit4.txt")
      val hash = add.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)
      commit.commit()

      // SECOND COMMIT
      val file2 = new File(repoTools.currentPath + "sgitRepo/testCommit5.txt")
      new PrintWriter(file2)
      add.addAFile("testCommit5.txt")
      commit.commit()

      assert(statusTools.isCommited(file) && statusTools.isCommited(file2))
    }
  }


  describe("You add one file and then commit") {
    describe("If your delete it from your working directory") {
      it("it should not appear in the commit content") {
        val file = new File(repoTools.currentPath + "sgitRepo/testCommit6.txt")
        new PrintWriter(file)
        add.addAFile("testCommit6.txt")
        commit.commit()

        new File(repoTools.currentPath + "sgitRepo/testCommit6.txt").delete()
        val file2 = new File(repoTools.currentPath + "sgitRepo/testCommit6B.txt")
        new PrintWriter(file2)
        add.addAFile("testCommit6B.txt")
        commit.commit()

        assert(statusTools.wasCommited(file)
          && !statusTools.isCommited(file)
          && commitTools.isRemoved(file)
          && !commitTools.isRemoved(file2)
          && !new File(repoTools.currentPath + "sgitRepo/testCommit6.txt").exists())
      }
    }
  }

  describe("You commit a file") {
    describe("then you update it and recommit") {
      it("the old one should not appear in the commit content") {
        val file = new File(repoTools.currentPath + "sgitRepo/testCommit7.txt")
        new PrintWriter(file)
        add.addAFile("testCommit7.txt")
        commit.commit()

        val pwUpdate = new FileWriter(file, true)
        pwUpdate.write("\n Update")
        pwUpdate.close

        add.addAFile("testCommit7.txt")
        commit.commit()

        val content = repoTools.getAllCommitedFileHash(commitTools.lastCommitHash())

        assert(content.length == 1)

      }
    }
  }


  describe("If the STAGE is empty") {
    describe("and you commit") {
      it("the commit should not be done") {
        val file = new File(repoTools.currentPath + "sgitRepo/testCommit8.txt")
        new PrintWriter(file)
        add.addAFile("testCommit8.txt")
        commit.commit()
        val fistCommitHash = commitTools.lastCommitHash()
        commit.commit()
        val secondCommitHash = commitTools.lastCommitHash()

        //GET CONTENT COMMIT
        //println(Source.fromFile(repoTools.currentPath + "sgitRepo/.git/objects/"+secondCommitHash).mkString)

        assert(secondCommitHash == fistCommitHash)
      }
    }
  }

}

package actionsTest

import java.io.{File, FileWriter, PrintWriter}
import actions.{add, commit, init}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{commitTools, fileTools, repoTools, statusTools}

import scala.io.Source

class commitTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

  before{
    new File(repoTools.currentPath + "/sgit").mkdir() //TestRepo
    FileUtils.cleanDirectory(new File(repoTools.currentPath + "sgit"))
    init.initDirectory(repoTools.currentPath)
  }

  describe("If you never had commit already") {
    describe("and you commit") {
      it("it should be your first commit") {
        assert(commit.isFirstCommit)
      }
    }
  }

  describe("If you add one file and then commit") {
    it("commit file should contains the file's path") {
      val file = new File(repoTools.rootPath + "/testCommit.txt")
      new PrintWriter(file)
      add.addAFile("testCommit.txt")

      val hash = fileTools.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)

      commit.commit("message")

      val commitHash = commitTools.lastCommitHash()

      val createdCommitObject = new File(repoTools.rootPath + "/.git/objects/" + commitHash).exists()
      val content = Source.fromFile(repoTools.rootPath + "/.git/objects/" + commitHash).mkString
      val supposedContent = "\n" +  "message\n" + hash + " " + file.getAbsolutePath+"\n"

      assert(createdCommitObject && content == supposedContent )
    }
  }

  describe("If it is not your first commit") {
    describe("and you commit") {
      it("commit file should contains the parent's hash") {
        // FIRST COMMIT
        val file = new File(repoTools.rootPath + "/testCommit2.txt")
        new PrintWriter(file)
        add.addAFile("testCommit2.txt")
        val hash = fileTools.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)
        commit.commit("message")

        val lastCommitHash = commitTools.lastCommitHash()

        // SECOND COMMIT
        val file2 = new File(repoTools.rootPath + "/testCommit3.txt")
        new PrintWriter(file2)
        add.addAFile("testCommit3.txt")
        val hash2 = fileTools.hash(file2.getAbsolutePath + Source.fromFile(file2.getAbsolutePath).mkString)
        commit.commit("message")

        val commitHash =  commitTools.lastCommitHash()
        val firstLineContent = fileTools.firstLine(new File(repoTools.rootPath + "/.git/objects/" + commitHash)).get

        assert(firstLineContent == lastCommitHash)
      }
    }
  }

  describe("If you add one file and then commit") {
    it("the file's status should be 'commited'") {
      val file = new File(repoTools.rootPath + "/testCommit3.txt")
      new PrintWriter(file)
      add.addAFile("testCommit3.txt")
      commit.commit("message")

      assert(statusTools.isCommited(file))
    }
  }

  describe("If you commit") {
    it("the stage file should be empty") {
      val file = new File(repoTools.rootPath + "/testCommit3.txt")
      new PrintWriter(file)
      add.addAFile("testCommit3.txt")
      commit.commit("message")

      val stagePath = repoTools.rootPath + "/.git/STAGE"
      val stageContent = repoTools.getListOfFiles(new File(stagePath))

      assert(stageContent.isEmpty)
    }
  }

  describe("If you add one file and then commit") {
    it("The commit should contain past commited files + the new one") {
      // FIRST COMMIT
      val file = new File(repoTools.rootPath + "/testCommit4.txt")
      new PrintWriter(file)
      add.addAFile("testCommit4.txt")
      val hash = fileTools.hash(file.getAbsolutePath + Source.fromFile(file.getAbsolutePath).mkString)
      commit.commit("message")

      // SECOND COMMIT
      val file2 = new File(repoTools.rootPath + "/testCommit5.txt")
      new PrintWriter(file2)
      add.addAFile("testCommit5.txt")
      commit.commit("message")

      assert(statusTools.isCommited(file) && statusTools.isCommited(file2))
    }
  }


  describe("If you delete a file which was commited ") {
    describe("and then commit this change") {
      it("it should not appear in the commit content") {
        val file = new File(repoTools.rootPath + "/testCommit6.txt")
        new PrintWriter(file)
        add.addAFile("testCommit6.txt")
        commit.commit("message")

        new File(repoTools.rootPath + "/testCommit6.txt").delete()
        val file2 = new File(repoTools.rootPath + "/testCommit6B.txt")
        new PrintWriter(file2)
        add.addAFile("testCommit6B.txt")
        commit.commit("message")

        assert(statusTools.wasCommited(file)
          && !statusTools.isCommited(file)
          && commitTools.isRemoved(file)
          && !commitTools.isRemoved(file2)
          && !new File(repoTools.rootPath + "/testCommit6.txt").exists())
      }
    }
  }

  describe("You commit a file") {
    describe("then you update it and recommit") {
      it("the old one should not appear in the commit content") {
        val file = new File(repoTools.rootPath + "/testCommit7.txt")
        new PrintWriter(file)
        add.addAFile("testCommit7.txt")
        commit.commit("message")

        val pwUpdate = new FileWriter(file, true)
        pwUpdate.write("\n Update")
        pwUpdate.close

        add.addAFile("testCommit7.txt")
        commit.commit("message")

        val content = repoTools.getAllFilesFromCommit(commitTools.lastCommitHash())

        assert(content.length == 1)
      }
    }
  }


  describe("If the stage area is empty") {
    describe("and you commit") {
      it("the commit should not be done") {
        val file = new File(repoTools.rootPath + "/testCommit8.txt")
        new PrintWriter(file)
        add.addAFile("testCommit8.txt")
        commit.commit("message")

        val fistCommitHash = commitTools.lastCommitHash()
        commit.commit("message")

        val secondCommitHash = commitTools.lastCommitHash()

        assert(secondCommitHash == fistCommitHash)
      }
    }
  }

}

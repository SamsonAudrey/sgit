package actionsTest

import java.io.{File, FileWriter, PrintWriter}

import actions.{add, commit, init, log}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FunSpec, GivenWhenThen, Matchers}
import tools.{commitTools, fileTools, repoTools, statusTools}

class commitTest extends FunSpec with Matchers with GivenWhenThen with BeforeAndAfter{

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

  describe("If you never had commit already") {
    describe("and you commit") {
      it("it should be your first commit") {
        assert(commit.isFirstCommit)
      }
    }
  }

  describe("If you add one file and then commit") {
    it("commit file should contains the file's path") {
      val file = new File(pathTest + "/testCommit.txt")
      new PrintWriter(file)
      add.addAFile("testCommit.txt")

      val hash = fileTools.hash(file.getAbsolutePath + fileTools.getContentFile(file.getAbsolutePath))
      commit.commit("message")

      val commitHash = commitTools.lastCommitHash()

      val createdCommitObject = new File(currentPath + "/.sgit/objects/" + commitHash).exists()
      val content = fileTools.getContentFile(currentPath + "/.sgit/objects/" + commitHash)
      val supposedContent = "\n" +  "message\n" + hash + " " + file.getAbsolutePath+"\n"

      assert(createdCommitObject && content == supposedContent )
    }
  }

  describe("If it is not your first commit") {
    describe("and you commit") {
      it("commit file should contains the parent's hash") {
        // FIRST COMMIT
        val file = new File(pathTest + "/testCommit2.txt")
        new PrintWriter(file)
        add.addAFile("testCommit2.txt")
        val hash = fileTools.hash(file.getAbsolutePath + fileTools.getContentFile(file.getAbsolutePath))
        commit.commit("message")

        val lastCommitHash = commitTools.lastCommitHash()

        // SECOND COMMIT
        val file2 = new File(pathTest + "/testCommit3.txt")
        new PrintWriter(file2)
        add.addAFile("testCommit3.txt")
        val hash2 = fileTools.hash(file2.getAbsolutePath + fileTools.getContentFile(file2.getAbsolutePath))
        commit.commit("message")

        val commitHash =  commitTools.lastCommitHash()
        val firstLineContent = fileTools.firstLine(new File(currentPath + "/.sgit/objects/" + commitHash)).get

        assert(firstLineContent == lastCommitHash)
      }
    }
  }

  describe("If you add one file and then commit") {
    it("the file's status should be 'commited'") {
      val file = new File(pathTest + "/testCommit3.txt")
      new PrintWriter(file)
      add.addAFile("testCommit3.txt")
      commit.commit("message")

      assert(statusTools.isCommited(file))
    }
  }

  describe("If you commit") {
    it("the stage file should be empty") {
      val file = new File(pathTest + "/testCommit3.txt")
      new PrintWriter(file)
      add.addAFile("testCommit3.txt")
      commit.commit("message")

      val stagePath = currentPath + "/.sgit/STAGE"
      val stageContent = repoTools.getListOfFiles(new File(stagePath))

      assert(stageContent.isEmpty)
    }
  }

  describe("If you add one file and then commit") {
    it("The commit should contain past commited files + the new one") {
      // FIRST COMMIT
      val file = new File(pathTest + "/testCommit4.txt")
      new PrintWriter(file)
      add.addAFile("testCommit4.txt")
      val hash = fileTools.hash(file.getAbsolutePath + fileTools.getContentFile(file.getAbsolutePath))
      commit.commit("message")

      // SECOND COMMIT
      val file2 = new File(pathTest + "/testCommit5.txt")
      new PrintWriter(file2)
      add.addAFile("testCommit5.txt")
      commit.commit("message")

      assert(statusTools.isCommited(file) && statusTools.isCommited(file2))
    }
  }


  describe("If you delete a file which was commited ") {
    describe("and then commit this change") {
      it("it should not appear in the commit content") {
        val file = new File(pathTest + "/testCommit6.txt")
        new PrintWriter(file)
        add.addAFile("testCommit6.txt")
        commit.commit("message")

        new File(pathTest + "/testCommit6.txt").delete()
        val file2 = new File(pathTest + "/testCommit6B.txt")
        new PrintWriter(file2)
        add.addAFile("testCommit6B.txt")
        commit.commit("message")

        assert(statusTools.wasCommited(file)
          && !statusTools.isCommited(file)
          && commitTools.isRemoved(file)
          && !commitTools.isRemoved(file2)
          && !new File(pathTest + "/testCommit6.txt").exists())
      }
    }
  }

  describe("You commit a file") {
    describe("then you update it and recommit") {
      it("the old one should not appear in the commit content") {
        val file = new File(pathTest + "/testCommit7.txt")
        new PrintWriter(file)
        add.addAFile("testCommit7.txt")
        commit.commit("message")

        val pwUpdate = new FileWriter(file, true)
        pwUpdate.write("\n Update")
        pwUpdate.close

        add.addAFile("testCommit7.txt")
        commit.commit("message2")

        println("/////////")
        log.logP()

        val content = repoTools.getAllFilesFromCommit(commitTools.lastCommitHash())

        assert(content.length == 1)
      }
    }
  }


  describe("If the stage area is empty") {
    describe("and you commit") {
      it("the commit should not be done") {
        val file = new File(pathTest + "/testCommit8.txt")
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

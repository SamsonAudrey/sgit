package tools

import java.io.File
import actions.{branch, commit}
import tools.statusTools.isCommited

object commitTools {

  /**
    * Get all files which has been updated, removed or added to the working directory
    * @return
    */
  def getFilesChanges: List[List[File]] = {
    val allUpdatedFiles = repoTools.getAllStagedFiles.filter(f => isUpdatedFromStageToCommit(f))
    val allAddedFiles = repoTools.getAllStagedFiles.filter(f => !isCommited(f))
    val allRemovedFiles = getLastCommitFiles.filter(f => !f.exists())

    List(allUpdatedFiles, allRemovedFiles, allAddedFiles)
  }


  /**
    * Return true if the file there are differences between staged file and commited file
    * @param file : File [file from stage area]
    * @return
    */
  def isUpdatedFromStageToCommit(file: File): Boolean = {
    val path = fileTools.firstLine(file).getOrElse("")
    if (!statusTools.isCommited(file)) false
    else {
      val allCommitedHash = getLastCommitFileHashs
      !allCommitedHash.contains(file.getName)
    }
  }

  /**
    * Get last commit hash of the current branch
    * @return
    */
  def lastCommitHash(): String = {
    val branch = fileTools.firstLine(new File(repoTools.rootPath + "/.git/HEAD/branch"))
    val path = repoTools.rootPath + "/.git/refs/heads/" + branch.get
    fileTools.getContentFile(path)
  }

  /**
    * Get parent hash of last commit of the current branch
    * @return
    */
  def lastCommitParentHash() : String = {
    val currentBranch = branch.currentBranch()
    val pathCurrentBranch = repoTools.rootPath + "/.git/refs/heads/" + currentBranch
    val objectsPath = repoTools.rootPath + "/.git/objects/"
    if (commit.isFirstCommit || fileTools.firstLine(new File(objectsPath + commitTools.lastCommitHash())).getOrElse("") == "") ""
    else {
      val lastCommitHash = fileTools.getContentFile(pathCurrentBranch)
      val seqHash = fileTools.getContentFile(objectsPath + lastCommitHash)
        .split("\n")
        .toSeq
        .map(_.trim)
      seqHash(0) // first line = parent hash
    }
  }


  /**
    * Return true if the file has been deleted from working directory
    * @param file : File
    * @return
    */
  def isRemoved(file: File): Boolean = {
    !fileTools.exist(file.getName) && statusTools.wasCommited(file)
  }


  /**
    * Get all the working directory files of the last commit
    * @return
    */
  def getLastCommitFiles: List[File] = {
    if (lastCommitHash() == "") List()
    else {
      repoTools.getAllFilesFromCommit(lastCommitHash()).map(f => new File(f.split(" ").map(_.trim).toList(1)))
    }
  }

  /**
    * Get all the hash of the last commit
    * @return
    */
  def getLastCommitFileHashs: List[File] = {
    if (lastCommitHash() == "") List()
    else {
      repoTools.getAllFilesFromCommit(lastCommitHash()).map(f => new File(f.split(" ").map(_.trim).toList(0)))
    }
  }

}

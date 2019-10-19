package tools

import java.io.File

import actions.status
import objects.{LineDiff, operation}

import scala.io.Source

object diffTools {

  /**
    * Return List of differences between two files
    * @param freeFile : File
    * @param stagedFile : File
    * @return
    */
  def diff(freeFile: File, stagedFile: File): List[LineDiff] = {
    val newContent = fileTools.getContentFile(freeFile.getAbsolutePath)
    val nC = newContent.split("\n")
      .toSeq
      .map(_.trim)
    val oldContent = fileTools.getContentFile(stagedFile.getAbsolutePath)
    val oC = oldContent.split("\n")
      .toSeq
      .map(_.trim)
      .filter(x=>x != freeFile.getAbsolutePath)

    diffTools.diffBetweenTexts(oC,nC)
  }

  /**
    * Test difference between two texts
    * Return list of LineDiff
    * @param originalText
    * @param modifiedText
    * @return
    */
  def diffBetweenTexts(originalText: Seq[String], modifiedText: Seq[String]): List[LineDiff]  ={
    diffTexts(originalText, modifiedText, List[LineDiff](), 0)
  }

  /**
    * Test difference between two texts
    * Recurcive function
    * @param originalText
    * @param modifiedText
    * @param listDiff
    * @param index
    * @return
    */
  def diffTexts(originalText: Seq[String], modifiedText: Seq[String], listDiff: List[LineDiff], index: Integer): List[LineDiff] ={
    if (originalText.isEmpty && modifiedText.isEmpty) {
      listDiff
    }
    else if (originalText.isEmpty) {
      diffTexts(originalText,modifiedText.drop(1), listDiff :+ LineDiff(operation.ADD, index, modifiedText.head), index + 1)
    }
    else if (modifiedText.isEmpty) {
      diffTexts(originalText.drop(1),modifiedText, listDiff :+ LineDiff(operation.REMOVE, index, originalText.head), index + 1)
    }
    else {
      if(originalText.head == modifiedText.head) {
        diffTexts(originalText.drop(1),modifiedText.drop(1), listDiff,index + 1)
      } else {
        val res1 = diffTexts(originalText.drop(1),modifiedText, listDiff :+ LineDiff(operation.REMOVE, index, originalText.head),index + 1)
        val res2 = diffTexts(originalText,modifiedText.drop(1), listDiff :+ LineDiff(operation.ADD, index, modifiedText.head),index + 1)

        if (res1.size < res2.size) res1 else res2
      }
    }
  }

  /**
    * Print all the differences of all Files
    */
  def showGeneralDiff(): String = {
    var content = ""

    // var list : List(allFreeFiles, allUpdatedStagedFiles, allUpdatedCommitedFiles, allStagedUnCommitedFiles)
    val list = status.generalStatus()

    // no diff for free files in list(0)
    // no diff for uncommited files in list(3)

    // diff between stage area and working directory
    if (list(1).nonEmpty) {
      list(1).map(f => {
        val name = f.getName
        val stageFile = fileTools.getLinkedStagedFile(f)
        content += s"diff --git a/$name b/$name \n"+
          "--- a/$name\n" +
          "+++ b/$name\n"
        val allDiff = diff(f,stageFile.get)
        content += "@@ " + (allDiff(0).index + 1) + "," + allDiff.length + " @@ \n"
        allDiff.foreach(d => content += formatDiffLine(d) + "\n") } )
    }

    // diff between last commit and working directory
    if (list(2).nonEmpty) {
      list(2).map(f => {
        val name = f.getName
        val commitedFile = fileTools.getLinkedCommitFile(f)
        content += s"diff --git a/$name b/$name \n"+
          "--- a/$name\n" +
          "+++ b/$name\n"
        val allDiff = diff(f,commitedFile.get)
        content += "@@ " + (allDiff(0).index + 1) + "," + allDiff.length + " @@ \n"
        allDiff.foreach(d => content += formatDiffLine(d) + "\n") } )
    }
    content
  }

  /**
    * Return Line of differences with the good format for then print it
    * @param lineDiff : LineDiff
    * @return
    */
  def formatDiffLine(lineDiff: LineDiff): String = {
    lineDiff.ope match {
      case "ADD" => "Line " + (lineDiff.index +1) + ": + " + lineDiff.content
      case "REMOVE" => "Line " + (lineDiff.index +1) +  ": - " + lineDiff.content
    }
  }
}

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
    * @param originalText : Seq[String]
    * @param modifiedText : Seq[String]
    * @return
    */
  def diffBetweenTexts(originalText: Seq[String], modifiedText: Seq[String]): List[LineDiff] = {

    /**
      * Test difference between two texts
      * Recurcive function
      * @param originalText : Seq[String]
      * @param modifiedText : Seq[String]
      * @param listDiff : List[LineDiff]
      * @param index : Int
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

    diffTexts(originalText, modifiedText, List[LineDiff](), 0)

  }



  /**
    * Print all the differences of all Files
    */
  def showGeneralDiff(): List[String] = {
    var head = ""
    var content = ""
    var lines = ""

    // list = List(allFreeFiles, allUpdatedStagedFiles, allUpdatedCommitedFiles, allStagedUnCommitedFiles)
    val list = status.generalStatus()

    // No diff for free files in list(0)
    // No diff for uncommited files in list(3)

    // Diff between stage area and working directory
    /*if (list(1).nonEmpty) {

      list(1).filter(!list(2).contains(_)).map(f => {

        val name = f.getName
        val stageFile = fileTools.getLinkedStagedFile(f)
        content += s"diff --git a/$name b/$name \n"+
          "--- a/$name\n" +
          "+++ b/$name\n"

        val allDiff = diff(f,stageFile.get)

        content += "@@ " + (allDiff(0).index + 1) + "," + allDiff.length + " @@ \n"
        allDiff.map(d => content += formatDiffLine(d) + "\n") } )
    }*/

    // Diff between last commit and working directory [NOT WITH STAGE AREA]
    val l = list(2).filter(f => !list(1).contains(f) && !list(3).contains(f) )
    if (l.nonEmpty) {

      l.map(f => {

        val name = f.getName
        val commitedFile = fileTools.getLinkedCommitFile(f)

        head += s"diff --git a/$name b/$name \n"+
          "--- a/$name\n" +
          "+++ b/$name"

        val allDiff = diff(f,commitedFile.get)

        content += "@@ " + (allDiff(0).index + 1) + "," + allDiff.length + " @@ "
        allDiff.map(d => lines += formatDiffLine(d) + "\n") } )
    }
    List(head,content,lines)
  }

  /**
    * Return Line of differences with the good format
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

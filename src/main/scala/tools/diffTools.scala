package tools

import java.io.File

import objects.{LineDiff, operation}
import tools.diffTools.diff

import scala.io.Source

object diffTools {

  /**
    * Return List of differences between two files
    * @param freeFile
    * @param stagedFile
    * @return
    */
  def diff(freeFile: File, stagedFile: File): List[LineDiff] = {
    val newContent = Source.fromFile(freeFile.getAbsolutePath).mkString
    val nC = newContent.split("\n")
      .toSeq
      .map(_.trim)
      //.filter(_ != "")
    val oldContent = Source.fromFile(stagedFile.getAbsolutePath).mkString
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
  def showGeneralDiff(): Unit= {
    val list = statusTools.generalStatus()

    // no diff for free files in list(0)

    if (list(1).nonEmpty) {
      list(1).foreach(f => {
        val name = f.getName
        val stageFile = fileTools.getLinkedStagedFile(f)
        println(s"diff --git a/$name b/$name \n"+
          "--- a/$name\n" +
          "+++ b/$name")
        val allDiff = diff(f,stageFile.get)
        println("@@ " + (allDiff(0).index + 1) + "," + allDiff.length + " @@")
        println(allDiff.foreach(d => println(formatDiffLine(d)))) } )
    }
    /*if (list(2).nonEmpty) {
      println("Changes not staged for commit:\n  (use \"git add <file>...\" " +
        "to update what will be committed)")
      list(2).foreach(f => println(f.getName))
    }

    if (list(3).nonEmpty) {
      println("Changes to be committed:")
      list(3).foreach(f => println(f.getName))
    }

    if(list(0).isEmpty && list(1).isEmpty && list(2).isEmpty) {
      println("nothing to commit or add")
    }*/
  }

  /**
    * Return Line of differences with the good format for then print it
    * @param lineDiff
    * @return
    */
  def formatDiffLine(lineDiff: LineDiff): String = {
    var res = ""
    lineDiff.ope match {
      case "ADD" => res += "Line " + (lineDiff.index +1) + ": + " + lineDiff.content
      case "REMOVE" => res += "Line " + (lineDiff.index +1) +  ": - " + lineDiff.content
    }
    res
  }
}

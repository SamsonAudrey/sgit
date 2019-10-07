package commands

import objects.LineDiff
import objects.operation

object diff {



  def diffBetweenTexts(originalText: Seq[String], modifiedText: Seq[String]) ={
    val l = List[LineDiff]()
    val listDiff = diffTexts(originalText, modifiedText, l, 0)
    println(listDiff)
  }

  def diffTexts(originalText: Seq[String], modifiedText: Seq[String], listDiff: List[LineDiff], index: Int): List[LineDiff] ={

    if (originalText.isEmpty && modifiedText.isEmpty) {
      println("Les 2 sont empty")
      listDiff
    }
    else if (originalText.isEmpty) {
      println("L'original est empty")
      diffTexts(originalText,modifiedText.drop(1), listDiff :+ LineDiff(operation.ADD, 1, modifiedText.head), index + 1)
    }
    else if (modifiedText.isEmpty) {
      println("Le modifié est empty")
      diffTexts(originalText.drop(1),modifiedText, listDiff :+ LineDiff(operation.REMOVE, 1, originalText.head), index + 1)
    }
    else {
      if(originalText.head == modifiedText.head) {
        diffTexts(originalText.drop(1),modifiedText.drop(1), listDiff,index + 1)
      } else {
        println("else ")
        val res1 = diffTexts(originalText.drop(1),modifiedText, listDiff :+ LineDiff(operation.REMOVE, 1, originalText.head),index + 1)
        val res2 = diffTexts(originalText,modifiedText.drop(1), listDiff :+ LineDiff(operation.ADD, 1, modifiedText.head),index + 1)
        println("RESULTAT"+ res1 + " ---- " + res2)
        if (res1.size < res2.size) res1 else res2
      }
    }

  }

}

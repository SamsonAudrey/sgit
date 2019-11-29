package actions

import tools.{diffTools, printerTools}

object diff {

  /**
    * Display diff
    */
  def diff(): Unit = {
    val diff = diffTools.showGeneralDiff()
    diff.map(d => {
      if (d(0).isEmpty && d(1).isEmpty && d(2).isEmpty) {}
      else {
        printerTools.printMessage(d(0))
        printerTools.printColorMessage(Console.CYAN,d(1))
        printerTools.printColorMessage(Console.GREEN,d(2))
      }
    })
  }

}

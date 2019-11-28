package actions

import tools.{diffTools, printerTools}

object diff {

  /**
    * Display diff
    */
  def diff(): Unit = {
    val diff = diffTools.showGeneralDiff()
    if (diff(0).isEmpty && diff(1).isEmpty && diff(2).isEmpty) {}
    else {
      printerTools.printMessage(diff(0))
      printerTools.printColorMessage(Console.CYAN,diff(1))
      printerTools.printColorMessage(Console.GREEN,diff(2))
    }
  }

}

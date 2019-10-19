package actions

import tools.{diffTools, printerTools}

object diff {

  /**
    * Display diff
    */
  def diff(): Unit = {
    printerTools.printMessage(diffTools.showGeneralDiff())
  }

}

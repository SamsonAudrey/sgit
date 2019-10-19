package tools
import Console.{RESET}

object printerTools {

  /**
    * Print simple message
    * @param message : String
    */
  def printMessage(message: String): Unit = {
    println(message)
  }

  /**
    * Print colored message
    * @param color : String
    * @param message : String
    */
  def printColorMessage(color: String,message: String): Unit = {
    Console.println(s"${RESET}$color$message${RESET}")
  }
}

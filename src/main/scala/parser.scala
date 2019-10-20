import java.io.File

import scopt.OParser
import tools.{printerTools, repoTools}

object parser {

  def main(args: Array[String]): Unit = {

    case class Config(
                       command: String = "",
                       files: List[String] = List(),
                       branch_tag: String = "",
                       commitMessage: String = "",
                       av: Boolean = false )

    val builder = OParser.builder[Config]


    val parser1 = {
      import builder._
      OParser.sequence(
        programName("sgit"),
        head("scopt", "4.x"),
        help("help").text("List of all commands."),
        cmd("init")
          .text("Initialize the repository.")
          .action((_, c) => c.copy(command = "init")),

        cmd("status")
          .text("Working directory status.")
          .action((_, c) => c.copy(command = "status")),

        cmd("diff")
          .text("Changes between working directory and stage area, working directory and commit, etc.")
          .action((_, c) => c.copy(command = "diff")),

        cmd("add")
          .text("Add files to the stage area.")
          .action((_, c) => c.copy(command = "add"))
          .children(
            arg[String]("<file> or <files> or .")
              .unbounded
              .required
              .action((x, c) => c.copy(files = c.files :+ x))
              .text("Files to add.")
          ),

        cmd("commit")
          .text("Commit changes to the repository.")
          .action((_, c) => c.copy(command = "commit"))
          .children(
            arg[String]("message")
              .required()
              .action((x, c) => c.copy(commitMessage = x))
              .text("Commit message.")
          ),

        cmd("branch")
          .text("Create or list branches.")
          .action((_, c) => c.copy(command = "branch"))
          .children(
            arg[String]("name")
              .optional()
              .action((x, c) => c.copy(branch_tag = x))
              .text("Name of the branch."),
            opt[Unit]("av")
              .action((_, c) => c.copy(av = true))
              .text("Display all the branches.")
          ),

        cmd("checkout")
          .text("Change working directory to the branch.")
          .action((_, c) => c.copy(command = "checkout"))
          .children(
            arg[String]("name")
              .required()
              .action((x, c) => c.copy(branch_tag = x))
              .text("Name of the branch."),
          ),

        cmd("tag")
          .text("Create a tag.")
          .action((_, c) => c.copy(command = "tag"))
          .children(
            arg[String]("name")
              .required
              .action((x, c) => c.copy(branch_tag = x))
              .text("Name of the tag.")
          ),

      )
    }

    OParser.parse(parser1, args, Config()) match {
      case Some(config) =>{
        config.command match {
          case "init" => {
            repoTools.getRoot(new File(repoTools.currentPath)) match {
              case Some(root) => {
                println("Already init.")
              }
              case None => {
                actions.init.initDirectory(repoTools.currentPath)
              }
            }
          }
          case _ => {
            repoTools.getRoot(new File(repoTools.currentPath)) match {
              case Some(root) => {
                config.command match {

                  case "add" => {
                    config.files match {
                      case x if config.files.contains(".") => {
                        actions.add.addAll()
                      }
                      case _ => actions.add.addMultipleFiles(config.files)
                    }
                  }
                  case "status" => {
                    actions.status.showGeneralStatus()
                  }
                  case "branch" => {
                    if (config.av) {
                      printerTools.printMessage(actions.branch.showAllBranches())
                    } else {
                      actions.branch.newBranch(config.branch_tag)
                    }
                  }
                  case "tag" => {
                    actions.tag.newTag(config.branch_tag)
                  }
                  case "checkout" => {
                    actions.branch.checkoutBranch(config.branch_tag)
                  }
                  case "commit" => {
                    actions.commit.commit(config.commitMessage)
                  }
                  case "diff" => {
                    actions.diff.diff()
                  }
                  case _ => {
                    println(
                      tools.printerTools.printColorMessage(
                        Console.RED,
                        "No repository found. Please, initialize one."
                      )
                    )
                  }
                }
              }
              case _ => {
                // arguments are bad, error message is displayed
              }
            }
          }
        }
      }
      case _ => {
        // arguments are bad, error message is displayed
      }
    }
  }
}

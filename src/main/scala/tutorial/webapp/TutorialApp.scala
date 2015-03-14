package tutorial.webapp

import lib.{Commit, Reference, Repository, GitHub}
import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.html.{Anchor, Element}
import rx.core.{Var, Rx}

import scala.scalajs.js
import scala.scalajs.js.{JSApp}
import org.scalajs.jquery.jQuery

import scala.util.{Failure, Success}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scalatags.JsDom.tags2.section

import scalatags.JsDom.all._

object TutorialApp extends JSApp {
  import Framework._

  val repositories: Var[Seq[Repository]] = Var(Seq[Repository]())

  val userInputBox = input(
    `id`:="userInputBox",
    autofocus:=true,
    autocomplete:=false,
    placeholder := "user name here.",
    value:="YusukeKokubo"
  ).render

  val userSubmit = button(
    `type`:="submit",
    onclick:={ () =>
      getRepositories(Var(userInputBox.value)())
      false
    })("send").render

  val errorMessage = Var("")

  case class Debug(url: String, res: String)

  val debug = Var(List[Debug]())

  def main(): Unit = {
    dom.document.getElementById("repositories").appendChild(setupUI())
    dom.document.getElementById("debug").appendChild(setupDebug())
    GitHub.hook = (url: String, res: String) => {
      debug() = Debug(url, res) :: debug()
    }
  }

  def setupUI(): Element = {
    section(
      Rx {
        div(`class`:="error")(
          p(errorMessage())
        )
      },
      form(userInputBox, userSubmit),
      Rx {
        ul(
          for (r <- repositories()) yield {
            val refs = Var(Seq[Reference]())
            li(referenceAnchor(r, refs),
              Rx {
                ul(
                  for(rf <- refs()) yield  {
                    val commits = Var(List[Commit]())
                    li(commitAnchor(r, rf.`object`.sha, rf.ref, commits),
                      Rx {
                        ul(
                          for (c <- commits()) yield {
                            li(`class`:="commit")(
                              label(c.author.date),
                              label(c.author.name),
                              label(c.message)
                            )
                          },
                          for (p <- if (!commits().isEmpty) commits().reverse.head.parents else Seq()) yield {
                            li(commitAnchor(r, p.sha, "parent", commits))
                          }
                        )
                      }
                    )
                  }
                )
              }
            )
          }
        )
      }
    ).render
  }

  def setupDebug(): Element = {
    div(`class`:="panel-group", role:="tablist", id:="accordion", aria.multiselectable:=true)(
      Rx {
        debug().zipWithIndex.map { case(d, i) =>
          div(`class` := "panel panel-default")(
            div(`class` := "panel-heading", role := "tab", id:="hedding" + i)(
              h4(`class` := "panel-title")(
                a(data.toggle := "collapse", data.parent := "#accordion", aria.expanded := false, aria.controls := "collapse" + i, href:="#collapse" + i)(d.url)
              )
            ),
            div(`class`:="panel-collapse collapse " + (if(i == 0) "in" else ""), role:="tabpanel", aria.labelledby:="hedding" + i, id:="collapse" + i, aria.expanded:=false)(
              div(`class`:="panel-body")(pre(d.res))
            )
          )
        }
      }
    ).render
  }

  def commitAnchor(repo: Repository, sha: String, caption: String, commits: Var[List[Commit]]): Element = {
    a(href:="#")(onclick:={() =>
      getCommit(Var(userInputBox.value)(), repo.name, sha, commits)
      false
    })(caption).render
  }

  def referenceAnchor(repo: Repository, refs: Var[Seq[Reference]]): Element = {
    a(href:="#")(onclick:={() =>
      getReferences(Var(userInputBox.value)(), repo.name, refs)
      false
    })(repo.full_name).render
  }

  def getRepositories(user: String): Unit = {
    if(user.isEmpty) {
      errorMessage() = "input user name."
      return
    }
    GitHub.repos(user).onComplete {
      case Success(msg) => repositories() = msg
      case Failure(t) => errorMessage() = t.getMessage
    }
  }

  def getReferences(owner: String, repo: String, result: Var[Seq[Reference]]): Unit = {
    GitHub.refs(owner, repo).onComplete {
      case Success(msg) => result() = msg
      case Failure(t) => errorMessage() = t.getMessage
    }
  }

  def getCommit(owner: String, repo: String, sha: String, result: Var[List[Commit]]): Unit = {
    GitHub.commit(owner, repo, sha).onComplete {
      case Success(msg) => result() = result() :+ msg
      case Failure(t) => errorMessage() = t.getMessage
    }
  }
}

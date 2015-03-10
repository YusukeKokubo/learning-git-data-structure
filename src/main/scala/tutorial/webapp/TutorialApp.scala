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

  val debug = Var(Seq[Debug]())

  def main(): Unit = {
    dom.document.body.appendChild(setupUI())
    dom.document.body.appendChild(setupDebug())
    GitHub.hook = (url: String, res: String) => {
      debug() = Seq(Debug(url, res)) ++ debug()
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
        ul(`class`:="repositories")(
          for (r <- repositories()) yield {
            val refs = Var(Seq[Reference]())
            li(referenceAnchor(r, refs),
              Rx {
                ul(
                  for(rf <- refs()) yield  {
                    val commit = Var[Option[Commit]](None)
                    li(commitAnchor(r, rf, commit),
                      Rx {
                        commit() match {
                          case Some(c) =>
                            div(`class`:="commit")(
                              label(commit().get.author.date),
                              label(commit().get.author.name),
                              label(commit().get.message)
                            )
                          case None => span()
                        }
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
    section(
      Rx {
        pre(`class`:="debug")(debug().map{d => "-----\n" + d.url + "\n" + d.res + "\n\n\n\n"})
      }
    ).render
  }

  def commitAnchor(repo: Repository, ref: Reference, commit: Var[Option[Commit]]): Element = {
    a(href:="#")(onclick:={() =>
      getCommit(Var(userInputBox.value)(), repo.name, ref.`object`.sha, commit)
      false
    })(ref.ref).render
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

  def getCommit(owner: String, repo: String, sha: String, result: Var[Option[Commit]]): Unit = {
    GitHub.commit(owner, repo, sha).onComplete {
      case Success(msg) => result() = Some(msg)
      case Failure(t) => errorMessage() = t.getMessage
    }
  }
}

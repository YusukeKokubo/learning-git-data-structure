package tutorial.webapp

import lib.{Reference, Repository, GitHub}
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

  def main(): Unit = {
    dom.document.body.appendChild(setupUI())
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
            li(r.name)(
              referenceAnchor(r, refs),
              Rx {
                ul(
                  for(rf <- refs()) yield  {
                    li(rf.ref)
                  }
                )
              }
            )
          }
        )
      }
    ).render
  }

  def referenceAnchor(repo: Repository, refs: Var[Seq[Reference]]): Element = {
    a(href:="#")(onclick:={() =>
      getReferences(Var(userInputBox.value)(), repo.name, refs)
      false
    })("refs").render
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
}

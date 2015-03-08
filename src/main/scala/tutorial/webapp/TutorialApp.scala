package tutorial.webapp

import lib.{Repository, GitHub}
import org.scalajs.dom
import org.scalajs.dom.Event
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

  val currentUser = Var("")

  def main(): Unit = {
    dom.document.body.appendChild(
      section(
        form(userInputBox,
          button(`type`:="submit", onclick:={ () =>
            GitHub.repos(Var(userInputBox.value)()).onComplete {
              case Success(msg) => repositories.update(msg)
              case Failure(t) => jQuery("body").append(t.getMessage)
            }
            false
          })("send")),
        Rx {
          ul(
            for (r <- repositories()) yield {
              li(
                r.name
              )
            }
          )
        }).render
    )
  }

}

package tutorial.webapp

import lib.{Repository, GitHub}
import org.scalajs.dom
import rx.core.{Var, Rx}

import scala.scalajs.js.{JSApp}
import org.scalajs.jquery.jQuery

import scala.util.{Failure, Success}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scalatags.JsDom.tags2.section

import scalatags.JsDom.all._

object TutorialApp extends JSApp {
  import Framework._

  val repositories: Var[Seq[Repository]] = Var(Seq[Repository]())

  def main(): Unit = {
    setupUI()
    dom.document.body.appendChild(
      section(
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

  def addClickedMessage(): Unit = {

    GitHub.repos("YusukeKokubo").onComplete {
      case Success(msg) => repositories.update(msg)
      case Failure(t) => jQuery("body").append(t.getMessage)
    }
  }

  def setupUI(): Unit = {
    jQuery("""<button type="button">Click me!</button>""")
      .click(addClickedMessage _)
      .appendTo(jQuery("body"))
  }
}

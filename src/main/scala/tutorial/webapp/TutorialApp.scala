package tutorial.webapp

import lib.{GitHub, Ajax}

import scala.scalajs.js
import scala.scalajs.js.{Dynamic, JSApp}
import org.scalajs.jquery.jQuery

import scala.util.{Failure, Success}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object TutorialApp extends JSApp {

  def main(): Unit = {
    jQuery(setupUI _)
  }

  def addClickedMessage(): Unit = {

    GitHub.repos("YusukeKokubo").onComplete {
      case Success(msg) => {
        jQuery("body").append("<ul>")
        msg.foreach(r =>
          jQuery("body").append("<li>" + r.name + "</li>")
        )
        jQuery("body").append("</ul>")
      }
      case Failure(t) => jQuery("body").append(t.getMessage)
    }
  }

  def setupUI(): Unit = {
    jQuery("body").append("<p>Hello World</p>")
    jQuery("""<button type="button">Click me!</button>""")
      .click(addClickedMessage _)
      .appendTo(jQuery("body"))
  }
}

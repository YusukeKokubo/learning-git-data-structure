package tutorial.webapp

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
    val text = for {
      res1 <- Ajax.get("https://api.github.com/users/YusukeKokubo/repos")
    } yield {
      res1.responseText
    }


    text.onComplete {
      case Success(msg) => {
        jQuery("body").append(msg)
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

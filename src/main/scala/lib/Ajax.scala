package lib

import org.scalajs.dom.{Event, XMLHttpRequest}

import scala.concurrent.{Future, Promise}

case class AjaxException(xhr: XMLHttpRequest) extends Exception

object Ajax {
  def get(url: String): Future[XMLHttpRequest] = apply("GET", url)

  def post(url: String, data: String): Future[XMLHttpRequest] = apply("POST", url, Option(data))

  def apply(method: String, url: String, data: Option[String] = None): Future[XMLHttpRequest] = {
    val req = new XMLHttpRequest()
    val promise = Promise[XMLHttpRequest]

//    req.withCredentials = true

    req.onreadystatechange = (e: Event) => {
      if (req.readyState.toInt == 4) {
        if (200 <= req.status && req.status < 300) {
          promise.success(req)
        } else {
          promise.failure(AjaxException(req))
        }
      }
    }

    req.open(method, url, true)

    data match {
      case Some(d) =>
        req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded")
        req.send(d)
      case None => req.send()
    }

    promise.future
  }
}

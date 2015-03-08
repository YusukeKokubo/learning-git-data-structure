package lib

import java.time.LocalDateTime

import scala.concurrent.Future

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js

import upickle._

case class Repository(
                       id: Int,
                       name: String,
                       full_name: String,
                       description: String,
//                       _private: Boolean,
                       fork: Boolean,
                       url: String,
                       html_url: String,
                       clone_url: String,
                       git_url: String,
                       homepage: String,
                       language: String,
                       forks_count: Int,
                       stargazers_count: Int,
                       watchers_count: Int,
                       size: Int,
                       default_branch: String,
                       open_issues_count: Int
//                       has_issues: Boolean,
//                       has_wiki: Boolean,
//                       has_pages: Boolean,
//                       has_downloads: Boolean,
//                       pushed_at: LocalDateTime,
//                       created_at: LocalDateTime,
//                       updated_at: LocalDateTime
                       )



object GitHub {

  def repos(user: String): Future[Seq[Repository]] = {

    val text = for {
      res1 <- Ajax.get("https://api.github.com/users/" + user + "/repos")
    } yield {
      read[Seq[Repository]](res1.responseText)
    }

    return text
  }
}


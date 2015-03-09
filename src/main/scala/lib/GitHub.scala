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

case class ReferenceObject(`type`: String, sha: String, url: String)

case class Reference(ref: String,
                     url: String,
                     `object`: ReferenceObject)


object GitHub {

  def repos(user: String): Future[Seq[Repository]] = {
    for {
      res1 <- Ajax.get("https://api.github.com/users/" + user + "/repos")
    } yield {
      read[Seq[Repository]](res1.responseText)
    }
  }

  def refs(owner: String, repo: String): Future[Seq[Reference]] = {
    for {
      res1 <- Ajax.get("https://api.github.com/repos/" + owner + "/" + repo + "/git/refs")
    } yield {
      read[Seq[Reference]](res1.responseText)
    }
  }
}


package lib

import scala.concurrent.Future

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

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

case class Author(date: String, name: String, email: String)

case class Commit(sha: String, url: String, author: Author, committer: Author, message: String, tree: Tree, parents: Seq[Tree])

case class Tree(url: String, sha: String)

object GitHub {

  var hook = (url: String, res: String) => {}

  def repos(user: String): Future[Seq[Repository]] = {
    val url = "https://api.github.com/users/" + user + "/repos"
    for {
      res1 <- Ajax.get(url)
    } yield {
      hook.apply(url, res1.responseText)
      read[Seq[Repository]](res1.responseText)
    }
  }

  def refs(owner: String, repo: String): Future[Seq[Reference]] = {
    val url = "https://api.github.com/repos/" + owner + "/" + repo + "/git/refs"
    for {
      res1 <- Ajax.get(url)
    } yield {
      hook.apply(url, res1.responseText)
      read[Seq[Reference]](res1.responseText)
    }
  }

  def commit(owner: String, repo: String, sha: String): Future[Commit] = {
    val url = "https://api.github.com/repos/" + owner + "/" + repo + "/git/commits/" + sha
    for {
      res1 <- Ajax.get(url)
    } yield {
      hook.apply(url, res1.responseText)
      read[Commit](res1.responseText)
    }
  }
}


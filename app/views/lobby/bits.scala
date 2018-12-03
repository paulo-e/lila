package views.html.lobby

import scalatags.Text.all._

import lila.api.Context
import lila.app.templating.Environment._
import lila.i18n.{ I18nKeys => trans }

import controllers.routes

object bits {

  def leaderboards(
    leaderboard: List[lila.user.User.LightPerf],
    tournamentWinners: List[lila.tournament.Winner]
  )(implicit ctx: Context) =
    div(cls := "leaderboards undertable")(
      div(
        div(cls := "undertable_top")(
          a(cls := "more", href := routes.User.list)(trans.more(), " »"),
          span(cls := "title text", dataIcon := "C")(trans.leaderboard())
        ),
        div(cls := "undertable_inner scroll-shadow-hard")(
          table(tbody(
            leaderboard map { l =>
              tr(
                td(lightUserLink(l.user)),
                lila.rating.PerfType(l.perfKey) map { pt =>
                  td(cls := "text", dataIcon := pt.iconChar)(l.rating)
                },
                td(showProgress(l.progress, withTitle = false))
              )
            }
          ))
        )
      ),
      div(
        div(cls := "undertable_top")(
          a(cls := "more", href := routes.Tournament.leaderboard)(trans.more(), " »"),
          span(cls := "title text", dataIcon := "g")(trans.tournamentWinners())
        ),
        div(cls := "undertable_inner scroll-shadow-hard")(
          table(tbody(
            tournamentWinners take 10 map { w =>
              tr(
                td(userIdLink(w.userId.some)),
                td(a(title := w.tourName, href := routes.Tournament.show(w.tourId))(scheduledTournamentNameShortHtml(w.tourName)))
              )
            }
          ))
        )
      )
    )

  def lastPosts(posts: List[lila.blog.MiniPost])(implicit ctx: Context): Option[Frag] = posts.nonEmpty option
    div(cls := "blog undertable")(
      div(
        div(cls := "undertable_top")(
          a(cls := "more", href := routes.Blog.index())(trans.more(), " »"),
          span(cls := "title text", dataIcon := "6")(trans.latestUpdates())
        ),
        div(cls := "undertable_inner")(
          posts map { post =>
            a(cls := "post", href := routes.Blog.show(post.id, post.slug))(
              img(src := post.image, width := "60", height := "60"),
              span(cls := "text")(
                span(cls := "title")(post.title),
                p(cls := "shortlede")(post.shortlede)
              ),
              semanticDate(post.date)
            )
          }
        )
      )
    )
}

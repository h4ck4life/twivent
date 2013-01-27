import scala.Some.apply
import scala.annotation.implicitNotFound
import scala.concurrent.duration.DurationInt
import akka.actor.Props.apply
import play.api.Application
import play.api.GlobalSettings
import play.api.Logger
import play.api.Play
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.libs.Akka
import akka.actor.Props

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    val controllerPath = controllers.routes.Ping.ping.url
    val urlToPing = play.api.Play.mode(app) match {
      case play.api.Mode.Prod => "http://twivent.herokuapp.com%s".format(controllerPath)
      case _ => "http://localhost:9000%s".format(controllerPath)
    }

    val pingActor = Akka.system.actorOf(Props(new PingActor(urlToPing)))
    Logger.info("Scheduling ping on " + urlToPing)
    Akka.system.scheduler.schedule(0 seconds, 10 minutes, pingActor, "ping")
  }

}
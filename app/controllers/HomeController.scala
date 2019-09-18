package controllers

import javax.inject._
import models.GoogleAuth
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val auth = GoogleAuth()

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def getSecret() = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(Map("secret" -> auth.secret)))
  }

  def getCode() = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(Map("code" -> auth.getCode)))
  }

  def verifyCode() = Action { implicit  request: Request[AnyContent] =>
    val inp = request.getQueryString("inputCode").get
    Ok(Json.toJson(Map("success" -> auth.verifyCode(inp))))
  }
}

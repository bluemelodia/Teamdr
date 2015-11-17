package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

// Takes requests from browser and decides how to process them
public class Application extends Controller {

    public Result index() {
        return redirect(routes.Account.signIn());
    }

    public Result errorPage() {
        return ok(index.render("Boss kitty."));
    }

}

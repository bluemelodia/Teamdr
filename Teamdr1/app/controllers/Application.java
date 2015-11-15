package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

// Takes requests from browser and decides how to process them
public class Application extends Controller {

    public Result errorPage() {
        return ok(index.render("Boss kitty."));
    }

}

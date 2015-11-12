package controllers;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Account extends Controller {
    public Result list() {
        return TODO;
    }

    public Result newAccount() {
        return ok(account.render());
        //return ok(index.render("Boss kitty2.")); // Account refers to the account in account.scala.html
    }

    public Result signIn() {
        return TODO;
    }
}

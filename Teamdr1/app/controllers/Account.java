package controllers;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by bluemelodia on 11/11/15.
 */
public class Account extends Controller {
    public Result list() {
        return TODO;
    }

    public Result newAccount() {
        return ok(info.render()); // Account refers to the account in account.scala.html
    }

    public Result signIn() {
        return TODO;
    }
}

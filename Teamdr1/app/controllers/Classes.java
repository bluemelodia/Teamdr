package controllers;

import models.*;
import play.mvc.Controller;
import play.data.Form;
import play.mvc.Result;
import views.html.*;
import java.util.List;

/**
 * Created by bluemelodia on 11/12/15.
 */

public class Classes extends Controller {
    private static final Form<ClassRecord> ClassForm = Form.form(ClassRecord.class);

    public Result leaveClass(String classId) {
        String user = session("connected");
        UserAccount userAccount = UserAccount.getUser(user);

        // Check if the user has a team for this class; if not, drop them from the class straight away
        if (TeamRecord.getTeamForClass(user, classId) == null) {
            userAccount.removeClass(classId);
            String announcement = "You have dropped " + classId;
            return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user)));
        }
        // User has a team for this class
        TeamRecord myTeam = TeamRecord.getTeamForClass(user, classId);
        String oldTeam = myTeam.tid;
        String[] teamMembers = myTeam.teamMembers.split(" ");
        for (String member: teamMembers) {
            if (member.equals(user)) {
                System.out.println(user + " purged");
                myTeam.teamMembers = myTeam.teamMembers.replace(user + " ", ""); // purge user from team
            }
        }
        System.out.println("Team members: " + myTeam.teamMembers);
        System.out.println("User purged"); // TODO: check this works*
        System.out.println("TEAM " + myTeam.tid + " has " + myTeam.teamMembers.trim().length());
        if (myTeam.teamMembers.trim().length() < 1) { // no people left, delete the newly emptied team
            System.out.println("PURGE team: " + myTeam.tid);
            List<TeamRecord> allTeams = TeamRecord.findAll();
            for (TeamRecord team: allTeams) { // remove this team from all seen lists
                if (team.tid.equals(myTeam.tid)) continue;
                team.seenTeams.replace(myTeam.tid + " ", "");
            }
            String teamName = myTeam.tid;
            myTeam.delete();
            userAccount.removeClass(classId);
            myTeam.save();
            String announcement = "Team " + teamName + " has disbanded.";
            return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user)));
        }

        System.out.println(myTeam.teamMembers);
        myTeam.save();
        System.out.println(user + " left");
        // Notify the rest of the team that you have left the team
        String message = user + " has left team " + myTeam.teamName + " for " + classId;
        for (String member: teamMembers) {
            if (member.equals(user)) continue;
            System.out.println("I am here");
            UserAccount moi = UserAccount.getUser(member);
            Notification.createNewNotification(moi.username, moi.currentClass, 3, myTeam.tid, message);
        }
        userAccount.removeClass(classId);
        String announcement = "Because you have dropped " + classId + ", you were removed from team " + oldTeam;
        return ok(profile.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user)));
    }

    public Result retrieveClass() {
        return ok(classes.render(ClassForm));
    }

    public Result putClass() {
        // grab HTML form that was sent to this method, and extracts relevant fields from it
        Form<ClassRecord> form = ClassForm.bindFromRequest();
        ClassRecord newClass = form.get();
        // save the data sent through HTTP POST
        System.out.println("newClass: " + newClass.classID + " " + newClass.className);
        newClass.save();

        List<ClassRecord> classes = ClassRecord.findAll();
        for (int i = 0; i < classes.size(); i++) {
            System.out.println(classes.get(i).classID + " " + classes.get(i).className);
        }

        return redirect(routes.Classes.retrieveClass());
    }
}

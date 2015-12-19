package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import models.TeamRecord;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

import java.util.*;

import static play.libs.Json.toJson;
/**
 * Created by anfalboussayoud on 11/11/15.
 */
public class Team extends Controller {
    public Result rateUser(String rated, String rating) {
        System.out.println("CRAZY");
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        System.out.println("Ratings");
        //int rating = Integer.parseInt(json.get("rating").toString());
        System.out.println("RATE: " + rated + " RATING: " + rating);
        try { // if you enter anything other than a number, you're hacking
            Integer.parseInt(rating);
        } catch(NumberFormatException e) {
            return redirect(routes.Profile.viewProfile());
        }
        int rate = Integer.parseInt(rating);
        if (rate < 1 || rate > 5) { // clearly you're hacking if the rating is outside of the 1-5 range
            return redirect(routes.Profile.viewProfile());
        }

        String ratedUser = rated.replaceAll("[^A-Za-z0-9]", "");

        // does this user exist? if you weren't hacking, they should...
        if (!UserAccount.exists(ratedUser)) {
            return redirect(routes.Profile.viewProfile());
        }
        // you are not allowed to rate yourself
        if (ratedUser.equals(user)) {
            return redirect(routes.Profile.viewProfile());
        }

        // are you in any class?
        List<String> thisClasses = UserAccount.getUser(user).getClassList();
        if (thisClasses.size() < 1) {
            return redirect(routes.Profile.viewProfile());
        }

        // are they still in this class?
        List<String> theirClasses = UserAccount.getUser(ratedUser).getClassList();
        if (!theirClasses.contains(UserAccount.getUser(user).currentClass)) {
            String announcement = ratedUser + " is not in " + UserAccount.getUser(user).currentClass;
            return badRequest(toJson(announcement));
        }

        // are you still in a team for this class?
        if (TeamRecord.getTeamForClass(user, UserAccount.getUser(user).currentClass) == null) {
            System.out.println("No team for class");
            String announcement = "You don't have a team for " + UserAccount.getUser(user).currentClass;
            return badRequest(toJson(announcement));
        }

        // are they still in a team for this class?
        if (TeamRecord.getTeamForClass(ratedUser, UserAccount.getUser(ratedUser).currentClass) == null) {
            System.out.println("No team for class");
            String announcement = ratedUser + " doesn't have a team for " + UserAccount.getUser(user).currentClass;
            return badRequest(toJson(announcement));
        }

        // you have a team for this class, now get their team for this class
        TeamRecord yourTeam = TeamRecord.getTeamForClass(user, UserAccount.getUser(user).currentClass);
        TeamRecord theirTeam = TeamRecord.getTeamForClass(ratedUser, UserAccount.getUser(ratedUser).currentClass);

        if (theirTeam == null) {
            return badRequest(toJson(ratedUser + " no longer in this class."));
        }

        // are you in the same class as this person?
        String theirClass = theirTeam.thisClass;
        List<String> myClasses = UserAccount.getUser(user).getClassList();
        if (!myClasses.contains(theirClass)) {
            return badRequest("You can't rate " + ratedUser + " without being in the same class as them.");
        }

        System.out.println(yourTeam.teamName + " vs " + theirTeam.teamName);
        // are you even in the same team as the person you're rating?
        if (!yourTeam.teamName.equals(theirTeam.teamName)) {
            System.out.println("CANNOT RATE NON-TEAM");
            return badRequest(toJson("You cannot rate " + ratedUser + " who isn't on your team."));
        }

        // have you rated this person before?
        if (UserAccount.getUser(ratedUser).hasRated(user)) {
            return badRequest(toJson("You've already rated " + ratedUser));
        }

        // ok, you can rate this user
        UserProfile.getUser(ratedUser).rate(rate);
        UserAccount.getUser(ratedUser).addRater(user); // save the rating

        // send rating notification to the recipient
        String message = "You received a rating for " + yourTeam.thisClass + ".";
        Notification.createNewNotification(ratedUser, yourTeam.thisClass, 4, yourTeam.teamName, message);

        return ok(toJson("You gave " + ratedUser + " a " + rate + "."));
    }

    public Result teamDetails(String classId) {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        // check if this class exists
        List<ClassRecord> cs = ClassRecord.findAll();
        // check if class exists, blocks URL hacking
        boolean found = false;
        for (int i = 0; i < cs.size(); i++) {
            if(classId.equals(cs.get(i).classID)) {
                found = true;
            }
        }
        if (!found) {
            return redirect(routes.Profile.viewProfile());
        }
        // check if the user is in the class, otherwise don't let them do this
        UserAccount thisUser = UserAccount.getUser(session("connected")); // get this user
        List<String> classes = thisUser.getClassList();
        if (!classes.contains(classId)) {
            return redirect(routes.Profile.viewProfile());
        }
        TeamRecord myTeam = TeamRecord.getTeamForClass(user, classId);
        ArrayList<UserAccount> members = new ArrayList<>();
        if (myTeam != null) {
            String[] theTeam = myTeam.teamMembers.split(" ");
            System.out.println("TEAM: " + myTeam.teamMembers);
            for (String member: theTeam) {
                members.add(UserAccount.getUser(member));
            }
        }
        return ok(teamdetails.render(UserProfile.getUser(user), UserAccount.getUser(user), Notification.getNotifs(user), myTeam, members));
    }

    public Result leaveTeam(String classId) {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        System.out.println("CLASS ID: " + classId);

        // check if class exists, blocks URL hacking
        List<ClassRecord> cs = ClassRecord.findAll();
        boolean found = false;
        for (int i = 0; i < cs.size(); i++) {
            if(classId.equals(cs.get(i).classID)) {
                found = true;
            }
        }
        if (!found) {
            String error = classId + " does not exist.";
            return badRequest(toJson(error));
        }
        System.out.println("Class does exist");
        System.out.println("Your current class: " + UserAccount.getUser(user).currentClass);

        System.out.println("You're in this class");
        // Check if user is currently in this class
        boolean foundClass = false;
        List<String> allClasses = UserAccount.getUser(user).getClassList();
        for (int i = 0; i < allClasses.size(); i++) {
            if (allClasses.get(i).equals(classId)) {
                foundClass = true;
            }
        } if (!foundClass) { // ok, because we want to refresh the page
            return ok(toJson("You are no longer in " + classId));
        }

        // Check if the user has a team for this class
        if (TeamRecord.getTeamForClass(user, classId) == null) {
            System.out.println("No team for class");
            String announcement = "You don't have a team for " + classId;
            return badRequest(toJson(announcement));
        }

        TeamRecord myTeam = TeamRecord.getTeamForClass(user, classId);
        String oldTeam = myTeam.tid;
        String[] teamMembers = myTeam.teamMembers.split(" ");
        for (String member: teamMembers) {
            if (member.equals(user)) {
                System.out.println(user + " purged");
                myTeam.teamMembers = myTeam.teamMembers.replace(user + " ", ""); // purge user from team
            }
        }
        System.out.println("TEAM " + myTeam.tid + " has " + myTeam.teamMembers.trim().length());
        if (myTeam.teamMembers.trim().length() < 1) { // no people left, delete the newly emptied team
            List<TeamRecord> allTeams = TeamRecord.findAll();
            for (TeamRecord team: allTeams) { // remove this team from all seen lists
                if (team.tid.equals(myTeam.tid)) continue;
                team.seenTeams.replace(myTeam.tid + " ", "");
            }
            String teamName = myTeam.tid;
            myTeam.delete();
            myTeam.save();
            String announcement = "Team " + teamName + " has disbanded.";

            // remove this team from everyone's seen list
            List<TeamRecord> everyTeam = TeamRecord.findAll();
            for (int i = 0; i < everyTeam.size(); i++) {
                TeamRecord thisTeam = everyTeam.get(i);
                String[] seen = thisTeam.seenTeams.split(" ");
                ArrayList<String> seenTeamsArr = new ArrayList<String>();
                for (int j = 0; j < seen.length; j++) {
                    seenTeamsArr.add(seen[j].trim());
                }
                if (seenTeamsArr.contains(teamName)) {
                    thisTeam.seenTeams = "";
                    for (int k = 0; k < seenTeamsArr.size(); k++) {
                        if (seenTeamsArr.get(k).equals(teamName)) {
                            continue;
                        }
                        thisTeam.seenTeams += seenTeamsArr.get(k) + " ";
                    }
                    thisTeam.save();
                }
            }

            return ok(toJson(announcement));
        }

        System.out.println(myTeam.teamMembers);
        myTeam.save();
        System.out.println(user + " left");
        // Notify the rest of the team that you have left the team
        String message = user + " has left team " + myTeam.teamName + " for " + classId;
        for (String member: teamMembers) {
            if (member.equals(user)) continue; // don't need to message yourself
            UserAccount moi = UserAccount.getUser(member);
            Notification.createNewNotification(moi.username, moi.currentClass, 3, myTeam.tid, message);
        }
        String announcement = "You have left " + oldTeam;
        return ok(toJson(announcement));
    }

    // Retrieve a team that you have not yet seen
    public TeamRecord showCurrentTeam(String username) {
        UserAccount thisUser = UserAccount.getUser(username);
        System.out.println("me: " + thisUser.username);

        // is user in this class?
        List<String> myClasses = UserAccount.getUser(thisUser.username).getClassList();
        if (!myClasses.contains(thisUser.currentClass)) {
            return null;
        }

        // is user in a team for this class?
        if (TeamRecord.getTeamForClass(thisUser.username, thisUser.currentClass) == null) {
            return null;
        }

        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (TeamRecord team: allTeams) {
            //System.out.println("Team: " + team.teamName + " id: " + team.tid + " members: " + team.teamMembers);
            if (!team.thisClass.equals(thisUser.currentClass)) {
                continue;
            }
            System.out.println(team.teamMembers);
            String[] teamMembers = (team.teamMembers).split(" ");
            Boolean myTeam = false;
            for (int i = 0; i < teamMembers.length; i++) {
                if (teamMembers[i].equals(thisUser.username)) {
                    System.out.println("This is my team...");
                    myTeam = true;
                }
            }
            if (myTeam) continue; // don't return your own team!
            if (TeamRecord.haveSeenTeam(thisUser.username, thisUser.currentClass, team.tid)) {
                continue;
            } else {
                return team;
            }
        }
        System.out.println("There are no other teams");
        return null;
    }
    
    /*public Result showError() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        JsonNode errorJson = toJson("I think you are lost...");
        return ok(errorPage.render(errorJson));
    }*/

    // This method helps pick the correct class, based on form submission, for the team search
    public Result setCurrentClass(String classId) {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        System.out.println("CLASS: " + classId);
        List<ClassRecord> cs = ClassRecord.findAll();
        // check if class exists, blocks URL hacking
        boolean found = false;
        for (int i = 0; i < cs.size(); i++) {
            if(classId.equals(cs.get(i).classID)) {
                found = true;
            }
        }
        if (!found) {
            return redirect(routes.Profile.viewProfile());
        }

        // check if the user is in the class, otherwise don't let them do this
        UserAccount thisUser = UserAccount.getUser(session("connected")); // get this user
        List<String> classes = thisUser.getClassList();
        if (!classes.contains(classId)) {
            return redirect(routes.Profile.viewProfile());
        }

        thisUser.changeCurrentClass(classId); // set the current class
        System.out.println("currentClass changed to: " + classId);
        String currentClass = thisUser.currentClass;

        // If the user does not have a team for this class, have them make a new team
        if (!hasTeam(currentClass, thisUser)) {
            return ok(createteam.render(ClassRecord.getClass(currentClass)));
        }
        return redirect(routes.Team.showTeams());
    }

    public Result showTeams() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        UserAccount thisUser = UserAccount.getUser(user);
        TeamRecord currentTeam = showCurrentTeam(user);
        JsonNode currentTeamJSON;
        if (currentTeam == null) {
            System.out.println("There are no teams...leave!");
            String noTeams = "There are no other teams in this class, or you have swiped on all of them";
            JsonNode errorJson = toJson(noTeams);
            //TODO: just make this into an alert when no teams in the class
            return ok(error.render(UserAccount.getUser(user).currentClass)); // There are no other teams available
        } else {
            List<UserProfile> teamDetails = new ArrayList<>();

            // Get the team info
            TeamRecord teamToDisplay = TeamRecord.getTeam(currentTeam.tid);
            // First, eliminate all double spaces
            String[] members = (teamToDisplay.teamMembers).split(" ");
            for (int i = 0; i < members.length; i++) { // Get all member descriptions
                System.out.println("CURRENT MEMBER: [" + members[i] + "]");
                UserAccount currentUser = UserAccount.getUser(members[i]);
                System.out.println(members[i] + " username: " + currentUser.username);
                if (currentUser == null || currentUser.username.length() < 1) continue;
                teamDetails.add(UserProfile.getUser(currentUser.username));
            }
            /*if (teamDetails.length() > 0 && teamDetails.charAt(teamDetails.length()-1)==',') {
                teamDetails = teamDetails.substring(0, teamDetails.length()-1);
            } if (teamDetails.length() >= 255) { // Varchar can only have 255 chars
                teamDetails = teamDetails.substring(0, 251);
                teamDetails += "...";
            }*/
            JsonNode teamMembers = toJson(teamDetails);
            currentTeamJSON = toJson(currentTeam);
            JsonNode className = toJson(thisUser.currentClass);
            JsonNode errorMessage = toJson("");
            return ok(team.render(currentTeam, teamDetails));
        }
    }

    public Result swipeRight(String teamName) {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        UserAccount thisUser = UserAccount.getUser(user);
        String thisTeam = teamName.toString().replaceAll("[^A-Za-z0-9]", "");
        // don't go swiping if you aren't even in this class
        List<String> thisClasses = UserAccount.getUser(user).getClassList();
        if (thisClasses.size() < 1) {
            return redirect(routes.Profile.viewProfile());
        }
        if (!thisClasses.contains(UserAccount.getUser(user).currentClass)) {
            return redirect(routes.Profile.viewProfile());
        }

        // don't go swiping when you don't have a team for this class
        if (TeamRecord.getTeamForClass(user, UserAccount.getUser(user).currentClass) == null) {
            System.out.println("OOPS!!!!");
            return badRequest(toJson("You must be in a team for this class to swipe."));
        }

        if (!TeamRecord.exists(thisTeam)) {
            System.out.println("DNE");
            return badRequest(toJson("Team you swiped on does not exist."));
        }
        // don't swipe on your own team, this isn't even possible unless you're trying to hack
        if (thisTeam.equals(TeamRecord.getTeamForClass(user, UserAccount.getUser(user).currentClass))) {
            return redirect(routes.Profile.viewProfile());
        }

        // get all the classes and teams you are in, you can't swipe on any of these
        List<String> allClasses = UserAccount.getUser(user).getClassList();
        for (int i = 0; i < allClasses.size(); i++) {
            TeamRecord currentTeam = TeamRecord.getTeamForClass(user, allClasses.get(i));
            if (currentTeam == null) continue;
            System.out.println("CURRENT TEAM: " + currentTeam.teamName);
            if (thisTeam.equals(currentTeam.teamName)) {
                System.out.println("GET OUT OT HERE");
                return redirect(routes.Profile.viewProfile());
            }
        }

        // don't swipe on a team that's not even in the class you're searching for
        if (!TeamRecord.getTeam(thisTeam).thisClass.equals(UserAccount.getUser(user).currentClass)) {
            return redirect(routes.Profile.viewProfile());
        }
        System.out.println("This team exists!!!!!");
        System.out.println("RIGHT: " + thisTeam);
        TeamRecord.addSeenTeam(thisUser.username, thisUser.currentClass, thisTeam);

        String seenTeams = TeamRecord.getSeenTeams(thisUser.username, thisUser.currentClass);
        System.out.println("Seen teams: " + seenTeams);

        // If the user already received an invite to join this team, send them to the notifs page
        List<Notification> notifs = Notification.getNotifs(user);
        for (int j = 0; j < notifs.size(); j++) {
            Notification currentNotif = notifs.get(j);
            // This user was already invited to join this team
            if (currentNotif.classID.equals(thisUser.currentClass) && currentNotif.teamID.equals(thisTeam)) {
                return ok(toJson("You already have an invitation to join this team. Go to notifications."));
            }
        }
        // TeamRecord currentTeam = showCurrentTeam(user);
        //TeamRecord td = TeamRecord.getTeam(thisTeam);

        // Get the user's team
        TeamRecord myTeam = TeamRecord.getTeamForClass(user, thisUser.currentClass);

        String teamDetails = "Invited to team: " + myTeam.tid + "/n Team Members:";
        String[] members = (myTeam.teamMembers).split(" ");
        for (int i = 0; i < members.length; i++) { // Get all member descriptions
            UserAccount currentUser = UserAccount.getUser(members[i]);
            teamDetails += "    " + currentUser.username.toString() + " (";
            UserProfile currentProfile = UserProfile.getUser(currentUser.username);
            teamDetails += "        " + currentProfile.description + "),";
        }
        // Remove the comma at the end
        if (teamDetails.length() > 0 && teamDetails.charAt(teamDetails.length()-1)==',') {
            teamDetails = teamDetails.substring(0, teamDetails.length()-1);
        } if (teamDetails.length() >= 255) { // Varchar can only have 255 chars
            teamDetails = teamDetails.substring(0, 251);
            teamDetails += "...";
        }

        TeamRecord td = TeamRecord.getTeam(thisTeam);
        String[] people = (td.teamMembers).split(" ");
        for (int i = 0; i < people.length; i++) {
            UserAccount currentUser = UserAccount.getUser(people[i]);
            Notification.createNewNotification(currentUser.username, thisUser.currentClass, 1, myTeam.tid, teamDetails);
        }
        //TODO: issue with this not refreshing into the next team
        TeamRecord.addSeenTeam(thisUser.username, thisUser.currentClass, thisTeam);
        //update team with currently shown team and user
        //td = td.updateTeam(thisTeam, user);
        //System.out.println("new team " + td.teamMembers);
        //td.save();
        return ok(toJson("You sent a request to join " + thisTeam));
    }

    // Swipe left: go to the next team, mark this one as seen
    public Result swipeLeft(String teamName) {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };
        UserAccount thisUser = UserAccount.getUser(user);

        JsonNode json = request().body().asJson();
        System.out.println("HIHIHIHI");
        System.out.println(teamName);
        String thisTeam = teamName.toString().replaceAll("[^A-Za-z0-9]", "");
        System.out.println("THIS TEAM: " + thisTeam);

        // don't go swiping if you aren't even in this class
        List<String> thisClasses = UserAccount.getUser(user).getClassList();
        if (thisClasses.size() < 1) {
            return redirect(routes.Profile.viewProfile());
        }
        if (!thisClasses.contains(UserAccount.getUser(user).currentClass)) {
            return redirect(routes.Profile.viewProfile());
        }

        // don't go swiping when you don't have a team for this class
        if (TeamRecord.getTeamForClass(user, UserAccount.getUser(user).currentClass) == null) {
            return badRequest(toJson("You must be in a team for this class to swipe."));
        }

        if (!TeamRecord.exists(thisTeam)) {
            return badRequest(toJson("Team you swiped on does not exist."));
        }
        // don't swipe on your own team, this isn't even possible unless you're trying to hack
        if (thisTeam.equals(TeamRecord.getTeamForClass(user, UserAccount.getUser(user).currentClass))) {
            return redirect(routes.Profile.viewProfile());
        }

        // get all the classes and teams you are in, you can't swipe on any of these
        List<String> allClasses = UserAccount.getUser(user).getClassList();
        for (int i = 0; i < allClasses.size(); i++) {
            TeamRecord currentTeam = TeamRecord.getTeamForClass(user, allClasses.get(i));
            if (currentTeam == null) continue;
            System.out.println("CURRENT TEAM: " + currentTeam.teamName);
            if (thisTeam.equals(currentTeam.teamName)) {
                System.out.println("GET OUT OT HERE");
                return redirect(routes.Profile.viewProfile());
            }
        }

        // don't swipe on a team that's not even in the class you're searching for
        if (!TeamRecord.getTeam(thisTeam).thisClass.equals(UserAccount.getUser(user).currentClass)) {
            return redirect(routes.Profile.viewProfile());
        }

        System.out.println("LEFT: " + thisTeam);
        TeamRecord.addSeenTeam(thisUser.username, thisUser.currentClass, thisTeam);

        String seenTeams = TeamRecord.getSeenTeams(thisUser.username, thisUser.currentClass);
        System.out.println("Seen teams: " + seenTeams);

        System.out.println("This team: " + thisTeam);
        //System.out.println("SEEN TEAMS: " + seenTeams);
        return ok(toJson("You rejected " + thisTeam));
    }

    public Result showCreateTeamPage() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        };
        UserAccount thisUser = UserAccount.getUser(user);
        return ok(createteam.render(ClassRecord.getClass(thisUser.currentClass)));
    }

    // Create a team, validate params before adding the new team to the database
    public Result createTeam() {
        String user = session("connected");
        if (user == null) { // unauthorized user login, kick them back to login screen
            return redirect(routes.Account.signIn());
        }
        JsonNode json = request().body().asJson();
        String teamName = json.get("teamName").toString().replaceAll("[^A-Za-z0-9]", "");
        String tid = json.get("teamID").toString().replaceAll("[^A-Za-z0-9]", "");
        UserAccount thisUser = UserAccount.getUser(user);
        JsonNode className = toJson(thisUser.currentClass);
        // don't go swiping if you aren't even in this class
        System.out.println("HERE");
        List<String> thisClasses = UserAccount.getUser(user).getClassList();
        System.out.println("THIS CLASSES: " + thisClasses);
        if (thisClasses.size() < 1) {
            return badRequest(toJson("You aren't in any classes."));
        }
        if (!thisClasses.contains(UserAccount.getUser(user).currentClass)) {
            return badRequest(toJson("You aren't in this class."));
        }

        if (teamName.length() < 1 && tid.length() < 1) {
            return badRequest(toJson("Team Name and Team ID are required."));
        } else if (teamName.length() < 1) {
            return badRequest(toJson("TeamName is required."));
        } else if (tid.length() < 1) {
            return badRequest(toJson("Team ID is required."));
        }

        // Check to see if the user is still in this class
        List<String> userClasses = UserAccount.getUser(user).getClassList();
        System.out.println("Classes: " + userClasses);
        System.out.println("Current class: " + thisUser.currentClass);
        if (userClasses.size() < 1 || !userClasses.contains(thisUser.currentClass)) {
            return badRequest(toJson("You must be in this class to make a team."));
        }

        // Check to see if the team already exists
        if (TeamRecord.exists(tid)) {
            String error = "Team " + tid + " already exists!";
            return badRequest(toJson(error));
        }

        // User cannot have more than one team for this class
        if (hasTeam(thisUser.currentClass, thisUser)) {
            return badRequest(toJson("You already have a team for this class."));
        } else {
            // Create a new team
            TeamRecord.createTeamRecord(tid, thisUser, teamName, thisUser.currentClass);

            // Code to check the created teams
            List<TeamRecord> allTeams = TeamRecord.findAll();
            for (int i = 0; i < allTeams.size(); i++) {
                TeamRecord team = allTeams.get(i);
                System.out.println("Name: " + team.teamName);
                System.out.println("Team: " + team.teamMembers);
                System.out.println("Class: " + team.thisClass);
                System.out.println();
            }
            // Redirect to the show teams page
            return ok(toJson("Successfully created team " + teamName));
        }
    }

    // Each student can only be in one team for each class
    private Boolean hasTeam(String currentClass, UserAccount thisUser) {
        List<TeamRecord> allTeams = TeamRecord.findAll();
        for (int i = 0; i < allTeams.size(); i++) {
            TeamRecord thisTeam = allTeams.get(i);
            if (!thisTeam.thisClass.equals(thisUser.currentClass)) {
                continue;
            }
            String team = thisTeam.teamMembers;
            if (team == null || team.length() < 1) continue;
            String[] teamMates = team.split(" ");
            for (int j = 0; j < teamMates.length; j++) {
                if (teamMates[j].equals(thisUser.username)) {
                    return true;
                }
            }
        }
        return false;
    }
}
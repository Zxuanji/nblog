package sg.nus.iss.blog.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String displayName;
    private String profilePicture;
    private String location;
    private String education;
    private String aboutMe;
    private String myTechStack;
    private String githubLink;
    private String linkedinLink;
    private UserStatusEnum userStatus;
    
    @OneToMany(mappedBy = "user")
    private List<Blog> postedBlogs;

    
    public User() {
    }

    public User(String displayName, String profilePicture, String location, String education, String aboutMe,
            String myTechStack, String githubLink, String linkedinLink, UserStatusEnum userStatus,
            List<Blog> postedBlogs) {
        this.displayName = displayName;
        this.profilePicture = profilePicture;
        this.location = location;
        this.education = education;
        this.aboutMe = aboutMe;
        this.myTechStack = myTechStack;
        this.githubLink = githubLink;
        this.linkedinLink = linkedinLink;
        this.userStatus = userStatus;
        this.postedBlogs = postedBlogs;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getMyTechStack() {
        return myTechStack;
    }

    public void setMyTechStack(String myTechStack) {
        this.myTechStack = myTechStack;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public String getLinkedinLink() {
        return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
        this.linkedinLink = linkedinLink;
    }

    public UserStatusEnum getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatusEnum userStatus) {
        this.userStatus = userStatus;
    }

    public List<Blog> getPostedBlogs() {
        return postedBlogs;
    }

    public void setPostedBlogs(List<Blog> postedBlogs) {
        this.postedBlogs = postedBlogs;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", displayName=" + displayName + ", profilePicture=" + profilePicture
                + ", location=" + location + ", education=" + education + ", aboutMe=" + aboutMe + ", myTechStack="
                + myTechStack + ", githubLink=" + githubLink + ", linkedinLink=" + linkedinLink + ", userStatus="
                + userStatus + ", postedBlogs=" + postedBlogs + "]";
    }

    
    

}

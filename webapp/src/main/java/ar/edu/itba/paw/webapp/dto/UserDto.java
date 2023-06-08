package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private String email;
    private URI self;
    private URI assignedIssues;
    private URI createdIssues;

    public static UserDto fromUser(final UriInfo uri, final User user) {
        final UserDto dto = new UserDto();
        dto.email = user.getEmail();
        dto.self = uri.getBaseUriBuilder().path("/users").path(String.valueOf(user.getUserId())).build();
        dto.assignedIssues = uri.getBaseUriBuilder().path("/issues").queryParam("assignedTo", user.getUserId()).build();
        dto.createdIssues = uri.getBaseUriBuilder().path("/issues").queryParam("createdBy", user.getUserId()).build();

        return dto;
    }

    public static List<UserDto> fromUserList(final UriInfo uri, final List<User> userList) {
        return userList.stream().map(u -> fromUser(uri, u)).collect(Collectors.toList());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getAssignedIssues() {
        return assignedIssues;
    }

    public void setAssignedIssues(URI assignedIssues) {
        this.assignedIssues = assignedIssues;
    }

    public URI getCreatedIssues() {
        return createdIssues;
    }

    public void setCreatedIssues(URI createdIssues) {
        this.createdIssues = createdIssues;
    }
}

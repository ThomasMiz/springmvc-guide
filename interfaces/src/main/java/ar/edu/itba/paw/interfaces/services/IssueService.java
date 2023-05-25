package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Issue;
import ar.edu.itba.paw.models.Priority;

public interface IssueService {
    Issue reportIssue(String userEmail, String title, String description, Priority priority);
}

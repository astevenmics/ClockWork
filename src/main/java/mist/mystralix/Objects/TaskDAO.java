package mist.mystralix.Objects;

import mist.mystralix.Enums.TaskStatus;

/*
    TaskDAO | Database Class
        - Responsible for creating objects to store in the database, not necessarily a global object
        - Contains all the basic information needed for a task
*/
public class TaskDAO {

    /*
        String title            | A brief title for the task
    */
    public String title;

    /*
        String description      | Information of what the task is going to be, have, and such.
    */
    public String description;

    /*
        TaskStatus taskStatus   | A value that showcases whether a task is COMPLETED, INPROGRESS, ARCHIVED, OR CANCELLED
    */
    public TaskStatus taskStatus;

    /*
        - Public constructor to easily create a TaskDAO object by including all class attributes into its parameters
        - Sets the class attributes to its respective parameter values
        - Three parameters
            * String title
                - A brief title for the task
            * String description
                - Information of what the task is going to be, have, and such.
            * TaskStatus taskStatus
                - A value that showcases whether a task is COMPLETED, INPROGRESS, ARCHIVED, OR CANCELLED
    */
    public TaskDAO(
            String title,
            String description,
            TaskStatus taskStatus
    ) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
    }

}
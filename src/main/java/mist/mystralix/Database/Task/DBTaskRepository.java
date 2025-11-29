package mist.mystralix.Database.Task;

import com.google.gson.Gson;
import mist.mystralix.Database.DBManager;
import mist.mystralix.Objects.Task.Task;
import mist.mystralix.Objects.Task.TaskDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBTaskRepository implements TaskRepository {

    /*
        addTask
        - Used in adding a new task into the MySQL database
        - First parameter: Task (task) contains the information for the task to be stored
        - Second parameter: String (userDiscordID) contains the discord ID of the user
        - Converts the task object into a String JSON object
        - Finally, pushes the converted task object into the database
    */
    @Override
    public void addTask(TaskDAO taskDAO, String userDiscordID, String taskUUIDAsString) {
        /*
            - SQL Query for MySQL to insert a new task
            - Uses taskUUID placeholder as the unique identifier for the row
            - Uses userDiscordID placeholder as the container for the user's discord ID
            - Uses task placeholder as the container for the task object
        */
        String sqlStatement = "INSERT INTO tasks (taskUUID, userDiscordID, taskDAO) values (?, ?, ?);";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            /*
                Initializes a Gson object to use in converting a Task JSON String into a Task object
            */
            Gson gson = new Gson();
            String taskDAOAsString = gson.toJson(taskDAO);

            /*
                Sets the values for each placeholder in the sqlStatement
                ? 1 | Sets taskUUIDString as the value for the first placeholder
                    | It is the TEXT/String version of the UUID object
                ? 2 | Sets userDiscordID as the value for the second placeholder
                    | userDiscordID is the user's discord ID
                ? 3 | Sets taskDAOAsString as the value for the third placeholder
                    | taskDAOAsString is the TEXT/String version of the JSON object Task
            */
            preparedStatement.setString(1, taskUUIDAsString);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setString(3, taskDAOAsString);

            /*
                Executes the statement pushing it into the database
            */
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding task to database.");
            throw new RuntimeException("DB Error", e);
        }
    }

    /*
        getTask
        - Used in getting a specific task using the discordID and taskID
        - Uses userDiscordID to get all the rows that contains the userDiscordID value in the userDiscordID column
        - Uses taskID to add a second filter in finding the row that has the taskID among all the userDiscordID rows found
        - Converts the collected row from a String JSON into a Task object using Gson
        - Finally, returns the converted Task object
    */
    @Override
    public Task getTask(String userDiscordID, int taskID) {
        /*
            - Initializes a Task object to use upon converting a Task JSON String into a Task object
            - Initialized outside the try-catch scope as return would also need to be outside the scope
            - Re-assigns the value of the task variable inside the try-catch scope
         */
        Task task;

        /*
            - SQL Query for MySQL to select all rows
            - Uses the userDiscordID column to find all the rows that has the userDiscordID value
            - Uses taskID to add a second filter in finding the row that has the taskID among all the userDiscordID rows found
        */
        String sqlStatement = "SELECT * FROM tasks WHERE userDiscordID = ? AND taskID = ?;";
        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            /*
                Sets the values for each placeholder in the sqlStatement
                ? 1 | Sets userDiscordID as the value for the first placeholder
                    | userDiscordID is the user's discord ID
                ? 2 | Sets taskID as the value for the second placeholder
                    | taskID is the unique integer identifier for tasks
            */
            preparedStatement.setString(1, userDiscordID);
            preparedStatement.setInt(2, taskID);

            /*
                - Expects a ResultSet object as the sqlStatement is set to get and retrieve data
            */
            ResultSet resultSet = preparedStatement.executeQuery();

            /*
                - Expects either one or zero output from resultSet
                - Gets the "taskUUID" column in the result and storing it in the taskUUIDAsString variable
                - Gets the "userDiscordID" column in the result and storing it in the userDiscordIDAsString variable
                - Gets the "taskDAO" column in the result and storing it in the taskDAOAsString variable
                - "taskDAO" column contains the JSON value of a Task object
                - Uses the Gson object to convert taskDAOAsString into a TaskDAO object using its fromJson function
                - Finally, re-assigns the task variable with a new Task instantiation with all the variables
                - If no data is stored in resultSet, returns null
            */
            /*
                Initializes a Gson object to use in converting a Task JSON String into a Task object
            */
            Gson gson = new Gson();

            if (resultSet.next()) {
                String taskUUIDAsString = resultSet.getString("taskUUID");
                String userDiscordIDAsString = resultSet.getString("userDiscordID");
                String taskDAOAsString = resultSet.getString("taskDAO");
                TaskDAO taskDAO = gson.fromJson(taskDAOAsString, TaskDAO.class);

                task = new Task(
                        taskUUIDAsString,
                        userDiscordIDAsString,
                        taskID,
                        taskDAO
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error getting user: " + userDiscordID + ", task: " + taskID);
            throw new RuntimeException("DB Error", e);
        }
        return task;
    }

    /*
        getTask
        - Used in getting a specific task using the UUID
        - Uses UUID to get the row that contains the UUID value in the uuid column
        - Converts the collected row from a Strings and String JSON into a complete Task object using Gson
        - Finally, returns the converted Task object
    */
    @Override
    public Task getTask(String taskUUIDAsString) {
        /*
            - Initializes a Task object to use upon converting a Task JSON String into a Task object
            - Initialized outside the try-catch scope as return would also need to be outside the scope
            - Re-assigns the value of the task variable inside the try-catch scope
         */
        Task task;

        /*
            - SQL Query for MySQL to select all rows
            - Uses the uuid column to find the row that has the taskUUIDAsString value
        */
        String sqlStatement = "SELECT * FROM tasks WHERE taskUUID = ?;";
        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            /*
                Sets the values for each placeholder in the sqlStatement
                ? 1 | Sets taskUUIDAsString as the value for the first placeholder
                    | taskUUIDAsString is the task UUID
            */
            preparedStatement.setString(1, taskUUIDAsString);

            /*
                - Expects a ResultSet object as the sqlStatement is set to get and retrieve data
            */
            ResultSet resultSet = preparedStatement.executeQuery();

            /*
                Initializes a Gson object to use in converting a Task JSON String into a Task object
            */
            Gson gson = new Gson();

            /*
                - Expects either one or zero output from resultSet
                - Gets the "userDiscordID" column in the result and storing it in the userDiscordIDAsString variable
                - Gets the "taskID" column in the result and storing it in the taskID variable
                - Gets the "taskDAO" column in the result and storing it in the taskDAOAsString variable
                - "taskDAO" column contains the JSON value of a TaskDAO object
                - Uses the Gson object to convert taskDAOAsString into a TaskDAO object using its fromJson function
                - Finally, re-assigns the task variable with the converted Task data from taskDAOAsString
                - If no data is stored in resultSet, returns null
            */
            if (resultSet.next()) {
                String userDiscordIDAsString = resultSet.getString("userDiscordID");
                int taskID = resultSet.getInt("taskID");
                String taskDAOAsString = resultSet.getString("taskDAO");
                TaskDAO taskDAO = gson.fromJson(taskDAOAsString, TaskDAO.class);

                task = new Task(
                        taskUUIDAsString,
                        userDiscordIDAsString,
                        taskID,
                        taskDAO
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error getting task with UUID: " + taskUUIDAsString);
            throw new RuntimeException("DB Error", e);
        }
        return task;
    }

    /*
        getAllUserTasks
        - Used in getting all collected user tasks using the discord ID of the user
        - Uses userDiscordID to get all the rows that contains the userDiscordID value in the userDiscordID column
        - Collects all rows gathered and stores in the userTasks ArrayList
        - Finally, returns a Task ArrayList
     */
    @Override
    public List<Task> getAllUserTasks(String userDiscordID) {

        List<Task> userTasks = new ArrayList<>();
        /*
            - SQL Query for MySQL to select all rows containing the userDiscordID value in the userDiscordID column
            - Sorts the collected rows by its taskID value from least to highest value (1 to the highest number)
         */
        String sqlStatement = "SELECT * FROM tasks WHERE userDiscordID = ? ORDER BY taskID ASC";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            /*
                Sets the values for the placeholder in the sqlStatement
                ? 1 | Sets userDiscordID as the value for the placeholder
                    | userDiscordID is the user's discord ID
            */
            preparedStatement.setString(1, userDiscordID);

            /*
                Expects a ResultSet object as the sqlStatement is set to get and retrieve data
            */
            ResultSet resultSet = preparedStatement.executeQuery();

            /*
                Initializes a Gson object to use in converting a Task JSON String into a Task object
            */
            Gson gson = new Gson();

            /*
                - Loops through all the rows in the resultSet until resultSet.next reaches its end
                - Gets the "task" column in the result and storing it in the taskDAOAsString variable
                - "task" column contains the JSON value of a Task object
                - Uses the Gson object to convert taskDAOAsString into a Task object using its fromJson function
                - Finally, adds the converted data into the Task ArrayList
            */
            while (resultSet.next()) {
                String taskUUIDAsString = resultSet.getString("taskUUID");
                String userDiscordIDAsString = resultSet.getString("userDiscordID");
                int taskID = resultSet.getInt("taskID");
                String taskDAOAsString = resultSet.getString("taskDAO");
                Task userTask = new Task(
                        taskUUIDAsString,
                        userDiscordIDAsString,
                        taskID,
                        gson.fromJson(taskDAOAsString, TaskDAO.class));
                userTasks.add(userTask);
            }
        } catch (Exception e) {
            System.out.println("Error getting user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
        return userTasks;
    }

    /*
        updateUserTask()
        - Used in updating the TaskStatus value of a task into cancelled
        - Uses userDiscordID and taskID to determine the row to update in the MySQL database
     */
    @Override
    public void updateUserTask(String userDiscordID, int taskID, Task task) {
        /*
            - SQL Query for MySQL to update the information in the task column
            - Uses the userDiscordID column to find all the rows that has the userDiscordID value
            - Uses taskID to add a second filter in finding the row that has the taskID among all the userDiscordID rows found
         */
        String sqlStatement = "UPDATE tasks SET taskDAO = ? WHERE userDiscordID = ? AND taskID = ?;";

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {

            /*
                Initializes a Gson object to use in converting an Object into a JSON String
            */
            Gson gson = new Gson();
            String taskDAOAsString = gson.toJson(task.taskDAO);

            /*
                Sets the values for each placeholder in the sqlStatement
                ? 1 | Sets taskDAOAsString as the value for the first placeholder
                    | It is the TEXT/String version of the JSON object Task
                ? 2 | Sets userDiscordID as the value for the second placeholder
                    | userDiscordID is the user's discord ID
                ? 3 | Sets taskID as the value for the third placeholder
                    | taskID is the unique integer identifier for tasks
            */
            preparedStatement.setString(1, taskDAOAsString);
            preparedStatement.setString(2, userDiscordID);
            preparedStatement.setInt(3, taskID);

            /*
                Gets the number of rows updated that is returned by the executeUpdate function
            */
            int rowsUpdated = preparedStatement.executeUpdate();

            /*
                Checks if rowsUpdated is equal to zero
                Zero: No rows were updated
                Above Zero: Rows were updated
             */
            if (rowsUpdated == 0) {
                System.out.println("Task not found");
            }

        } catch (SQLException e) {
            System.out.println("Error updating task ID: " + taskID + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

    /*
        deleteUserTask()
        - Used in updating the TaskStatus value of a task into cancelled
        - Uses userDiscordID to determine the row to update in the MySQL database
     */
    @Override
    public void deleteUserTask(String userDiscordID, Task task) {
        /*
            - SQL Query for MySQL to update the information in the task column
            - Uses the taskUUID column to find the row that has the taskUUID value
            - Uses the userDiscordID as a second filter in finding the row that has the taskUUID
         */
        String sqlStatement = "DELETE FROM tasks WHERE taskUUID = ? AND userDiscordID = ?;";
        String taskUUID = task.taskUUID;

        try (
                Connection connection = DBManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)
        ) {
            /*
                Sets the values for each placeholder in the sqlStatement
                ? 1 | Sets taskUUID as the value for the first placeholder
                    | It is the TEXT/String version of the unique identifier UUID of a Task
                ? 2 | Sets userDiscordID as the value for the second placeholder
                    | userDiscordID is the user's discord ID
            */
            preparedStatement.setString(1, taskUUID);
            preparedStatement.setString(2, userDiscordID);

            /*
                Gets the number of rows deleted that is returned by the executeUpdate function
            */
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No task deleted | TaskUUID not found: " + taskUUID);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting task UUID: " + taskUUID + " for user: " + userDiscordID);
            throw new RuntimeException("DB Error", e);
        }
    }

}
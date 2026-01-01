package mist.mystralix.domain.task;

public interface ITask {

    String getUUID();

    String getUserDiscordID();

    Integer getId();

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    Integer getStatus();

    void setStatus(Integer status);

}
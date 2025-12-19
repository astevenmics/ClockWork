package mist.mystralix.application.loops;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class Loops {

    public static StringBuilder createTeamUsersStringBuilder(
            Guild guild,
            List<String> members,
            String emptyMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        if(members.isEmpty()) {
            stringBuilder.append(emptyMessage);
        } else {
            stringBuilder.append("\n");
            for (String memberId : members) {
                Member mem = guild.getMember(User.fromId(memberId));
                if (mem == null) {
                    stringBuilder.append(memberId).append("\n");
                } else {
                    stringBuilder.append(mem.getAsMention()).append("\n");
                }
            }
        }

        return stringBuilder;
    }

}

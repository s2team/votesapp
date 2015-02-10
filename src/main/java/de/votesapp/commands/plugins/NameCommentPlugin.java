package de.votesapp.commands.plugins;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.client.RestTemplate;

import de.votesapp.client.GroupMessage;
import de.votesapp.commands.CommandPlugin;
import de.votesapp.groups.Group;

public class NameCommentPlugin implements CommandPlugin
{
    private Pattern pattern = Pattern.compile("^comment \\w+$",Pattern.CASE_INSENSITIVE);

    @Override public Optional<Answer> interpret(GroupMessage message, Group group)
    {
        Matcher matcher = pattern.matcher(message.normalizedText());
        if (matcher.matches())
        {
            return Optional.of(Answer.intoGroup(group, fetchNewComment(message)));
        }
        return Optional.empty();
    }

    public String fetchNewComment(GroupMessage message)
    {
        String noun = message.normalizedText().split(" ")[1];
        final RestTemplate restTemplate = new RestTemplate();
        final String response = restTemplate.getForObject("http://nickserve42.appspot.com/forname/" + noun, String.class);
        return String.format("%s says: %s", message.getSenderName(), response);
    }
}




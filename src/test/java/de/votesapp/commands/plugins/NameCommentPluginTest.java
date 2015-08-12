package de.votesapp.commands.plugins;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import de.votesapp.client.GroupMessage;

public class NameCommentPluginTest
{
    @Test
    public void should_fetch_comment()
    {
        GroupMessage message = mock(GroupMessage.class);
        when(message.normalizedText()).thenReturn("comment foobar");
        when(message.getSenderName()).thenReturn("test user");
        String comment = new NameCommentPlugin().fetchNewComment(message);
        assertTrue(comment.contains("test user"));
        assertTrue(comment.contains("foobar"));
    }
}

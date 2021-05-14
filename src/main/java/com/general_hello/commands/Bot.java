package com.general_hello.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class Bot {

    private Bot() throws LoginException {

        WebUtils.setUserAgent("Legendary Bot");
        EmbedUtils.setEmbedBuilder(
                () -> new EmbedBuilder()
                        .setColor(Color.cyan)
                        .setFooter("-help to get some help")
        );

        EventWaiter waiter = new EventWaiter();

        JDABuilder.createDefault(Config.get("token"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_INVITES
        )
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new Listener(waiter), waiter)
                .setActivity(Activity.watching("For Auctions"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
    }
    public static void main(String[] args) throws LoginException {
        new Bot();
    }
}

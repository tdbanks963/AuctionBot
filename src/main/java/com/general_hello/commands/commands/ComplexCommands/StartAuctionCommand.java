package com.general_hello.commands.commands.ComplexCommands;

import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import com.general_hello.commands.commands.Storing.Data;

import java.util.concurrent.TimeUnit;

public class StartAuctionCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        ctx.getChannel().sendMessage("Loading setup....").complete().delete().queueAfter(4, TimeUnit.SECONDS);
        ctx.getChannel().sendMessage("Who is the host for this auction? Kindly `@mention` the user").queueAfter(4, TimeUnit.SECONDS);
        Data.questionProgress.put(ctx.getMember(), 0);
    }

    @Override
    public String getName() {
        return "auctionstart";
    }

    @Override
    public String getHelp(String prefix) {
        return "";
    }
}

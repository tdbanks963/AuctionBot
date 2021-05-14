package com.general_hello.commands;

import com.general_hello.commands.commands.PrefixStoring;
import com.general_hello.commands.commands.Storing.Data;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Listener extends ListenerAdapter {
    private final CommandManager manager;
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    public static HashMap<String, Integer> count = new HashMap<>();

    public Listener(EventWaiter waiter) {
        manager = new CommandManager(waiter);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
        LOGGER.info("{} is reconnected!! Response number {}", event.getJDA().getSelfUser().getAsTag(), event.getResponseNumber());
    }

    @Override
    public void onResumed(@NotNull ResumedEvent event) {
        User owner_id = event.getJDA().retrieveUserById(Config.get("owner_id")).complete();
        owner_id.openPrivateChannel().complete().sendMessage("The bot disconnected for sometime due to connection issues.\n" +
                "Response number: " + event.getResponseNumber() + "\n" +
                "Account type: " + event.getJDA().getAccountType().name()).queue();

        owner_id = event.getJDA().retrieveUserById(Config.get("owner_id_partner")).complete();

        owner_id.openPrivateChannel().complete().sendMessage("The bot disconnected for sometime due to connection issues.\n" +
                "Response number: " + event.getResponseNumber() + "\n" +
                "Account type: " + event.getJDA().getAccountType().name()).queue();
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        String contentRaw = event.getMessage().getContentRaw();
        String[] message = contentRaw.split(" ");

        if (!message[0].equals("bid")) {
            return;
        }

        try {
            int auctionId = Integer.parseInt(message[1]);
            int auctionBid = Integer.parseInt(message[2]);

            if (Data.count < (auctionId)) {
                event.getChannel().sendMessage("An error occurred kindly check if the id you have placed is valid.\n" +
                        "bid 1 100\n" +
                        "bid [id] [bid amount]").queue();
                return;
            }

            if (auctionBid < 1) {
                event.getChannel().sendMessage("An error occurred kindly check if you have placed a bid higher than **0**.\n" +
                        "bid 1 100\n" +
                        "bid [id] [bid amount]").queue();
                return;
            }

            if (!Data.host.containsKey(auctionId)) {
                event.getChannel().sendMessage("Invalid id! No such auction found.").queue();
                return;
            }

            if (auctionBid < Data.startingBid.get(auctionId)) {
                event.getChannel().sendMessage("Error! Kindly check if your bid is higher than the starting bid!").queue();
                return;
            }

            HashMap<Member, Integer> bid = new HashMap<>();

        Member member = Data.textChannel.get(auctionId).getGuild().getMember(event.getAuthor());

        if (Data.bid.containsKey(auctionId)) {
            bid = Data.bid.get(auctionId);
        }

        if (bid.containsKey(member)) {
            event.getChannel().sendMessage("Error you already have placed a bid!!!").queue();
            return;
        }

        ArrayList<Member> ok = new ArrayList<>();

        if (Data.bidders.containsKey(auctionId)) {
            ok = Data.bidders.get(auctionId);
        }

        ok.add(member);

        bid.put(member, auctionBid);

            Data.bid.put(auctionId, bid);
            Data.bidders.put(auctionId,ok);

            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Summary of the bid").setFooter("Need a custom discord bot? DM " + event.getJDA().retrieveUserById(Config.get("owner_id_partner")).complete().getAsTag()).setColor(Color.green);
            embedBuilder.addField("ID:", String.valueOf(auctionId), false);
            embedBuilder.addField("Host:", Data.host.get(auctionId).getAsMention(), false);
            embedBuilder.addField("Bid Amount:", String.valueOf(auctionBid), false);
            embedBuilder.addField("Text Channel:", Data.textChannel.get(auctionId).getAsMention(), false);
            embedBuilder.addField("Item:", Data.items.get(auctionId), false);
            embedBuilder.addField("Starting Bid:", String.valueOf(Data.startingBid.get(auctionId)), false);
            LocalDateTime timeToStop = Data.time.get(auctionId);

            try {
                if (timeToStop.getMinute() < 10) {
                    embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":0" + timeToStop.getMinute(), false);
                } else {
                    embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":" + timeToStop.getMinute(), false);
                }
            } catch (NullPointerException e) {
                embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":00", false);
            }

            event.getChannel().sendMessage(embedBuilder.build()).queue();

            event.getMessage().addReaction("✅").complete();
        } catch (Exception e) {
            event.getChannel().sendMessage("An error occurred kindly check if you have placed the correct format.\n" +
                    "bid 1 100\n" +
                    "bid [id] [bid amount]").queue();

            User owner_id = event.getJDA().retrieveUserById(Config.get("owner_id_partner")).complete();
            owner_id.openPrivateChannel().complete().sendMessage("An error occurred!\n" +
                    "Message sent to cause the problem: " + event.getMessage().getContentRaw() + "\n" +
                    "Error message: " + e.getMessage() + "\n" +
                    "Localized message: " +
                    e.getLocalizedMessage() +
                    "\n" +
                    "DM " + owner_id.getAsMention() + " relating to this problem.").queue();

            owner_id = event.getJDA().retrieveUserById(Config.get("owner_id")).complete();
            owner_id.openPrivateChannel().complete().sendMessage("An error occurred!\n" +
                    "Message sent to cause the problem: " + event.getMessage().getContentRaw() + "\n" +
                    "Error message: " + e.getMessage() + "\n" +
                    "Localized message: " +
                    e.getLocalizedMessage() +
                    "\n" +
                    "DM " + owner_id.getAsMention() + " relating to this problem.").queue();
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        LOGGER.info(event.getAuthor().getName());

        LOGGER.info(event.getMessage().getContentRaw() + "\n" +
                "Sent by " +
                event.getAuthor().getName() + " in " +
                event.getGuild().getName());

        final long guildID = event.getGuild().getIdLong();
        String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, (id) -> Config.get("prefix"));
        String raw = event.getMessage().getContentRaw();

        HashMap<Integer, LocalDateTime> time = Data.time;

        int x = 0;

        while (x < time.size()) {
            try {
                LocalDateTime time1 = time.get(x);

                System.out.println("gg");

                if (!Data.deleted.get(x)) {
                    if (time1.isBefore(LocalDateTime.now()) || time1.equals(LocalDateTime.now())) {
                        System.out.println("gg");
                        HashMap<Member, Integer> memberIntegerHashMap = Data.bid.get(x);
                        ArrayList<Member> bidders = Data.bidders.get(x);

                        if (memberIntegerHashMap.size() != 0 && memberIntegerHashMap.size() != 1) {
                            EmbedBuilder em = new EmbedBuilder().setTitle("Auction Ended").setFooter("Congratulations to the winners!").setColor(Color.yellow).setTimestamp(LocalDateTime.now());

                            try {
                                int y = 0;
                                System.out.println("gg");

                                ArrayList<Member> bids = new ArrayList<>();
                                ArrayList<Integer> bids1 = new ArrayList<>();
                                ArrayList<Integer> location = new ArrayList<>();

                                while (y < bidders.size()) {
                                    Member member = bidders.get(y);
                                    Integer integer = memberIntegerHashMap.get(member);
                                    bids1.add(integer);
                                    location.add(y);
                                    bids.add(y, member);
                                    y++;
                                }

                                System.out.println("Ok");

                                for (int i = 0; i < bids1.size(); i++) {
                                    for (int j = i + 1; j < bids1.size(); j++) {
                                        if (bids1.get(i) > bids1.get(j)) {
                                            int temp = bids1.get(i);
                                            bids1.add(i, bids1.get(j));
                                            bids1.add(j, temp);

                                            Integer integer2 = location.get(i);
                                            Integer integer3 = location.get(j);

                                            location.add(i, integer3);
                                            location.add(j, integer2);
                                        }
                                        System.out.println("Ok");
                                    }
                                    System.out.println("Ok");
                                }

                                System.out.println("Ok");

                                em.addField("Winner: (" + bids1.get(bids1.size()-1) + ")", bids.get(location.get(bids1.size() - 1)).getAsMention(), false);
                                em.addField("Second place: (" + bids1.get(bids1.size()-2) + ")", bids.get(location.get(bids1.size() - 2)).getAsMention(), false);
                                try {
                                    em.addField("Third place: (" + bids1.get(bids1.size()-3) + ")", bids.get(location.get(bids1.size() - 3)).getAsMention(), false);
                                } catch (Exception ignored) {
                                }
                                em.addField("Total Bids:", String.valueOf(bids1.size()), false);

                                Data.textChannel.get(x).sendMessage(em.build()).queue();

                                TextChannel channel = Data.textChannel.get(x);
                                try {
                                    TextChannel textChannelById = channel.getGuild().getTextChannelById("480796411768864779");
                                    textChannelById.sendMessage(Data.host.get(x).getAsMention() + ", Congratulations! " + bids.get(location.get(bids1.size() - 1)) + " won your auction of **" + Data.items.get(x) + "** with a bid of ***" + bids1.get(bids1.size()-2) + "***.\n").queue();
                                } catch (Exception ignored) {}

                                try {
                                    TextChannel textChannelById = channel.getGuild().getTextChannelById("687057577095200806");
                                    textChannelById.sendMessage(Data.host.get(x).getAsMention() + ", Congratulations! " + bids.get(location.get(bids1.size() - 1)) + " won your auction of **" + Data.items.get(x) + "** with a bid of ***" + bids1.get(bids1.size()-2) + "***.\n").queue();
                                } catch (Exception ignored) {}

                                try {
                                    TextChannel textChannelById = channel.getGuild().getTextChannelById("798697170604654623");
                                    textChannelById.sendMessage(Data.host.get(x).getAsMention() + ", Congratulations! " + bids.get(location.get(bids1.size() - 1)) + " won your auction of **" + Data.items.get(x) + "** with a bid of ***" + bids1.get(bids1.size()-2) + "***.\n").queue();
                                } catch (Exception ignored) {}
                            } catch (Exception e) {
                                Data.textChannel.get(x).sendMessage(em.build()).queue();
                            }
                        } else if (memberIntegerHashMap.size() == 0) {
                            EmbedBuilder em = new EmbedBuilder().setTitle("Auction Ended").setFooter("Congratulations to the winners!").setColor(Color.yellow).setTimestamp(LocalDateTime.now());
                            em.addField("No one won!", "No one placed a bid on the auction!", false);
                            Data.textChannel.get(x).sendMessage(em.build()).queue();
                        } else {
                            memberIntegerHashMap.size();
                            EmbedBuilder em = new EmbedBuilder().setTitle("Auction Ended").setFooter("Congratulations to the winners!").setColor(Color.yellow).setTimestamp(LocalDateTime.now());
                            em.addField(Data.bidders.get(x).get(0).getEffectiveName() + " (" + memberIntegerHashMap.get(Data.bidders.get(x).get(0)) + ") won the auction", "No one else placed a bid on the auction!", false);
                            em.addField("Total Bids:", "1", false);
                            Data.textChannel.get(x).sendMessage(em.build()).queue();

                            TextChannel channel = Data.textChannel.get(x);

                            try {
                                TextChannel textChannelById = channel.getGuild().getTextChannelById("480796411768864779");
                                textChannelById.sendMessage(Data.host.get(x).getAsMention() + ", Congratulations! " + Data.bidders.get(x).get(0).getAsMention() + " won your auction of **" + Data.items.get(x) + "** with a bid of ***" + memberIntegerHashMap.get(Data.bidders.get(x).get(0)) + "***.\n").queue();
                            } catch (Exception ignored) {}

                            try {
                                TextChannel textChannelById = channel.getGuild().getTextChannelById("687057577095200806");
                                textChannelById.sendMessage(Data.host.get(x).getAsMention() + ", Congratulations! " + Data.bidders.get(x).get(0).getAsMention() + " won your auction of **" + Data.items.get(x) + "** with a bid of ***" + memberIntegerHashMap.get(Data.bidders.get(x).get(0)) + "***.\n").queue();
                            } catch (Exception ignored) {}

                            try {
                                TextChannel textChannelById = channel.getGuild().getTextChannelById("798697170604654623");
                                textChannelById.sendMessage(Data.host.get(x).getAsMention() + ", Congratulations! " + Data.bidders.get(x).get(0).getAsMention() + " won your auction of **" + Data.items.get(x) + "** with a bid of ***" + memberIntegerHashMap.get(Data.bidders.get(x).get(0)) + "***.\n").queue();
                            } catch (Exception ignored) {}

                            try {
                                TextChannel textChannelById = channel.getGuild().getTextChannelById("840022873636470845");
                                textChannelById.sendMessage(Data.host.get(x).getAsMention() + ", Congratulations! " + Data.bidders.get(x).get(0).getAsMention() + " won your auction of **" + Data.items.get(x) + "** with a bid of ***" + memberIntegerHashMap.get(Data.bidders.get(x).get(0)) + "***.\n").queue();
                            } catch (Exception ignored) {}
                        }
                        System.out.println("gg");

                        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Auction Ended").setFooter("Congratulations to the winners!").setColor(Color.yellow).setTimestamp(LocalDateTime.now());
                        embedBuilder.addField("ID:", String.valueOf(x), false);
                        embedBuilder.addField("Host:", Data.host.get(x).getAsMention(), false);
                        embedBuilder.addField("Text Channel:", Data.textChannel.get(x).getAsMention(), false);
                        embedBuilder.addField("Item:", Data.items.get(x), false);
                        embedBuilder.addField("Starting Bid:", String.valueOf(Data.startingBid.get(x)), false);

                        LocalDateTime timeToStop = Data.time.get(Data.count);

                        try {
                            if (timeToStop.getMinute() < 10) {
                                embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":0" + timeToStop.getMinute(), false);
                            } else {
                                embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":" + timeToStop.getMinute(), false);
                            }
                        } catch (NullPointerException e) {
                            embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":00", false);
                        }

                        TextChannel channel = Data.textChannel.get(x);
                        List<Role> roles = channel.getGuild().getRoles();

                        int y = 0;
                        while (y < roles.size()) {
                            if (!roles.get(y).hasPermission(Permission.KICK_MEMBERS) && !roles.get(y).getName().equals("Silent Auctioneer") && !event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getRoles().contains(roles.get(y))) {
                                try {
                                    channel.getManager().clearOverridesAdded().getChannel().putPermissionOverride(roles.get(y)).setDeny(Permission.VIEW_CHANNEL).complete();
                                } catch (Exception ignored) {}
                            }

                            System.out.println("ook");
                            y++;
                        }

                        List<Message> retrievedHistory = channel.getHistory().getChannel().getHistory().getRetrievedHistory();
                        channel.deleteMessages(retrievedHistory).complete();

                        Data.textChannel.get(x).sendMessage(embedBuilder.build()).queue();
                        Data.deleted.put(x, true);
                    }
                }
            } catch(Exception ignored){}

            x++;
        }

        if (Data.questionProgress.containsKey(event.getMember())) {
            if (Data.questionProgress.get(event.getMember()) == 0) {
                int size = Data.host.size();

                try {
                    Data.host.put(size, event.getMessage().getMentionedMembers().get(0));
                    event.getChannel().sendMessage(event.getMessage().getMentionedMembers().get(0).getAsMention() + " is the host for this auction.\n" +
                            "Where is the auction going to take place? Kindly mention the channel (#channel)").queue();
                    Data.questionProgress.put(event.getMember(), 1);
                    return;
                } catch (Exception e) {
                    event.getChannel().sendMessage("Error! You didn't mentioned any user.").queue();
                    Data.questionProgress.remove(event.getMember());
                    return;
                }
            }

            if (Data.questionProgress.get(event.getMember()) == 1) {
                int size = Data.textChannel.size();
                try {
                    Data.textChannel.put(size, event.getMessage().getMentionedChannels().get(0));
                    event.getChannel().sendMessage(event.getMessage().getMentionedChannels().get(0).getAsMention() + " will be the place where the auction will start.\n" +
                            "What will be the item(s) of this auction?").queue();
                    Data.questionProgress.put(event.getMember(), 2);
                    return;
                } catch (Exception e) {
                    event.getChannel().sendMessage("Error! You didn't mentioned any channel.").queue();
                    Data.questionProgress.remove(event.getMember());
                    Data.host.remove(Data.host.size() - 1);
                    return;
                }
            }

            if (Data.questionProgress.get(event.getMember()) == 2) {
                int size = Data.items.size();
                try {
                    Data.items.put(size, raw);
                    event.getChannel().sendMessage("The item of this auction will be `" + raw + "`\n" +
                            "What will be the starting bid of this auction? (Number only)").queue();
                    Data.questionProgress.put(event.getMember(), 3);
                    return;
                } catch (Exception e) {
                    event.getChannel().sendMessage("Error! You didn't placed any item.").queue();
                    Data.questionProgress.remove(event.getMember());
                    Data.host.remove(Data.host.size() - 1);
                    Data.textChannel.remove(Data.textChannel.size() - 1);
                    return;
                }
            }

            if (Data.questionProgress.get(event.getMember()) == 3) {
                try {
                    Data.startingBid.add(Integer.parseInt(raw));
                    event.getChannel().sendMessage("The starting bid of this auction will be `" + Integer.parseInt(raw) + "`\n" +
                            "How long will this auction last? Kindly do it in this **format** : `(Days)d (Hours)h (Minutes)m`\n" +
                            "Example: `1d 2h 0m` So it will be 1 day 2 hours 0 minutes").queue();
                    Data.questionProgress.put(event.getMember(), 4);
                    return;
                } catch (Exception e) {
                    event.getChannel().sendMessage("Error! You didn't placed a number **only**.").queue();
                    Data.questionProgress.remove(event.getMember());
                    Data.host.remove(Data.host.size() - 1);
                    Data.textChannel.remove(Data.textChannel.size() - 1);
                    Data.startingBid.remove(Data.startingBid.size() - 1);
                    return;
                }
            }

            if (Data.questionProgress.get(event.getMember()) == 4) {
                int size = Data.time.size();
                try {
                    String[] timeSplit = raw.split("d ");
                    String days;
                    String hours;
                    String minutes;
                    days = timeSplit[0];
                    timeSplit = timeSplit[1].split("h ");
                    hours = timeSplit[0];
                    timeSplit = timeSplit[1].split("m");
                    minutes = timeSplit[0];

                    LocalDateTime timeToStop = LocalDateTime.now().plusDays(Long.parseLong(days)).plusHours(Long.parseLong(hours)).plusMinutes(Long.parseLong(minutes));
                    Data.time.put(size, timeToStop);
                    event.getChannel().sendMessage("Time Length: `" + days + " days " + hours + " hours " + minutes + " minutes`").queue();
                    if (timeToStop.getMinute() < 10) {
                        event.getChannel().sendMessage("Ends in: **" + timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + "**. On **" + timeToStop.getHour() + ":0" + timeToStop.getMinute() + "**").queue();
                    } else {
                        event.getChannel().sendMessage("Ends in: **" + timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + "**. On **" + timeToStop.getHour() + ":" + timeToStop.getMinute() + "**").queue();
                    }
                    Data.questionProgress.remove(event.getMember());

                    if (Data.ok) {
                        Data.count++;
                    } else {
                        Data.ok = true;
                    }

                } catch (Exception e) {
                    event.getChannel().sendMessage("Error! Incorrect format placed.").queue();
                    Data.questionProgress.remove(event.getMember());
                    Data.host.remove(Data.host.size() - 1);
                    Data.textChannel.remove(Data.textChannel.size() - 1);
                    Data.items.remove(Data.items.size() - 1);
                    Data.startingBid.remove(Data.startingBid.size() - 1);
                    Data.time.remove(Data.startingBid.size() - 1);
                    return;
                }
            }

            TextChannel channel = Data.textChannel.get(Data.count);
            List<Role> roles = channel.getGuild().getRoles();

            int y = 0;
            while (y < roles.size()) {
                if (!roles.get(y).hasPermission(Permission.KICK_MEMBERS) && !roles.get(y).getName().equals("Silent Auctioneer") && !event.getGuild().getMemberById(event.getJDA().getSelfUser().getId()).getRoles().contains(roles.get(y))) {
                    try {
                        channel.getManager().clearOverridesAdded().getChannel().putPermissionOverride(roles.get(y)).setAllow(Permission.VIEW_CHANNEL).complete();
                    } catch (Exception ignored) {}
                }

                System.out.println("ook");
                y++;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Summary of the Auction").setFooter("Bid now by DMing me with bid").setColor(Color.RED);
            embedBuilder.addField("ID:", String.valueOf(Data.count), false);
            embedBuilder.addField("Host:", Data.host.get(Data.count).getAsMention(), false);
            embedBuilder.addField("Text Channel:", Data.textChannel.get(Data.count).getAsMention(), false);
            embedBuilder.addField("Item:", Data.items.get(Data.count), false);
            embedBuilder.addField("Starting Bid:", String.valueOf(Data.startingBid.get(Data.count)), false);

            LocalDateTime timeToStop = Data.time.get(Data.count);
            try {
                if (timeToStop.getMinute() < 10) {
                    embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":0" + timeToStop.getMinute(), false);
                } else {
                    embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":" + timeToStop.getMinute(), false);
                }
            } catch (NullPointerException e) {
                embedBuilder.addField("Ends in:", timeToStop.getMonth().name() + " " + timeToStop.getDayOfMonth() + ", " + timeToStop.getYear() + ". On " + timeToStop.getHour() + ":00", false);
            }

            event.getChannel().sendMessage(embedBuilder.build()).queue();

            embedBuilder.setColor(Color.cyan);
            Data.deleted.put(Data.count, false);
            Data.textChannel.get(Data.count).sendMessage(embedBuilder.build()).queue();
            return;
        }

        if (raw.equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(Config.get("owner_id"))) {
            shutdown(event, true);
            return;
        } else if (raw.equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(Config.get("owner_id_partner"))) {
            shutdown(event, false);
            return;
        }

        if (raw.startsWith(prefix)) {
            try {
                manager.handle(event, prefix);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String commandsCount() {
        int x = 0;
        int size = CommandManager.commandNames.size();
        StringBuilder result = new StringBuilder();

        while (x < size) {
            String commandName = CommandManager.commandNames.get(x);
            result.append(x+1).append(".) ").append(commandName).append(" - ").append(count.get(commandName)).append("\n");
            x++;
        }

        return String.valueOf(result);
    }

    public static void shutdown(GuildMessageReceivedEvent event, boolean isOwner) {
        LOGGER.info("The bot " + event.getAuthor().getAsMention() + " is shutting down.\n" +
                "Thank you for using General_Hello's (" + event.getJDA().retrieveUserById(Config.get("owner_id")).complete().getAsTag() + ") Code!!!");

        event.getChannel().sendMessage("Shutting down...").queue();
        event.getChannel().sendMessage("Bot successfully shutdown!!!!").queue();
        EmbedBuilder em = new EmbedBuilder().setTitle("Shutdown details!!!!").setColor(Color.red).setFooter("Shutdown on ").setTimestamp(LocalDateTime.now());
        em.addField("Shutdown made by ", event.getAuthor().getName(), false);
        em.addField("Date", LocalDateTime.now().getDayOfWeek().name(), false);
        em.addField("List of Commands used in this session....", commandsCount(), false);
        event.getAuthor().openPrivateChannel().complete().sendMessage(em.build()).queue();

        if (!isOwner) {
            User owner = event.getJDA().retrieveUserById(Config.get("owner_id")).complete();
            owner.openPrivateChannel().complete().sendMessage(em.build()).queue();
        } else {
            User owner = event.getJDA().retrieveUserById(Config.get("owner_id_partner")).complete();
            owner.openPrivateChannel().complete().sendMessage(em.build()).queue();
        }

        event.getJDA().shutdown();
        BotCommons.shutdown(event.getJDA());
    }
}

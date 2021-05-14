package com.general_hello.commands.commands.ComplexCommands;

import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import com.general_hello.commands.commands.Storing.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Check implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws InterruptedException, IOException {
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
                                    }
                                }

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

                        Data.textChannel.get(x).sendMessage(embedBuilder.build()).queue();
                        Data.deleted.put(x, true);
                    }
                }
            } catch(Exception ignored){}

            x++;
        }
    }

    @Override
    public String getName() {
        return "scan";
    }

    @Override
    public String getHelp(String prefix) {
        return null;
    }
}

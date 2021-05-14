package com.general_hello.commands.commands.Storing;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Data {
    public static HashMap<Member, Integer> questionProgress = new HashMap<>();
    public static HashMap<Integer, Member> host = new HashMap<>();
    public static HashMap<Integer, TextChannel> textChannel = new HashMap<>();
    public static HashMap<Integer, String> items = new HashMap<>();
    public static ArrayList<Integer> startingBid = new ArrayList<>();
    public static HashMap<Integer, LocalDateTime> time = new HashMap<>();
    public static Integer count = 0;
    public static boolean ok = false;
    public static HashMap<Integer, HashMap<Member, Integer>> bid = new HashMap<>();
    public static HashMap<Integer, Boolean> deleted = new HashMap<>();
    public static HashMap<Integer, ArrayList<Member>> bidders = new HashMap<>();
}

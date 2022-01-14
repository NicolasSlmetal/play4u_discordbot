package com.play4ubot.commands;

import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class CommandLimiter  {
    private final String name;
    private Member member;
    private final List<Member> mutableList = new ArrayList<>();
    private final List<Member> sendeds = new ArrayList<>();
    public CommandLimiter(String name){
        this.name = name;
    }
    public List<Member> updateMembersConnected(VoiceChannel channel){
        List<Member> membersConnected = new ArrayList<>(channel.getMembers());
        membersConnected.removeIf(member -> member.getUser().isBot());
        return membersConnected;
    }
    public void verifyLimit(@NotNull List<? extends Member> membersConnected, Member member, VoiceChannel voiceCh, TextChannel textCh, Runnable r){
        this.setMember(member);
        this.getSendeds().add(member);
        mutableList.addAll(membersConnected);
        mutableList.remove(member);
        String user = voiceCh.getJDA().getSelfUser().getName();
        List<? extends Member> copy = new ArrayList<>(membersConnected);
        textCh.sendMessage(String.format(
                "**(%d/%d) enviaram o comando %s**", copy.size() - mutableList.size(), copy.size(), this.getName())
        ).queue();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            synchronized (mutableList) {
                while (!mutableList.isEmpty()) {
                    try {
                        mutableList.wait();
                        if (updateMembersConnected(voiceCh).size() != copy.size() && !this.getSendeds().contains(this.getMember())) this.getMutableList().add(this.getMember());
                        mutableList.remove(this.member);
                        textCh.sendMessage(String.format("**(%d/%d) enviaram o comando %s**", updateMembersConnected(voiceCh).size() - mutableList.size(), updateMembersConnected(voiceCh).size(), getName())
                            ).queue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                r.run();
                sendeds.clear();
            }
            });
        ScheduledExecutorService finalizer = Executors.newSingleThreadScheduledExecutor();
        finalizer.schedule(() -> {
            if (!executor.isTerminated()) {
                executor.shutdown();
                mutableList.clear();
                sendeds.clear();
            }
            finalizer.shutdown();
        }, 60, TimeUnit.SECONDS);
    }

    public String getName() {
        return name;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public List<Member> getMutableList() {
        return mutableList;
    }

    public List<Member> getSendeds() {
        return sendeds;
    }
}

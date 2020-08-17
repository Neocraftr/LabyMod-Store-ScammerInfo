package de.neocraftr.scammerlist.listener;

public interface ClientCommandEvent {
    boolean onCommand(String cmd, String[] args);
}

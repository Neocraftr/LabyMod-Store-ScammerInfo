package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import net.labymod.core.LabyModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreRenderListener {

    private ScammerList sc = ScammerList.getScammerList();
    private boolean tablistUpdated = false;

    @SubscribeEvent
    public void onPreRender(RenderGameOverlayEvent e) {
        if(Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isKeyDown()
                && !Minecraft.getMinecraft().isIntegratedServerRunning()) {
            if(!isTablistUpdated()) {
                setTablistUpdated(true);
                ScoreObjective scoreobjective = LabyModCore.getMinecraft().getWorld().getScoreboard().getObjectiveInDisplaySlot(0);
                NetHandlerPlayClient handler = LabyModCore.getMinecraft().getPlayer().sendQueue;

                if(handler.getPlayerInfoMap().size() > 0 || scoreobjective != null) {
                    Collection<NetworkPlayerInfo> players = handler.getPlayerInfoMap();

                    for(NetworkPlayerInfo player : players) {
                        if(player.getDisplayName() != null) {
                            IChatComponent playerName = player.getDisplayName();
                            IChatComponent scammerMessage = new ChatComponentText(sc.getHelper().colorize("§a§a§r"+sc.getSettings().getScammerPrefix())+" §r");

                            if(playerName.getSiblings().size() > 0 && playerName.getSiblings().get(0).getFormattedText().startsWith("§a§a§r")) {
                                playerName.getSiblings().remove(0);
                            }

                            if(sc.getSettings().isHighlightInTablist()) {
                                if(sc.getPrivateListName().contains(player.getGameProfile().getName()) || sc.getPrivateListName().contains("*")
                                        || (sc.getSettings().isShowOnlineScammer() && sc.getOnlineListName().contains(player.getGameProfile().getName()))) {
                                    playerName.getSiblings().add(0, scammerMessage);
                                }
                            }

                            player.setDisplayName(playerName);
                        }
                    }
                }
            }
        } else {
            setTablistUpdated(false);
        }
    }

    public boolean isTablistUpdated() {
        return tablistUpdated;
    }
    public void setTablistUpdated(boolean tablistUpdated) {
        this.tablistUpdated = tablistUpdated;
    }
}

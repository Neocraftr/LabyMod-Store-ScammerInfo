package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.PlayerType;
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

public class PreRenderListener {

    private ScammerList sc = ScammerList.getScammerList();
    private boolean tablistUpdated = false;

    @SubscribeEvent
    public void onPreRender(RenderGameOverlayEvent e) {
        if(Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isKeyDown()
                && !Minecraft.getMinecraft().isIntegratedServerRunning()) {
            if(!tablistUpdated) {
                tablistUpdated = true;
                ScoreObjective scoreobjective = LabyModCore.getMinecraft().getWorld().getScoreboard().getObjectiveInDisplaySlot(0);
                NetHandlerPlayClient handler = LabyModCore.getMinecraft().getPlayer().sendQueue;
                IChatComponent scammerMessage = new ChatComponentText(sc.getHelper().colorize("§a§a§r"+sc.getSettings().getScammerPrefix())+" §r");
                IChatComponent trustedMessage = new ChatComponentText(sc.getHelper().colorize("§a§a§r"+sc.getSettings().getTrustedPrefix())+" §r");

                if(handler.getPlayerInfoMap().size() > 0 || scoreobjective != null) {
                    Collection<NetworkPlayerInfo> players = handler.getPlayerInfoMap();

                    for(NetworkPlayerInfo player : players) {
                        if(player.getDisplayName() != null) {
                            IChatComponent playerName = player.getDisplayName();

                            if(playerName.getSiblings().size() > 0 && playerName.getSiblings().get(0).getFormattedText().startsWith("§a§a§r")) {
                                playerName.getSiblings().remove(0);
                            }

                            if(sc.getSettings().isHighlightInTablist()) {
                                String uuid = player.getGameProfile().getId().toString();
                                if(player.getGameProfile().getName().startsWith("!"))
                                    uuid = player.getGameProfile().getName();

                                if(sc.getListManager().checkUUID(uuid, PlayerType.SCAMMER)) {
                                    playerName.getSiblings().add(0, scammerMessage);
                                } else if(sc.getListManager().checkUUID(uuid, PlayerType.TRUSTED)) {
                                    playerName.getSiblings().add(0, trustedMessage);
                                }
                            }

                            player.setDisplayName(playerName);
                        }
                    }
                }
            }
        } else {
            tablistUpdated = false;
        }
    }
}

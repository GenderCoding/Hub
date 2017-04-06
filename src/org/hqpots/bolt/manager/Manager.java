package org.hqpots.bolt.manager;

import org.bukkit.plugin.messaging.*;
import org.hqpots.bolt.Bolt;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import java.io.*;

public class Manager implements PluginMessageListener
{
    private static int playerCounter;
    
    static {
        Manager.playerCounter = 0;
    }
    
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        try {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            final String command = in.readUTF();
            if (command.equals("PlayerCount")) {
                final String server = in.readUTF();
                final int playerCount = Manager.playerCounter = in.readInt();
                System.out.println("Server " + server + " has " + playerCount + " player(s).");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendPlayerToServer(final Player p, final String str) {
        Bukkit.getMessenger().registerOutgoingPluginChannel(Bolt.getInstance(), "BungeeCord");
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(str);
        }
        catch (IOException ex) {}
        p.sendPluginMessage(Bolt.getInstance(), "BungeeCord", b.toByteArray());
    }
    
    public static int getPlayerCount(final String server) {
        try {
            final ByteArrayOutputStream b = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
            Bukkit.getServer().sendPluginMessage(Bolt.getInstance(), "BungeeCord", b.toByteArray());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Manager.playerCounter;
    }
}

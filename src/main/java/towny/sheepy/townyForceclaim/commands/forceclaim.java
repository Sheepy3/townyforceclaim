package towny.sheepy.townyForceclaim.commands;

import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;

public class forceclaim implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Expect: /townyadmin claim <town> <x> <z>
        if (args.length < 3) {
            TownyMessaging.sendErrorMsg(sender, "Usage: /townyadmin claim <town> <x> <z> <flag>");
            return true;
        }

        String townName = args[0];
        Town town = TownyUniverse.getInstance().getTown(townName);
        if (town == null) {
            TownyMessaging.sendErrorMsg(sender, "Town not found: " + townName);
            return true;
        }

        TownyWorld world = TownyAPI.getInstance().getTownyWorld("world");
        int chunkX,chunkZ;
        int coordx,coordy;

        if (args.length == 4 && "-w".equalsIgnoreCase(args[3])) {
            chunkX = Integer.parseInt(args[1]);
            chunkZ = Integer.parseInt(args[2]);
        }else{
            coordx = Integer.parseInt(args[1]);
            coordy = Integer.parseInt(args[2]);
            WorldCoord worldCoord = WorldCoord.parseWorldCoord(String.valueOf(world), coordx, coordy);
            Coord c = worldCoord.getCoord();
            chunkX = c.getX();
            chunkZ = c.getZ();
        }
        Coord coord = new Coord(chunkX, chunkZ);

       /* if (world.hasTownBlock(coord)) {
            try {
                TownBlock townblock = TownyUniverse.getInstance().getTownBlock(worldCoord);
                TownyMessaging.sendErrorMsg(sender, "Chunk " + coord.toString() + " is already claimed by" + townblock.getTown().getName());
            } catch (NotRegisteredException e) {
                TownyMessaging.sendMsg(sender, "Chunk " + coord + " is unclaimed.");            }
        }*/


        try {
            TownBlock oldBlock = world.getTownBlock(coord);
            Town oldTown = oldBlock.getTown();

            // Unregister from the old town and global maps
            oldTown.removeTownBlock(oldBlock);
            TownyUniverse.getInstance().removeTownBlock(oldBlock);
            //oldBlock.clear();  // remove from disk

            TownyMessaging.sendMsg(sender,
                    String.format("Removed claim at %s from town %s.", coord, oldTown.getName())
            );
        } catch (NotRegisteredException ignored) {
            // no existing claim — nothing to remove
        }



        // Create & register the claim
        TownyMessaging.sendMsg(sender,
                String.format("registering claim..."+coord.toString()));

        TownBlock newBlock = new TownBlock(chunkX, chunkZ, world);
        newBlock.setTown(town);
        newBlock.setType(TownBlockType.RESIDENTIAL);

        TownyUniverse.getInstance().addTownBlock(newBlock);
        newBlock.save();  // Persist immediately

        TownyMessaging.sendMsg(sender,
                String.format("Chunk (%d, %d) force‑claimed for town %s.", chunkX, chunkZ, town.getName())
        );

        // Refresh protections immediately
        Towny.getPlugin().resetCache();

        return true;
    }
}

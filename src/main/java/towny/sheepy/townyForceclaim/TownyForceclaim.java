package towny.sheepy.townyForceclaim;

import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import towny.sheepy.townyForceclaim.commands.forceclaim;
public final class TownyForceclaim extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Register "/townyadmin claim" as a subcommand of Towny’s admin commands
        TownyCommandAddonAPI.addSubCommand(
                TownyCommandAddonAPI.CommandType.TOWNYADMIN,    // attach to /townyadmin
                "claim",                                       // subcommand name
                new forceclaim()                     // your CommandExecutor implementation
        );

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

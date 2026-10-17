package me.tWizT3d_dreaMr.ShopAddon.util;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.Collection;
import java.util.Locale;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Registers each mixed-case plugin.yml command under its declared spelling too. Since 26.x the
 * dispatcher is case-sensitive and Bukkit registers only the lowercase label.
 */
public final class CapitalizedCommands {
    private CapitalizedCommands() {}

    @SuppressWarnings("deprecation")
    public static void register(JavaPlugin plugin) {
        plugin.getLifecycleManager()
                .registerEventHandler(
                        LifecycleEvents.COMMANDS,
                        event -> {
                            for (String name : plugin.getDescription().getCommands().keySet()) {
                                if (name.equals(name.toLowerCase(Locale.ROOT))) continue;
                                PluginCommand command = plugin.getCommand(name);
                                if (command == null) continue;
                                event.registrar().register(name, forward(command, name));
                            }
                        });
    }

    private static BasicCommand forward(PluginCommand command, String label) {
        return new BasicCommand() {
            @Override
            public void execute(CommandSourceStack source, String[] args) {
                command.execute(source.getSender(), label, args);
            }

            @Override
            public Collection<String> suggest(CommandSourceStack source, String[] args) {
                return command.tabComplete(source.getSender(), label, args);
            }
        };
    }
}

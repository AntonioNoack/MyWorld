package net.dynamicdev.anticheat.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandHandler implements CommandExecutor {
    private List<CommandBase> commands;
    
    public CommandHandler() {
        (this.commands = new ArrayList<CommandBase>()).add(new CommandHelp());
        this.commands.add(new CommandCheck());
        this.commands.add(new CommandDebug());
        this.commands.add(new CommandDeveloper());
        this.commands.add(new CommandLog());
        this.commands.add(new CommandReload());
        this.commands.add(new CommandReport());
        this.commands.add(new CommandReset());
        this.commands.add(new CommandSpy());
        this.commands.add(new CommandUpdate());
        this.commands.add(new CommandXray());
    }
    
    public boolean onCommand(final CommandSender cs, final Command cmd, final String alias, final String[] args) {
        if (args.length >= 1) {
            final String command = args[0];
            final String[] newArgs = new String[args.length - 1];
            for (int i = 1; i < args.length; ++i) {
                newArgs[i - 1] = args[i];
            }
            for (final CommandBase base : this.commands) {
                if (base.getCommand().equalsIgnoreCase(command)) {
                    base.run(cs, newArgs);
                    return true;
                }
            }
            this.commands.get(0).run(cs, null);
        } else {
            this.commands.get(0).run(cs, null);
        }
        return true;
    }
}

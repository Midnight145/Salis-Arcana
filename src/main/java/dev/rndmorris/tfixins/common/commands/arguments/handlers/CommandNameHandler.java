package dev.rndmorris.tfixins.common.commands.arguments.handlers;

import dev.rndmorris.tfixins.config.CommandSettings;
import dev.rndmorris.tfixins.config.FixinsConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CommandNameHandler implements IArgumentHandler {
    @Override
    public Object parse(ICommandSender sender, String current, Iterator<String> args) {
        final var command = findCommand(current);
        if (command == null) {
            throw new CommandException("commands.generic.notFound");
        }
        if (!sender.canCommandSenderUseCommand(command.getPermissionLevel(), command.getFullName())) {
            throw new CommandException("commands.generic.permission");
        }
        return command;
    }

    @Override
    public List<String> getAutocompleteOptions(ICommandSender sender, String current, Iterator<String> args) {
        final var settingsArr = FixinsConfig.commandsModule.commandsSettings;
        if (!args.hasNext()) {
            final var results = new ArrayList<String>(settingsArr.length * 2);
            for (var settings : settingsArr) {
                if (!settings.isEnabled()) {
                    continue;
                }
                Collections.addAll(results, settings.getFullName(), settings.name);
                results.addAll(settings.aliases);
            }

            return results;
        }

        return null;
    }

    private CommandSettings findCommand(String current) {
        CommandSettings foundCommand = null;
        for (var settings : FixinsConfig.commandsModule.commandsSettings) {
            if (!settings.isEnabled()) {
                continue;
            }
            if (settings.name.equalsIgnoreCase(current) || settings.getFullName().equalsIgnoreCase(current)) {
                foundCommand = settings;
                break;
            }
            for (var alias : settings.aliases) {
                if (alias.equalsIgnoreCase(current)) {
                    foundCommand = settings;
                    break;
                }
            }
        }
        return foundCommand;
    }
}

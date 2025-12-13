package mist.mystralix.presentation.commands.slash;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public interface ISlashCommandCRUD {

    MessageEmbed create(SlashCommandInteraction event);

    MessageEmbed read(SlashCommandInteraction event);

    MessageEmbed update(SlashCommandInteraction event);

    MessageEmbed delete(SlashCommandInteraction event);

    MessageEmbed readAll(SlashCommandInteraction event);

}
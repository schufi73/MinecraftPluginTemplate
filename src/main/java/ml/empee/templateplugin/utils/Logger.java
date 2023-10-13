package ml.empee.templateplugin.utils;

import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * This class allow you to easily log messages.
 **/

@UtilityClass
public class Logger {

  private static final String HEX_PREFIX = "&#";
  private static final Pattern HEX_COLOR = Pattern.compile(HEX_PREFIX + "[a-zA-z0-9]{6}");

  @Getter @Setter
  private String prefix;

  @Getter @Setter
  private boolean debug;

  @Setter
  private java.util.logging.Logger consoleLogger = JavaPlugin.getProvidingPlugin(Logger.class).getLogger();

  /**
   * Send a formatted message to the player
   * # Lol
   */
  public void log(CommandSender sender, String message, Object... args) {
    message = String.format(message, args);

    message = message.replace("\n", "\n&r");
    message = prefix + message;
    if (message.endsWith("\n")) {
      message += " ";
    }

    message = message.replace("\t", "    ");

    sender.sendMessage(
        ChatColor.translateAlternateColorCodes(
            '&', translateHexCodes(message)
        ).split("\n")
    );
  }

  private String translateHexCodes(String input) {
    Matcher matcher = HEX_COLOR.matcher(input);
    while (matcher.find()) {
      String group = matcher.group().substring(HEX_PREFIX.length());
      StringBuilder hex = new StringBuilder("&x");
      for (char code : group.toLowerCase().toCharArray()) {
        hex.append("&").append(code);
      }

      input = input.replace(HEX_PREFIX + group, hex.toString());
    }

    return input;
  }

  /**
   * Log to the console a debug message.
   **/
  public void debug(String message, Object... args) {
    if (debug) {
      consoleLogger.info(String.format(Locale.ROOT, message, args));
    }
  }

  /**
   * Log a debug message to a player.
   **/
  public void debug(CommandSender player, String message, Object... args) {
    if (debug) {
      log(player, message, ChatColor.DARK_GRAY, args);
    }
  }

  /**
   * Log to the console an info message.
   **/
  public void info(String message, Object... args) {
    if (consoleLogger.isLoggable(Level.INFO)) {
      consoleLogger.info(String.format(Locale.ROOT, message, args));
    }
  }

  /**
   * Log to the console a warning message.
   **/
  public void warning(String message, Object... args) {
    if (consoleLogger.isLoggable(Level.WARNING)) {
      consoleLogger.warning(String.format(Locale.ROOT, message, args));
    }
  }

  /**
   * Log to the console an error message.
   **/
  public void error(String message, Object... args) {
    if (consoleLogger.isLoggable(Level.SEVERE)) {
      consoleLogger.severe(String.format(Locale.ROOT, message, args));
    }
  }
}

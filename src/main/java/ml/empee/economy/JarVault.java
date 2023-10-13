package ml.empee.economy;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.val;
import ml.empee.economy.config.CommandsConfig;
import ml.empee.economy.config.LangConfig;
import ml.empee.economy.config.client.DbClient;
import ml.empee.economy.controllers.Controller;
import ml.empee.economy.utils.Logger;
import ml.empee.simplemenu.SimpleMenu;
import mr.empee.lightwire.Lightwire;

/**
 * Boot class of this plugin.
 **/

public final class JarVault extends JavaPlugin {

  private final Lightwire iocContainer = new Lightwire();
  private final SimpleMenu simpleMenu = new SimpleMenu();

  /**
   * Called when enabling the plugin
   */
  public void onEnable() {
    simpleMenu.init(this);

    iocContainer.addBean(this);
    iocContainer.loadBeans(getClass().getPackage());

    loadPrefix();
    registerCommands();
  }

  private void loadPrefix() {
    val langConfig = iocContainer.getBean(LangConfig.class);
    Logger.setPrefix(langConfig.translate("prefix"));
  }

  private void registerCommands() {
    var commandManager = iocContainer.getBean(CommandsConfig.class);
    iocContainer.getAllBeans(Controller.class).forEach(
        c -> commandManager.register(c)
    );
  }

  public void onDisable() {
    var dbClient = iocContainer.getBean(DbClient.class);
    if (dbClient != null) {
      dbClient.closeConnections();
    }

    simpleMenu.unregister(this);
  }
}

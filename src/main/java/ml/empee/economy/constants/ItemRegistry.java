package ml.empee.economy.constants;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ml.empee.economy.config.LangConfig;
import mr.empee.lightwire.annotations.Instance;
import mr.empee.lightwire.annotations.Singleton;

/**
 * Contains all the plugin custom items
 */

@Singleton
@RequiredArgsConstructor
public class ItemRegistry {

  @Instance
  private static ItemRegistry instance;

  private final JavaPlugin plugin;
  private final LangConfig langConfig;

  public void reload() {
  }

}

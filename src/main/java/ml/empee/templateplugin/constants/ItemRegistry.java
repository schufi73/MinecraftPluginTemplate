package ml.empee.templateplugin.constants;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.RequiredArgsConstructor;
import ml.empee.templateplugin.config.LangConfig;
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

package ml.empee.templateplugin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.leangen.geantyref.TypeToken;
import lombok.experimental.UtilityClass;

/**
 * Converts complex objects into raw one
 */

@UtilityClass
public class ObjectConverter {

  private static final Gson gson = new GsonBuilder().create();
  private static final Pattern LOCATION_PATTERN = Pattern.compile(
      ".+:-?\\d+(\\.\\d+)?:-?\\d+(\\.\\d+)?:-?\\d+(\\.\\d+)?");

  /**
   * Parse a collection to a string
   */
  public <T> String parseCollection(Collection<T> collection, Function<T, String> mapper) {
    return gson.toJson(
        collection.stream()
            .map(mapper)
            .collect(Collectors.toList())
    );
  }

  /**
   * Parse a string to a collection
   */
  public <T> ArrayList<T> parseCollection(String raw, Function<String, T> mapper) {
    return Arrays.stream(gson.fromJson(raw, String[].class))
        .map(mapper)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public <K, V> String parseMap(Map<K, V> map, Function<K, String> keyMapper, Function<V, String> valueMapper) {
    return gson.toJson(
        map.entrySet().stream()
            .collect(
              Collectors.toMap(
                entry -> keyMapper.apply(entry.getKey()),
                entry -> valueMapper.apply(entry.getValue())
              )
            )
    );
  }

  public <K, V> Map<K, V> parseMap(String json, Function<String, K> keyMapper,
      Function<String, V> valueMapper) {
    return ((Map<String, String>) gson.fromJson(json, Map.class))
        .entrySet().stream()
        .collect(Collectors.toMap(
            entry -> keyMapper.apply(entry.getKey()),
            entry -> valueMapper.apply(entry.getValue())));
  }

  /**
   * Parse a object to json
   */
  public <T> String parse(T object) {
    return gson.toJson(object);
  }

  /**
   * Parse json into an object
   */
  public <T> T parse(String json, TypeToken<T> target) {
    return gson.fromJson(json, target.getType());
  }

  /**
   * Parse json into an object
   */
  public <T> T parse(String json, Class<T> target) {
    return gson.fromJson(json, target);
  }

  /**
   * Parse a location from a string
   *
   * @param rawLoc format "world:x:y:z"
   */
  public Location parseLocation(String rawLoc) {
    if (rawLoc == null || !LOCATION_PATTERN.matcher(rawLoc).matches()) {
      throw new RuntimeException("The location '" + rawLoc + "' must match 'world:x:y:z'");
    }

    String[] coordinates = rawLoc.split(":");
    World world = Bukkit.getWorld(coordinates[0]);
    if (world == null) {
      throw new RuntimeException("Unable to find world " + coordinates[0]);
    }

    return new Location(
        world,
        Double.parseDouble(coordinates[1]),
        Double.parseDouble(coordinates[2]),
        Double.parseDouble(coordinates[3]));
  }

  /**
   * Parse a location into a string
   *
   * @return world:x:y:z
   */
  public String parseLocation(Location loc) {
    return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
  }

  /**
   * Parse a date-time from a string
   *
   * @param rawTime format "2007-12-03T10:15:30"
   */
  public LocalDateTime parseTime(String rawTime) {
    return LocalDateTime.parse(rawTime);
  }

  /**
   * Parse a date-time to a string
   *
   * @return "2007-12-03T10:15:30"
   */
  public String parseTime(LocalDateTime time) {
    return time.toString();
  }

  /**
   * A method to serialize an item to a Base64 string.
   */
  public String parseItem(ItemStack item) throws IOException {
    return parseInventory(new ItemStack[] { item });
  }

  /**
   * A method to get an item from an encoded Base64 string.
   */
  public ItemStack parseItem(String item) throws IOException {
    return parseInventory(item)[0];
  }

  /**
   * A method to serialize an array of items to a Base64 string.
   */
  public String parseInventory(ItemStack[] items) throws IOException {
    try (
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput dataOutput = new BukkitObjectOutputStream(outputStream)) {

      dataOutput.writeInt(items.length);

      for (ItemStack item : items) {
        dataOutput.writeObject(item);
      }

      // Serialize that array
      dataOutput.flush();
      outputStream.flush();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    }
  }

  /**
   * A method to get an array of items from an encoded Base64 string.
   */
  public ItemStack[] parseInventory(String rawItems) throws IOException {
    if (rawItems.isEmpty()) {
      return new ItemStack[0];
    }

    try (
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(rawItems));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
      ItemStack[] items = new ItemStack[dataInput.readInt()];

      // Read the serialized inventory
      for (int i = 0; i < items.length; i++) {
        items[i] = (ItemStack) dataInput.readObject();
      }

      return items;
    } catch (ClassNotFoundException e) {
      throw new IOException("Unable to decode class type.", e);
    }
  }

}

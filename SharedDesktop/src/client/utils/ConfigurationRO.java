package client.utils;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.02.13
 *
 * @version 2.1.20170610
 */
public class ConfigurationRO {
  private final Properties props;

  public ConfigurationRO(File file) throws IOException {
    this(file, null);
  }

  public ConfigurationRO(File file, String charsetName) throws IOException {
    props = new Properties();

    Reader in;
    if (charsetName == null) in = new InputStreamReader(new FileInputStream(file));
    else in = new InputStreamReader(new FileInputStream(file), charsetName);
    try {
      props.load(in);
    } finally {
      in.close();
    }
  }

  public ConfigurationRO(InputStream stream) throws IOException {
    this(stream, null);
  }

  public ConfigurationRO(InputStream stream, String charsetName) throws IOException {
    props = new Properties();

    Reader in;
    if (charsetName == null) in = new InputStreamReader(stream);
    else in = new InputStreamReader(stream, charsetName);
    try {
      props.load(in);
    } finally {
      in.close();
    }
  }

  public String getProperty(String key) {
    return props.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return props.getProperty(key, defaultValue);
  }

  public Enumeration<?> propertyNames() {
    return props.propertyNames();
  }

  public Set<String> stringPropertyNames() {
    return props.stringPropertyNames();
  }
}

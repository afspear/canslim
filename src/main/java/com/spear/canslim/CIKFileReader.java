package com.spear.canslim;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class CIKFileReader {
  public static Optional<String> getCIKFromTicker(String ticker) {
    Path path = null;
    try {
      path = Paths.get(CIKFileReader.class.getClassLoader().getResource("cik_ticker.csv").toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return Optional.empty();
    }

    StringBuilder data = new StringBuilder();
    Stream<String> lines = null;
    try {
      lines = Files.lines(path);
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
    // skip the header of the csv
    return lines.skip(1).filter(s -> s.contains(ticker)).findAny().map(s -> {
      return s.split("\\|")[0];
    });
  }
}

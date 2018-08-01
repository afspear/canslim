package com.spear.canslim;

import com.spear.canslim.web.Web;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class Main {
  public static void main(String[] args) throws IOException {

    Web.init();

    MarketAnalysis dow =  new MarketAnalysis(MarketAnalysis.DowJonesIndustrialAverage).analyze();

    MarketAnalysis sAndP = new MarketAnalysis(MarketAnalysis.SAndP500).analyze();

    MarketAnalysis nasdaq = new MarketAnalysis(MarketAnalysis.NASDAQComposite).analyze();

    try {
      Files.delete(Paths.get("errors.csv"));
    } catch (Exception e) {
      System.out.println("can't delete" + e.getMessage());
    }


    Stream.of(dow, sAndP, nasdaq).forEach(marketAnalysis -> {
      System.out.println(ToStringBuilder.reflectionToString(marketAnalysis, ToStringStyle.MULTI_LINE_STYLE));
    });

  }
}

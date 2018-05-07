import com.spear.canslim.EarningsAnalyzer;
import org.junit.Test;

import java.io.IOException;

public class TestEArningsService {

  EarningsAnalyzer earningsService = new EarningsAnalyzer("AAPL");

  public TestEArningsService() throws IOException {
  }

  @Test
  public void testEarnings() {
    System.out.println("test");
    try {
      earningsService.epsPercentageGainFromAYearAgo();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testSlope() {
    earningsService.getSlopeOfEps();
  }

}

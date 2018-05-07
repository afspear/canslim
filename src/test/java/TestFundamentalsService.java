import com.spear.canslim.FundamentalService;
import org.junit.Test;

import java.io.IOException;

public class TestFundamentalsService {

  FundamentalService fundamentalService = FundamentalService.getInstance();

  @Test
  public void testFundamentals() throws IOException {
    System.out.println(fundamentalService.getFundamentals("AAPL"));
  }
}

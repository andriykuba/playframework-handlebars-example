import static org.junit.Assert.assertTrue;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

public class IntegrationTest {

  /**
   * Check that application is starts and show the processed template
   */
  @Test
  public void test() {
    running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
      browser.goTo("http://localhost:3333");
      assertTrue(browser.pageSource().contains("Main Article"));
    });
  }

}

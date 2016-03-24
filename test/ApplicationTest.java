import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.contentAsString;

import java.util.HashMap;

import org.junit.Test;

import handlebars.HandlebarsApi;
import play.Application;
import play.i18n.Lang;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.twirl.api.Content;

/**
 *
 * Simple (JUnit) tests that can call all parts of a play app. If you are
 * interested in mocking a whole application, see the wiki for more details.
 *
 */
public class ApplicationTest {

  @Test
  public void renderHandlebarsTemplateUaEn() throws Exception {
    // English text in the case if request language is UA but was switched to EN
    // on the server
    assertThat(mockTemplateProcessedWithLanguage("ua", "en"), containsString("Page Sub Header"));
  }

  @Test
  public void renderHandlebarsTemplateEn() throws Exception {
    // English text in the case if request language is UA but was switched to EN
    // on the server
    assertThat(mockTemplateProcessedWithLanguage("en", "en"), containsString("Page Sub Header"));
  }

  @Test
  public void renderHandlebarsTemplateEnUa() throws Exception {
    // English text in the case if request language is UA but was switched to EN
    // on the server
    assertThat(mockTemplateProcessedWithLanguage("en", "ua"), containsString("Підзаголовок сторінки"));
  }

  @Test
  public void renderHandlebarsTemplateCacheRenew() throws Exception {
    // Mock the application
    ApplicationMeta applicationMeta = mockHandlebarsApiWithLanguage("ua", "en");
    
    // Fill the template with the data.
    Content page = applicationMeta.handlebarsApi.html("page", new HashMap<>());

    // Check English text
    assertThat(contentAsString(page), containsString("Page Sub Header"));

    // Setup an Ukrainian language to the HTTP Context
    when(applicationMeta.context.lang()).thenReturn(Lang.forCode("ua"));

    // Process the same template on the same handlebars engine again
    page = applicationMeta.handlebarsApi.html("page", new HashMap<>());
    
    // Check the Ukrainian text  
    assertThat(contentAsString(page), containsString("Підзаголовок сторінки"));
  }

  private class ApplicationMeta {
    final Http.Context context;
    final HandlebarsApi handlebarsApi;

    private ApplicationMeta(Http.Context context, HandlebarsApi handelbarsApi) {
      this.context = context;
      this.handlebarsApi = handelbarsApi;
    }
  }

  private ApplicationMeta mockHandlebarsApiWithLanguage(String requestLang, String sessionLang) throws Exception{
    // Initialize application
    Application application = new GuiceApplicationBuilder().build();

    // Setup an HTTP Context
    Http.Context context = mock(Http.Context.class);

    // Setup the language and messages
    Lang langRequest = Lang.forCode(requestLang);
    Lang langSession = Lang.forCode(sessionLang);
    MessagesApi messagesApi = application.injector().instanceOf(MessagesApi.class);
    Messages messages = new Messages(langRequest, messagesApi);

    // Train the Context
    when(context.lang()).thenReturn(langSession);
    when(context.messages()).thenReturn(messages);
    //Http.Context.current.set(context);

    // Get the handlebars API
    HandlebarsApi handlebarsApi = application.injector().instanceOf(HandlebarsApi.class);

    return new ApplicationMeta(context, handlebarsApi);
  }

  private String mockTemplateProcessedWithLanguage(String requestLang, String sessionLang) throws Exception {
    ApplicationMeta applicationMeta = mockHandlebarsApiWithLanguage(requestLang, sessionLang);

    // Fill the template with the data.
    Content page = applicationMeta.handlebarsApi.html("page", new HashMap<>());

    // Check the content type
    assertEquals("text/html", page.contentType());

    return contentAsString(page);
  }

}

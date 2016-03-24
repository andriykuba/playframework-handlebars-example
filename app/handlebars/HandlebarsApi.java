package handlebars;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.GuavaTemplateCache;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import play.Configuration;
import play.Environment;
import play.i18n.MessagesApi;
import play.twirl.api.Content;

/**
 * Provide access to the handlebars template engine.
 */
@Singleton
public class HandlebarsApi {

  /**
   * Original handlebars engine.
   */
  private final Handlebars handlebars;

  /**
   * Initialize Handlebars engine, register cache, handlers.
   * 
   * @param environment
   *          Play environment, used for getting templates folder
   */
  @Inject
  public HandlebarsApi(final Environment environment, final Configuration configuration, final MessagesApi messagesApi) {
    
    // Initialize the properties
    final Properties properties = new Properties(configuration);

    // Get the template folder
    final File rootFolder = environment.getFile(properties.getDirectory());

    // Put the template extension.
    final TemplateLoader loader = new FileTemplateLoader(rootFolder, properties.getExtension());

    // Initialize the cache. Could be builded from configuration as well
    // For example: CacheBuilder.from(config.getString("hbs.cache")).build()
    final Cache<TemplateSource, Template> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(1000)
        .build();

    // Initialize the engine with the cache
    handlebars = new Handlebars(loader)
        .with(new GuavaTemplateCache(cache));

    // Add helpers. MessagesApi is a singleton so we can use it in the helpers.
    Helpers helpers = new Helpers(messagesApi);
    handlebars.registerHelpers(helpers);
  }

  /**
   * Render the template with the data.
   * 
   * @param templateName
   *          Name of the template to be rendered.
   * @param data
   *          Data to fill the template.
   * @return Compiled and filled with data.
   * @throws Exception
   * 
   */
  public String render(final String templateName, final Object data) throws Exception {
    return handlebars
        .compile(templateName)
        .apply(data);
  }

  /**
   * Calls {@link #render(String, Object) render} method and convert result to the
   * {@link play.twirl.api.Content Content}.
   * 
   * @param templateName
   * @param data
   * @return
   * @throws Exception
   */
  public Content html(final String templateName, final Object data) throws Exception {
    return new HtmlContent(render(templateName, data));
  }

  /**
   * Proxy handlebars configuration to easy accessible java code.
   */
  private class Properties {
    final static String ROOT = "handlebars";
    final static String DIRECTORY = "directory";
    final static String EXTENSION = "extension";

    /**
     * the handlebars configuration.
     */
    private Configuration configuration;

    Properties(final Configuration configuration){
      this.configuration = configuration.getConfig(Properties.ROOT);
    }
    
    /**
     * @return the directory of the templates.
     */
    String getDirectory() {
      return configuration.getString(DIRECTORY);
    }

    /**
     * @return the extension of the template files in the templates directory.
     */
    String getExtension() {
      return configuration.getString(EXTENSION);
    }

  }
}

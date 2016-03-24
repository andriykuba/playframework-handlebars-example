package handlebars;

import com.github.jknack.handlebars.Options;

import play.i18n.MessagesApi;
import play.i18n.Lang;
import play.mvc.Http.Context;

/**
 * Helpers specific for the Play.
 *
 */
public final class Helpers {
  
  final MessagesApi messagesApi;
  
  /**
   * MessagesApi is a singleton so we can use it in helpers
   * @param messagesApi
   */
  public Helpers(final MessagesApi messagesApi){
    this.messagesApi = messagesApi;
  }

  /**
   * Do the same as "@routes.Assets.versioned" in Twirl.
   * 
   * @param url
   *          relative path to the asset.
   * @return actual path to the asset.
   */
  public static CharSequence asset(final String url) {
    return controllers.routes.Assets.versioned(new controllers.Assets.Asset(url)).toString();
  }

  /**
   * Do the same as "@Message(key)" in Twirl. It use MessageFormat for the
   * formatting as well as "@Message(key)".
   * 
   * @param key
   *          message key in the messages.** files.
   * @return message
   */
  public CharSequence message(final String key, final Options options) {
    // Get the current language.
    final Lang lang = Context.current().lang();
    
    // Retrieve the message, internally formatted by MessageFormat.
    return messagesApi.get(lang, key, options.params);
  }
}

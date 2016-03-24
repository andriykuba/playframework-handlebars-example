package controllers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;

import handlebars.HandlebarsApi;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;

public class Application extends Controller {

  @Inject
  private HandlebarsApi handlebarsApi;

  public Result index() throws Exception {    
    // The data.
    final Map<String, Object> data = new HashMap<>();
    data.put("title", "Page Title");
    data.put("header", "Header");
    data.put("main", ImmutableMap.of("article", "Main Article"));
    data.put("footer", "Footer");

    // Fill it with data.
    final Content page = handlebarsApi.html("page", data);

    // Return the page to the client.
    return ok(page);
  }

}

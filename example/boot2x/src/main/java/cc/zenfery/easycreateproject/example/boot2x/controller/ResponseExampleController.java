package cc.zenfery.easycreateproject.example.boot2x.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResponseExampleController {

  /**
   * hello response
   * @return
   */
  @GetMapping(value = "/hello")
  public Map helloResponse(){
    return new HashMap(){{
      put("hello", "hello world");
    }};
  }
}

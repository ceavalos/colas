package sv.com.freund.rsMHFFactuacionElectronica.controller;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RequestMapping(value = "/hello")
@RestController
public class HelloWorldController {
    
    static Logger log = Logger.getLogger(HelloWorldController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> firstPage() {
        return ResponseEntity.ok("Hello World!!!");
    }
}

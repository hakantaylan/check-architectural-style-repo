package ch.martinelli.api;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {

    public String check() {
        return "ok";
    }
}

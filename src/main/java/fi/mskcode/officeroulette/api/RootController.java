package fi.mskcode.officeroulette.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    record RootMessageDto(String message) {}

    @RequestMapping(value = "", method = RequestMethod.GET)
    public RootMessageDto getRoot() {
        return new RootMessageDto("OK");
    }
}

package fi.mskcode.officeroulette.api;

import static java.lang.String.format;

import fi.mskcode.officeroulette.core.DrawService;
import fi.mskcode.officeroulette.error.InvalidRequestParameter;
import fi.mskcode.officeroulette.error.NotImplementedException;
import fi.mskcode.officeroulette.error.ResourceNotFound;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/draws")
public class DrawController {

    private final DrawService drawService;

    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public DrawResponseDto createNewDraw() {
        var draw = drawService.openNewDraw();
        return DrawResponseDto.from(draw);
    }

    @RequestMapping(value = "/{drawId}", method = RequestMethod.GET)
    public FullDrawResponseDto getDraw(@PathVariable("drawId") long drawId) {
        var draw = drawService
                .findDrawById(drawId)
                .orElseThrow(() -> new ResourceNotFound(format("Draw ID %d does not exist", drawId)));
        return FullDrawResponseDto.from(draw);
    }

    @RequestMapping(value = "/{drawId}/employees", method = RequestMethod.POST)
    public void addEmployeesToDraw(
            @PathVariable("drawId") long drawId, @RequestBody AddEmployeesToDrawRequestDto request) {
        if (drawId != request.drawId) {
            throw new InvalidRequestParameter(
                    format("URL and body draw IDs do not match: %d != %d", drawId, request.drawId));
        }

        drawService.addEmployeeIdsToDraw(drawId, request.participants);
    }

    @RequestMapping(value = "/{id}/execute", method = RequestMethod.POST)
    public FullDrawResponseDto executeDraw() {
        // TODO API should be idempotent
        throw new NotImplementedException();
    }
}

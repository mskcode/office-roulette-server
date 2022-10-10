package fi.mskcode.officeroulette.core;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;

import fi.mskcode.officeroulette.error.NotImplementedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DrawService {

    private final DrawDao drawDao;
    private final DrawEmployeesDao drawEmployeesDao;
    private final DrawResultsDao drawResultsDao;

    public DrawService(DrawDao drawDao, DrawEmployeesDao drawEmployeesDao, DrawResultsDao drawResultsDao) {
        this.drawDao = drawDao;
        this.drawEmployeesDao = drawEmployeesDao;
        this.drawResultsDao = drawResultsDao;
    }

    public Draw openNewDraw() {
        return drawDao.insertOpenDraw();
    }

    public void executeDraw(long drawId) {
        throw new NotImplementedException();
    }

    public Optional<FullDraw> findFullDrawById(long drawId) {
        throw new NotImplementedException();
    }

    public List<Draw> findDraws() {
        return drawDao.findDraws();
    }

    public void addEmployeeIdsToDraw(long drawId, List<UUID> employeeIds) {
        var draw = drawDao.findDrawById(drawId)
                .orElseThrow(() -> new IllegalArgumentException(format("Draw ID %d does not exist", drawId)));

        if (draw.status() != Draw.Status.OPEN) {
            throw new IllegalArgumentException(format("Draw ID %d is not OPEN for participation", drawId));
        }

        var notParticipatingEmployeeIds = employeeIds.stream()
                .filter(employeeId -> !drawEmployeesDao.drawContainsEmployeeId(employeeId))
                .collect(toImmutableList());
        drawEmployeesDao.insertEmployeesToDraw(drawId, notParticipatingEmployeeIds);
    }
}

package fi.mskcode.officeroulette.core;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;

import fi.mskcode.officeroulette.error.ImplementationBugException;
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
    private final EmployeeDao employeeDao;

    public DrawService(
            DrawDao drawDao,
            DrawEmployeesDao drawEmployeesDao,
            DrawResultsDao drawResultsDao,
            EmployeeDao employeeDao) {
        this.drawDao = drawDao;
        this.drawEmployeesDao = drawEmployeesDao;
        this.drawResultsDao = drawResultsDao;
        this.employeeDao = employeeDao;
    }

    public Draw openNewDraw() {
        return drawDao.insertOpenDraw();
    }

    public void executeDraw(long drawId) {
        throw new NotImplementedException();
    }

    public Optional<FullDraw> findFullDrawById(long drawId) {
        var maybeDraw = drawDao.findDrawById(drawId);
        if (maybeDraw.isEmpty()) {
            return Optional.empty();
        }

        var drawParticipants = drawEmployeesDao.enumerateEmployeesParticipatingInDraw(drawId).stream()
                .map(employeeId -> employeeDao
                        .findEmployeeById(employeeId)
                        .orElseThrow(() -> new ImplementationBugException(
                                format("Employee ID %s did not exist when it should have", employeeId.toString()))))
                .collect(toImmutableList());

        var drawResult = drawResultsDao.findDrawResultByDrawId(drawId);

        return Optional.of(new FullDraw(maybeDraw.get(), drawParticipants, drawResult));
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

    private Draw findDrawByIdOrThrow(long drawId) {
        return drawDao.findDrawById(drawId)
                .orElseThrow(() -> new IllegalArgumentException(format("Draw ID %d does not exist", drawId)));
    }
}

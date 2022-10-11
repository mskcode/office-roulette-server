package fi.mskcode.officeroulette.core;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;

import fi.mskcode.officeroulette.error.ImplementationBugException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DrawService {

    private final DrawDao drawDao;
    private final DrawEmployeeDao drawEmployeeDao;
    private final DrawResultDao drawResultDao;
    private final EmployeeDao employeeDao;
    private final LotteryService lotteryService;

    public DrawService(
            DrawDao drawDao,
            DrawEmployeeDao drawEmployeeDao,
            DrawResultDao drawResultDao,
            EmployeeDao employeeDao,
            LotteryService lotteryService) {
        this.drawDao = drawDao;
        this.drawEmployeeDao = drawEmployeeDao;
        this.drawResultDao = drawResultDao;
        this.employeeDao = employeeDao;
        this.lotteryService = lotteryService;
    }

    public Draw openNewDraw() {
        return drawDao.insertOpenDraw();
    }

    @Transactional
    public void executeDraw(long drawId) {
        var draw = findDrawByIdOrThrow(drawId);
        if (draw.status() != Draw.Status.OPEN) {
            throw new IllegalArgumentException(format("Draw ID %d is not OPEN", drawId));
        }

        var drawParticipants = drawEmployeeDao.enumerateEmployeesParticipatingInDraw(drawId);
        if (drawParticipants.isEmpty()) {
            throw new IllegalArgumentException(format("Draw ID %d has no participants", drawId));
        }

        var winnerEmployeeId = lotteryService.selectWinningEmployee(drawParticipants);

        drawResultDao.insertDrawResult(drawId, winnerEmployeeId);
        drawDao.updateDrawClosed(drawId);
    }

    public Optional<Draw> findDrawById(long drawId) {
        return drawDao.findDrawById(drawId);
    }

    public Optional<FullDraw> findFullDrawById(long drawId) {
        var maybeDraw = drawDao.findDrawById(drawId);
        if (maybeDraw.isEmpty()) {
            return Optional.empty();
        }

        var drawParticipants = drawEmployeeDao.enumerateEmployeesParticipatingInDraw(drawId).stream()
                .map(employeeId -> employeeDao
                        .findEmployeeById(employeeId)
                        .orElseThrow(() -> new ImplementationBugException(
                                format("Employee ID %s did not exist when it should have", employeeId.toString()))))
                .collect(toImmutableList());

        var drawResult = drawResultDao.findDrawResultByDrawId(drawId);

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
                .filter(employeeId -> !drawEmployeeDao.drawContainsEmployeeId(employeeId))
                .collect(toImmutableList());

        // FIXME can end up raising exception if another request beat us to adding these employees before us
        drawEmployeeDao.insertEmployeesToDraw(drawId, notParticipatingEmployeeIds);
    }

    private Draw findDrawByIdOrThrow(long drawId) {
        return drawDao.findDrawById(drawId)
                .orElseThrow(() -> new IllegalArgumentException(format("Draw ID %d does not exist", drawId)));
    }
}

package fi.mskcode.officeroulette.core;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;

import fi.mskcode.officeroulette.error.ImplementationBugException;
import fi.mskcode.officeroulette.util.TransactionService;
import fi.mskcode.officeroulette.util.TransactionUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DrawService {

    private final DrawDao drawDao;
    private final DrawEmployeeDao drawEmployeeDao;
    private final DrawResultDao drawResultDao;
    private final EmployeeDao employeeDao;
    private final LotteryService lotteryService;
    private final TransactionService transactionService;

    public DrawService(
            DrawDao drawDao,
            DrawEmployeeDao drawEmployeeDao,
            DrawResultDao drawResultDao,
            EmployeeDao employeeDao,
            LotteryService lotteryService,
            TransactionService transactionService) {
        this.drawDao = drawDao;
        this.drawEmployeeDao = drawEmployeeDao;
        this.drawResultDao = drawResultDao;
        this.employeeDao = employeeDao;
        this.lotteryService = lotteryService;
        this.transactionService = transactionService;
    }

    public Draw openNewDraw() {
        return drawDao.insertOpenDraw();
    }

    public void executeDraw(long drawId) {
        TransactionUtils.validateTransactionNotActive();

        var draw = findDrawByIdOrThrow(drawId);
        if (draw.status() != Draw.Status.OPEN) {
            throw new IllegalArgumentException(format("Draw ID %d is not OPEN", drawId));
        }

        var drawParticipants = drawEmployeeDao.enumerateEmployeesParticipatingInDraw(drawId);
        if (drawParticipants.isEmpty()) {
            throw new IllegalArgumentException(format("Draw ID %d has no participants", drawId));
        }

        var winnerEmployeeId = lotteryService.selectWinningEmployee(drawParticipants);

        transactionService.txRequired(() -> {
            drawResultDao.insertDrawResult(drawId, winnerEmployeeId);
            drawDao.updateDrawClosed(drawId);
        });
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
        TransactionUtils.validateTransactionNotActive();

        var draw = drawDao.findDrawById(drawId)
                .orElseThrow(() -> new IllegalArgumentException(format("Draw ID %d does not exist", drawId)));

        if (draw.status() != Draw.Status.OPEN) {
            throw new IllegalArgumentException(format("Draw ID %d is not OPEN for participation", drawId));
        }

        // FIXME race-condition: the following already-added-exclusion and
        //  addition can end up raising exception if another request beat us
        //  to adding these employees before us

        var notParticipatingEmployeeIds = employeeIds.stream()
                .filter(employeeId -> !drawEmployeeDao.drawContainsEmployeeId(employeeId))
                .collect(toImmutableList());

        transactionService.txRequired(() -> {
            drawEmployeeDao.insertEmployeesToDraw(drawId, notParticipatingEmployeeIds);
        });
    }

    private Draw findDrawByIdOrThrow(long drawId) {
        return drawDao.findDrawById(drawId)
                .orElseThrow(() -> new IllegalArgumentException(format("Draw ID %d does not exist", drawId)));
    }
}

package co.com.pragma.schedule;

import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReportSchedule {
    private final UserUseCase userUseCase;

    @Scheduled(cron = "0 0 20 * * ?", zone = "America/Bogota")
    public void sendReportToAdmins(){
        userUseCase.sendReportToAdmins().subscribe();
    }
}

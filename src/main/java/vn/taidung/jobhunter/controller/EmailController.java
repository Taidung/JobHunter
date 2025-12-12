package vn.taidung.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.taidung.jobhunter.service.SubscriberService;
import vn.taidung.jobhunter.util.annotition.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final SubscriberService subscriberService;

    public EmailController(
            SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("winsp203@gmail.com", "test send email",
        // "<h1><b> hello </b></h1>", false, true);
        // this.emailService.sendEmailFromTemplateSync("winsp203@gmail.com", "test send
        // email", "job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }

}

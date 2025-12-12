package vn.taidung.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.taidung.jobhunter.domain.Subscriber;
import vn.taidung.jobhunter.service.SubscriberService;
import vn.taidung.jobhunter.util.SecurityUtil;
import vn.taidung.jobhunter.util.annotition.ApiMessage;
import vn.taidung.jobhunter.util.error.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create a new subscriber")
    public ResponseEntity<Subscriber> createNewSubscriber(@Valid @RequestBody Subscriber requestSubscriber)
            throws ResourceNotFoundException {

        if (this.subscriberService.isEmailExists(requestSubscriber.getEmail())) {
            throw new ResourceNotFoundException(
                    "email: " + requestSubscriber.getEmail() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.subscriberService.handleCreateSubcriber(requestSubscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("update a subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber requestSubscriber)
            throws ResourceNotFoundException {
        if (!this.subscriberService.isIdExists(requestSubscriber.getId())) {
            throw new ResourceNotFoundException(
                    "Id: " + requestSubscriber.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(subscriberService.handleUpdateSubscriber(requestSubscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws ResourceNotFoundException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new ResourceNotFoundException("User not logged in"));

        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}

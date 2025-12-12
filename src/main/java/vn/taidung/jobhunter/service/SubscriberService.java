package vn.taidung.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.Job;
import vn.taidung.jobhunter.domain.Skill;
import vn.taidung.jobhunter.domain.Subscriber;
import vn.taidung.jobhunter.domain.response.email.ResEmailJob;
import vn.taidung.jobhunter.repository.JobRepository;
import vn.taidung.jobhunter.repository.SkillRepository;
import vn.taidung.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository,
            EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber handleCreateSubcriber(Subscriber reqSubscriber) {

        // lấy ra list id từ request
        if (reqSubscriber.getSkills() != null) {
            List<Long> skillIds = reqSubscriber.getSkills()
                    .stream()
                    .map(Skill::getId)
                    .collect(Collectors.toList());

            List<Skill> skillsFromDb = skillRepository.findByIdIn(skillIds);
            reqSubscriber.setSkills(skillsFromDb);
        }

        return this.subscriberRepository.save(reqSubscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber reqSubscriber) {
        Optional<Subscriber> subscriberOptional = this.subscriberRepository.findById(reqSubscriber.getId());

        if (subscriberOptional.isPresent()) {
            Subscriber currentSubscriber = subscriberOptional.get();

            // Cập nhật skills
            List<Long> skillIds = reqSubscriber.getSkills()
                    .stream()
                    .map(Skill::getId)
                    .collect(Collectors.toList());
            List<Skill> skillsFromDb = skillRepository.findByIdIn(skillIds);
            currentSubscriber.setSkills(skillsFromDb);

            // Lưu Subscriber
            return this.subscriberRepository.save(currentSubscriber);
        }

        return null;
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron() {
    // System.out.println(">>> test cron");
    // }

    public boolean isEmailExists(String email) {
        return subscriberRepository.existsByEmail(email);
    }

    public boolean isIdExists(long id) {
        return subscriberRepository.existsById(id);
    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }
}

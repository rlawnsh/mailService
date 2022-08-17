package hello.mailService.controller;

import hello.mailService.config.SecurityConfig;
import hello.mailService.db.ApiStatisticsRepository;
import hello.mailService.dto.MailDto;
import hello.mailService.service.Bucket4jService;
import hello.mailService.service.MailService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;
    private final ApiStatisticsRepository apiStatisticsRepository;
    private static final Bucket bucket = Bucket4jService.generateSimpleBucket();

    @PostMapping(value = "/mail/send", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<String> sendMail(@RequestParam String fromAddress,
                                           @RequestParam("toAddress") List<String> toAddressList,
                                           @RequestParam("ccAddress") List<String> ccAddressList,
                                           @RequestParam String title,
                                           @RequestParam String content,
                                           @RequestParam(value = "images", required = false) List<MultipartFile> imgList,
                                           @RequestParam(value = "files", required = false) List<MultipartFile> fileList,
                                           HttpServletRequest request) throws MessagingException, IOException {

        MailDto mailDto = new MailDto(fromAddress, toAddressList, ccAddressList, title, content);
        HashMap<String, String> authToken = SecurityConfig.authToken;
        if (bucket.tryConsume(1)) {
            log.info(">>> Remain bucket count : {}", bucket.getAvailableTokens());
            String authorization = request.getHeader("Authorization");
            for (String key : authToken.keySet()) {
                String value = authToken.get(key);
                if (value.equals(authorization)) {
                    log.info("보낸 서버는 : {}", key);
                    apiStatisticsRepository.updateCount(key);
                }
            }
            mailService.sendMail(mailDto, fileList);
            return new ResponseEntity<String>("Ok", HttpStatus.OK);
        }
        log.warn(">>> Exhausted limit in bucket");
        return new ResponseEntity<String>("Too Many Request", HttpStatus.TOO_MANY_REQUESTS);
    }
}

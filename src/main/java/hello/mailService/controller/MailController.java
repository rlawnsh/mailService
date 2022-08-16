package hello.mailService.controller;

import hello.mailService.dto.MailDto;
import hello.mailService.service.Bucket4jService;
import hello.mailService.service.MailService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;
    private static final Bucket bucket = Bucket4jService.generateSimpleBucket();


    @PostMapping("/mail/send")
    public ResponseEntity<String> sendMail(@RequestPart("mailInfo") MailDto mailDto, @RequestPart(value = "files", required = false) List<MultipartFile> fileList) throws MessagingException, IOException {

        if (bucket.tryConsume(1)) {
            log.info(">>> Remain bucket count : {}", bucket.getAvailableTokens());
            mailService.sendMail(mailDto, fileList);
            return new ResponseEntity<String>("Ok", HttpStatus.OK);
        }
        log.warn(">>> Exhausted limit in bucket");
        return new ResponseEntity<String>("Too Many Request", HttpStatus.TOO_MANY_REQUESTS);
    }
}

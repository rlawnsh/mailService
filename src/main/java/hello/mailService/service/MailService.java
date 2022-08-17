package hello.mailService.service;

import hello.mailService.db.ApiStatistics;
import hello.mailService.db.ApiStatisticsRepository;
import hello.mailService.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final ApiStatisticsRepository apiStatisticsRepository;

    public void sendMail(MailDto mailDto, List<MultipartFile> fileList) throws MessagingException, IOException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        int toAddressSize = mailDto.getToAddressList().size();
        int toCcSize = mailDto.getCcAddressList().size();

        messageHelper.setFrom(mailDto.getFromAddress());
        messageHelper.setTo((String[]) mailDto.getToAddressList().toArray(new String[toAddressSize]));
        messageHelper.setCc((String[]) mailDto.getCcAddressList().toArray(new String[toCcSize]));
        messageHelper.setSubject(mailDto.getTitle());
        messageHelper.setText(mailDto.getTextContent(), mailDto.getHtmlContent());
//         파일첨부
        if (fileList != null) {
            for (MultipartFile file : fileList) {
                messageHelper.addAttachment(file.getOriginalFilename(), new ByteArrayResource(file.getBytes()), file.getContentType());
            }
        }

        javaMailSender.send(message);

    }

    @Transactional
    public void updateCount(String key) {
        if (apiStatisticsRepository.findByServerNameAndLocalDate(key, LocalDate.now()) != null) {
            apiStatisticsRepository.updateCount(key, LocalDate.now());
        } else {
            ApiStatistics apiStatistics = new ApiStatistics(key, 1);
            apiStatisticsRepository.save(apiStatistics);
        }
    }
}

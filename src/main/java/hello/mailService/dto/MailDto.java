package hello.mailService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {

    private String fromAddress;
    private List<String> toAddressList;
    private List<String> ccAddressList;
    private String title;
    private String textContent;
    private String htmlContent;
}

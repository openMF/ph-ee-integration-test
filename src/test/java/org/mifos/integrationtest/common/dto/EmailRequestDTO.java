package org.mifos.integrationtest.common.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestDTO {

    private List<String> to;
    private String subject;
    private String body;

    public EmailRequestDTO(List<String> to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}

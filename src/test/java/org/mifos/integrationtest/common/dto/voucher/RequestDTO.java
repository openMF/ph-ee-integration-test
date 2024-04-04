package org.mifos.integrationtest.common.dto.voucher;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    public String requestID;
    public String batchID;
    public ArrayList<VoucherInstruction> voucherInstructions;
}

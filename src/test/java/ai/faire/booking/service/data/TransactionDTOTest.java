package ai.faire.booking.service.data;

import ai.faire.booking.DTO.TransactionDTO;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.springframework.stereotype.Component;

@Component
public class TransactionDTOTest {
    
    public TransactionDTO getTransactionDTO() throws ParseException {
        TransactionDTO transactionDTO = new TransactionDTO();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = "2021-10-07";
        
        transactionDTO.setAccountNumber("abc123321123");
        transactionDTO.setTransaction(BigDecimal.valueOf(-50));
        transactionDTO.setTransactionDate(sdf.parse(strDate));
        
        return transactionDTO;
    }
}

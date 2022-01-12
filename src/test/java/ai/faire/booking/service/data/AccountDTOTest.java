package ai.faire.booking.service.data;

import ai.faire.booking.DTO.AccountDTO;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class AccountDTOTest {
    
    public AccountDTO getAccountDto() throws ParseException {
        AccountDTO accountDTO = new AccountDTO();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = "2021-10-01";
        
        accountDTO.setAccountNumber("abc123321123");
        accountDTO.setBalance(BigDecimal.valueOf(150));
        accountDTO.setCreateDate(sdf.parse(strDate));
        
        return accountDTO;
    }
    
}

 
package ai.faire.booking.DTO;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

 @Data
public class TransactionDTO {
      @NotEmpty
    private String accountNumber;
    
    @NotEmpty
    private BigDecimal transaction;
    
    @NotEmpty
    private Date transactionDate;
    
    
}

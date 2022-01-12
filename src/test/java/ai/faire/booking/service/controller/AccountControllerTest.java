package ai.faire.booking.service.controller;

import ai.faire.booking.DTO.AccountDTO;
import ai.faire.booking.DTO.TransactionDTO;
import ai.faire.booking.service.data.AccountDTOTest;
import ai.faire.booking.service.data.TransactionDTOTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AccountDTOTest accountDTOTest;

    @Autowired
    private TransactionDTOTest transactionDTOTest;

    @Test
    public void testAddAccount() throws Exception {

        AccountDTO accountDTO = accountDTOTest.getAccountDto();

        this.mvc.perform(MockMvcRequestBuilders.post("/api/add-account")
                .content(asJsonString(accountDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void testTransaction() throws Exception {

        TransactionDTO transactionDTO = transactionDTOTest.getTransactionDTO();

        this.mvc.perform(MockMvcRequestBuilders.post("/api/add-transaction")
                .content(asJsonString(transactionDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

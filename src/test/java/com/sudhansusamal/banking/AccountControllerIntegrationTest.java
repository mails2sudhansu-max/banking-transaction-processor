package com.sudhansusamal.banking;

import com.sudhansusamal.banking.dto.AmountRequest;
import com.sudhansusamal.banking.dto.CreateAccountRequest;
import com.sudhansusamal.banking.dto.TransferRequest;
import com.sudhansusamal.banking.model.Account;
import com.sudhansusamal.banking.repository.AccountRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldCreateAccountAndCheckBalance() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest("ACC-001", new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$.balance").value(1000.00));

        mockMvc.perform(get("/api/accounts/ACC-001/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void shouldRejectWithdrawalThatExceedsBalance() throws Exception {
        accountRepository.save(new Account("ACC-998", new BigDecimal("100.00")));

        AmountRequest request = new AmountRequest(new BigDecimal("200.00"));

        mockMvc.perform(post("/api/accounts/ACC-998/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldTransferFundsBetweenAccounts() throws Exception {
        accountRepository.save(new Account("ACC-201", new BigDecimal("500.00")));
        accountRepository.save(new Account("ACC-202", new BigDecimal("250.00")));

        TransferRequest request = new TransferRequest("ACC-201", "ACC-202", new BigDecimal("100.00"));

        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-201"))
                .andExpect(jsonPath("$.balance").value(400.00));
    }
}

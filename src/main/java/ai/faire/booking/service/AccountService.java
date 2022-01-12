package ai.faire.booking.service;

import ai.faire.booking.DTO.AccountDTO;
import ai.faire.booking.DTO.TransactionDTO;
import ai.faire.booking.model.Accounts;
import ai.faire.booking.model.Monitoring;
import ai.faire.booking.model.Transactions;
import ai.faire.booking.model.TransactionsTypeEnum;
import ai.faire.booking.repository.AccountsRepository;
import ai.faire.booking.repository.MonitoringRepository;
import ai.faire.booking.repository.TransactionsRepository;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private MonitoringRepository monitoringRepository;

    public Accounts mapToEntityAccount(AccountDTO accountDto) {

        Accounts account = new Accounts().builder()
                .accountNumber(accountDto.getAccountNumber())
                .balance(accountDto.getBalance())
                .createDate(accountDto.getCreateDate())
                .updateDate(new Date())
                .build();

        return account;
    }

    public Transactions mapToEntityTransaction(TransactionDTO transactionDto) {

        Accounts account = accountsRepository.findByAccountNumber(transactionDto.getAccountNumber());

        Transactions transaction = new Transactions().builder()
                .accountNumber(account)
                .action(checkTransactionType(transactionDto.getTransaction()))
                .transaction(transactionDto.getTransaction())
                .createDate(new Date())
                .transactionDate(transactionDto.getTransactionDate())
                .build();

        return transaction;
    }

    public void addAccount(AccountDTO accountDto) {

        if (!accountsRepository.existsByAccountNumber(accountDto.getAccountNumber())) {

            Accounts account = mapToEntityAccount(accountDto);

            accountsRepository.save(account);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already exist!");
        }
    }

    @Transactional
    public void addTransaction(TransactionDTO transactionDto) {

        if (transactionDto.getTransaction().compareTo(BigDecimal.ZERO) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction's amount can not be zero!");
        }
        if (accountsRepository.existsByAccountNumber(transactionDto.getAccountNumber())) {

            Transactions transaction = mapToEntityTransaction(transactionDto);

            transactionsRepository.save(transaction);

            changeBalance(transactionDto);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exist!");
        }
    }

    @Transactional
    public void changeBalance(TransactionDTO transactionDto) {

        List<String> notifications = new ArrayList();

        Monitoring monitoring = new Monitoring().builder()
                .accountNumber(transactionDto.getAccountNumber())
                .balance(transactionDto.getTransaction())
                .date(transactionDto.getTransactionDate())
                .build();

        BigDecimal accountInitBalance = accountsRepository.findByAccountNumber(transactionDto.getAccountNumber()).getBalance();

        List<Monitoring> monitoringsBefore = monitoringRepository.findAllWithAccountNumberAndDateBefore(transactionDto.getTransactionDate(), transactionDto.getAccountNumber());

        List<Monitoring> monitoringsAfter = monitoringRepository.findAllWithAccountNumberAndDateAfter(transactionDto.getTransactionDate(), transactionDto.getAccountNumber());

        Monitoring monitoringCurrent = monitoringRepository.findAllWithAccountNumberAndDate(transactionDto.getTransactionDate(), transactionDto.getAccountNumber());

        if (monitoringsBefore.isEmpty()) {

            if (ObjectUtils.isEmpty(monitoringCurrent)) {
                monitoring.setBalance(accountInitBalance.add(transactionDto.getTransaction()));
                monitoringRepository.save(monitoring);

                notifications.add(createMessage(monitoring.getAccountNumber(), monitoring.getBalance(), monitoring.getDate()));
            } else {
                Optional<Monitoring> monit = monitoringRepository.findById(monitoringCurrent.getId());
                monit.get().setBalance(monit.get().getBalance().add(transactionDto.getTransaction()));
                monitoringRepository.save(monit.get());

                notifications.add(createMessage(monit.get().getAccountNumber(), monit.get().getBalance(), monit.get().getDate()));
            }
        } else if (!monitoringsBefore.isEmpty()) {

            Monitoring lastMonitoring = Collections.max(monitoringsBefore, Comparator.comparing(c -> c.getDate()));

            if (ObjectUtils.isEmpty(monitoringCurrent)) {
                monitoring.setBalance(lastMonitoring.getBalance().add(transactionDto.getTransaction()));
                monitoringRepository.save(monitoring);

                notifications.add(createMessage(monitoring.getAccountNumber(), monitoring.getBalance(), monitoring.getDate()));
            } else {
                Optional<Monitoring> monit = monitoringRepository.findById(monitoringCurrent.getId());
                monit.get().setBalance(monit.get().getBalance().add(transactionDto.getTransaction()));
                monitoringRepository.save(monit.get());

                notifications.add(createMessage(monit.get().getAccountNumber(), monit.get().getBalance(), monit.get().getDate()));
            }
        }

        if (!monitoringsAfter.isEmpty()) {
            for (Monitoring mon : monitoringsAfter) {
                mon.setBalance(mon.getBalance().add(transactionDto.getTransaction()));
                monitoringRepository.save(mon);

                notifications.add(createMessage(mon.getAccountNumber(), mon.getBalance(), mon.getDate()));
            }
        }

        sendNotifications(notifications);
    }

    public TransactionsTypeEnum checkTransactionType(BigDecimal transaction) {

        if (transaction.compareTo(BigDecimal.ZERO) > 0) {
            return TransactionsTypeEnum.INCOME;
        } else {
            return TransactionsTypeEnum.EXPSENSE;
        }
    }

    public String createMessage(String accNum, BigDecimal balance, Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String result = "balance changed " + "(" + accNum + ", " + balance
                + " EUR" + ", " + sdf.format(date) + ")";

        return result;
    }

    public void sendNotifications(List<String> notifications) {

        final String uri = "http://localhost:8885/api/notifications";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<Object>(notifications, headers);
        ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<String>>() {
        });
    }
}

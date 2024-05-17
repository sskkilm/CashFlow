package com.sskkilm.cashflow.service;

import com.sskkilm.cashflow.dto.CreateRemittanceDto;
import com.sskkilm.cashflow.dto.RemittanceDto;
import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.Remittance;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.AccountErrorCode;
import com.sskkilm.cashflow.enums.AccountStatus;
import com.sskkilm.cashflow.enums.Authority;
import com.sskkilm.cashflow.enums.RemittanceErrorCode;
import com.sskkilm.cashflow.exception.CustomException;
import com.sskkilm.cashflow.repository.AccountRepository;
import com.sskkilm.cashflow.repository.RemittanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RemittanceServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RemittanceRepository remittanceRepository;

    @InjectMocks
    private RemittanceService remittanceService;

    @Test
    @DisplayName("송금 성공")
    void createRemittance_success() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        Account account = Account.builder()
                .id(1L)
                .user(user)
                .status(AccountStatus.ACTIVE)
                .balance(1000)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(account));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(2L)
                                .user(user)
                                .status(AccountStatus.ACTIVE)
                                .balance(1000)
                                .build()
                ));
        LocalDateTime createdAt = LocalDateTime.of(
                2024, 5, 5,
                6, 30
        );
        given(remittanceRepository.save(any()))
                .willReturn(
                        Remittance.builder()
                                .receivingAccountNumber("1122334455")
                                .amount(1000)
                                .accountBalanceSnapshot(0)
                                .account(account)
                                .createdAt(createdAt)
                                .build()
                );

        //when
        CreateRemittanceDto.Response response = remittanceService.createRemittance(
                new CreateRemittanceDto.Request(
                        1L,
                        "1122334455",
                        1000), user
        );

        //then
        assertEquals("1122334455", response.receivingAccountNumber());
        assertEquals(1000, response.remittanceAmount());
        assertEquals(0, response.accountBalanceSnapshot());
        assertEquals(createdAt, response.createdAt());
    }

    @Test
    @DisplayName("송금 실패 - 존재하지 않는 계좌")
    void createRemittance_fail_AccountNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.createRemittance(
                        new CreateRemittanceDto.Request(
                                1L,
                                "1122334455",
                                1000
                        ), user
                )
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("송금 실패 - 계좌 소유주 다름")
    void createRemittance_fail_AccountUserUnMatch() {
        //given
        User user1 = User.builder()
                .id(1L)
                .loginId("root1")
                .password("root1")
                .role(Authority.ROLE_USER)
                .build();
        User user2 = User.builder()
                .id(2L)
                .loginId("root2")
                .password("root2")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user1)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.createRemittance(
                        new CreateRemittanceDto.Request(
                                1L,
                                "1122334455",
                                1000
                        ), user2
                )
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_USER_UN_MATCH, customException.getErrorCode());
    }

    @Test
    @DisplayName("송금 실패 - 사용할 수 없는 계좌")
    void createRemittance_fail_AccountCanNotUse() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.INACTIVE)
                                .user(user)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.createRemittance(
                        new CreateRemittanceDto.Request(
                                1L,
                                "1122334455",
                                1000
                        ), user
                )
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_CAN_NOT_USE, customException.getErrorCode());
    }

    @Test
    @DisplayName("송금 실패 - 계좌 잔액 부족")
    void createRemittance_fail_AccountBalanceInsufficient() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.ACTIVE)
                                .balance(0)
                                .user(user)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.createRemittance(
                        new CreateRemittanceDto.Request(
                                1L,
                                "1122334455",
                                1000
                        ), user
                )
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_BALANCE_INSUFFICIENT, customException.getErrorCode());
    }

    @Test
    @DisplayName("송금 실패 - 존재하지 않는 수금 계좌")
    void createRemittance_fail_ReceivingAccountNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.ACTIVE)
                                .balance(1000)
                                .user(user)
                                .build()
                ));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.createRemittance(
                        new CreateRemittanceDto.Request(
                                1L,
                                "1122334455",
                                1000
                        ), user
                )
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("송금 실패 - 수금 계좌 비활성")
    void createRemittance_fail_ReceivingAccountCanNotUse() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.ACTIVE)
                                .balance(1000)
                                .user(user)
                                .build()
                ));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(2L)
                                .status(AccountStatus.INACTIVE)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.createRemittance(
                        new CreateRemittanceDto.Request(
                                1L,
                                "1122334455",
                                1000
                        ), user
                )
        );

        //then
        assertEquals(RemittanceErrorCode.RECEIVING_ACCOUNT_CAN_NOT_USE, customException.getErrorCode());
    }

    @Test
    @DisplayName("송금 실패 - 송금 계좌와 수금 계좌가 같음")
    void createRemittance_fail_RemittanceAndReceivingAccountSame() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.ACTIVE)
                                .balance(1000)
                                .user(user)
                                .build()
                ));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .status(AccountStatus.ACTIVE)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.createRemittance(
                        new CreateRemittanceDto.Request(
                                1L,
                                "1122334455",
                                1000
                        ), user
                )
        );

        //then
        assertEquals(RemittanceErrorCode.REMITTANCE_AND_RECEIVING_ACCOUNT_SAME, customException.getErrorCode());
    }

    @Test
    @DisplayName("모든 송금 내역 조회 성공")
    void getRemittanceList_success() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user)
                                .build()
                ));
        LocalDateTime createdAt1 = LocalDateTime.of(
                2024, 5, 5,
                0, 0
        );
        List<Remittance> remittanceList = List.of(
                Remittance.builder()
                        .receivingAccountNumber("1122334455")
                        .amount(1000)
                        .accountBalanceSnapshot(0)
                        .createdAt(createdAt1)
                        .build()
        );
        PageRequest pageRequest = PageRequest.of(
                0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
        );
        given(remittanceRepository.findAllByAccount(any(), any()))
                .willReturn(new SliceImpl<>(remittanceList, pageRequest, false));

        //when
        Slice<RemittanceDto> remittanceDtoPage = remittanceService.getRemittanceList(pageRequest, 1L, user);

        //then
        assertEquals("1122334455", remittanceDtoPage.getContent().get(0).receivingAccountNumber());
        assertEquals(1000, remittanceDtoPage.getContent().get(0).remittanceAmount());
        assertEquals(0, remittanceDtoPage.getContent().get(0).accountBalanceSnapshot());
        assertEquals(createdAt1, remittanceDtoPage.getContent().get(0).createdAt());
    }

    @Test
    @DisplayName("모든 송금 내역 조회 실패 - 존재하지 않는 계좌")
    void getRemittanceList_fail_AccountNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        PageRequest pageRequest = PageRequest.of(
                0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
        );

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.getRemittanceList(pageRequest, 1L, user)
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("모든 송금 내역 조회 실패 - 계좌 소유주 다름")
    void getRemittanceList_fail_AccountUserUnMatch() {
        //given
        User user1 = User.builder()
                .id(1L)
                .loginId("root1")
                .password("root1")
                .role(Authority.ROLE_USER)
                .build();
        User user2 = User.builder()
                .id(2L)
                .loginId("root2")
                .password("root2")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user1)
                                .build()
                ));
        PageRequest pageRequest = PageRequest.of(
                0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
        );

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.getRemittanceList(pageRequest, 1L, user2)
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_USER_UN_MATCH, customException.getErrorCode());
    }

    @Test
    @DisplayName("기간 내 송금 내역 조회 성공")
    void getRemittanceListBetween_success() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user)
                                .build()
                ));
        LocalDateTime createdAt1 = LocalDateTime.of(
                2024, 5, 5,
                0, 0
        );
        List<Remittance> remittanceList = List.of(
                Remittance.builder()
                        .receivingAccountNumber("1122334455")
                        .amount(1000)
                        .accountBalanceSnapshot(0)
                        .createdAt(createdAt1)
                        .build()
        );
        PageRequest pageRequest = PageRequest.of(
                0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
        );
        given(remittanceRepository.findAllByAccountAndCreatedAt(
                any(), any(), any(), any())
        ).willReturn(new PageImpl<>(remittanceList, pageRequest, 1));

        //when
        Page<RemittanceDto> remittanceDtoPage = remittanceService.getRemittanceList(
                pageRequest,
                1L, user,
                LocalDateTime.of(
                        2024, 5, 1,
                        0, 0
                ),
                LocalDateTime.of(
                        2024, 5, 10,
                        0, 0
                )
        );

        //then
        assertEquals("1122334455", remittanceDtoPage.getContent().get(0).receivingAccountNumber());
        assertEquals(1000, remittanceDtoPage.getContent().get(0).remittanceAmount());
        assertEquals(0, remittanceDtoPage.getContent().get(0).accountBalanceSnapshot());
        assertEquals(createdAt1, remittanceDtoPage.getContent().get(0).createdAt());
    }

    @Test
    @DisplayName("기간 내 송금 내역 조회 실패 - 존재하지 않는 계좌")
    void getRemittanceListBetween_fail_AccountNotFound() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();
        PageRequest pageRequest = PageRequest.of(
                0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
        );
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.getRemittanceList(
                        pageRequest,
                        1L, user,
                        LocalDateTime.of(
                                2024, 5, 1,
                                0, 0
                        ),
                        LocalDateTime.of(
                                2024, 5, 10,
                                0, 0
                        )
                )
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("기간 내 송금 내역 조회 실패 - 계좌 소유주 다름")
    void getRemittanceListBetween_fail_AccountUserUnMatch() {
        //given
        User user1 = User.builder()
                .id(1L)
                .loginId("root1")
                .password("root1")
                .role(Authority.ROLE_USER)
                .build();
        User user2 = User.builder()
                .id(2L)
                .loginId("root2")
                .password("root2")
                .role(Authority.ROLE_USER)
                .build();
        PageRequest pageRequest = PageRequest.of(
                0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
        );
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user1)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.getRemittanceList(
                        pageRequest,
                        1L, user2,
                        LocalDateTime.of(
                                2024, 5, 1,
                                0, 0
                        ),
                        LocalDateTime.of(
                                2024, 5, 10,
                                0, 0
                        )
                )
        );

        //then
        assertEquals(AccountErrorCode.ACCOUNT_USER_UN_MATCH, customException.getErrorCode());
    }

    @Test
    @DisplayName("기간 내 송금 내역 조회 실패 - 송금 이력 조회는 시작일부터 최대 1년까지 가능")
    void getRemittanceListBetween_fail_RemittanceHistoryInquiryPeriodLimited() {
        //given
        User user = User.builder()
                .id(1L)
                .loginId("root")
                .password("root")
                .role(Authority.ROLE_USER)
                .build();

        PageRequest pageRequest = PageRequest.of(
                0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
        );
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(
                        Account.builder()
                                .id(1L)
                                .user(user)
                                .build()
                ));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> remittanceService.getRemittanceList(
                        pageRequest,
                        1L, user,
                        LocalDateTime.of(
                                2023, 5, 1,
                                0, 0
                        ),
                        LocalDateTime.of(
                                2024, 5, 10,
                                0, 0
                        )
                )
        );

        //then
        assertEquals(RemittanceErrorCode.REMITTANCE_HISTORY_INQUIRY_PERIOD_LIMITED, customException.getErrorCode());
    }
}
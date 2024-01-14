package com.market.alarm.infrastructure;

import com.market.alarm.domain.MailStatus;
import com.market.alarm.domain.MailStorage;
import com.market.helper.JdbcTestHelper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.market.alarm.fxiture.MailStorageFixture.이메일_저장소_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MailStorageJpaRepositoryTest extends JdbcTestHelper {

    @Autowired
    private MailStorageJpaRepository mailStorageJpaRepository;

    @Test
    void 이메일_전송_상태를_저장한다() {
        // given
        Long memberId = 1L;
        String email = "email@email.com";
        String nickname = "nickname";
        MailStatus mailStatus = MailStatus.DONE;

        MailStorage storage = 이메일_저장소_생성(memberId, email, nickname, mailStatus);

        // when
        MailStorage result = mailStorageJpaRepository.save(storage);

        // then
        assertThat(storage).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(result);
    }

    @Test
    void 메일_상태에_기반하여_찾는다() {
        // given
        Long memberId = 1L;
        String email = "email@email.com";
        String nickname = "nickname";
        MailStatus mailStatus = MailStatus.DONE;
        MailStorage savedStorage = mailStorageJpaRepository.save(이메일_저장소_생성(memberId, email, nickname, mailStatus));

        // when
        List<MailStorage> result = mailStorageJpaRepository.findAllByMailStatus(mailStatus);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(1);
            softly.assertThat(result.get(0))
                    .usingRecursiveComparison()
                    .isEqualTo(savedStorage);
        });
    }
}

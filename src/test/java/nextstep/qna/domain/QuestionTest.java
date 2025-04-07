package nextstep.qna.domain;

import nextstep.qna.CannotDeleteException;
import nextstep.users.domain.NsUserTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QuestionTest {
    public static final Question Q1 = new Question(NsUserTest.JAVAJIGI, "title1", "contents1");
    public static final Question Q2 = new Question(NsUserTest.SANJIGI, "title2", "contents2");

    private Question question;
    private Answer answer1;
    private Answer answer2;

    @BeforeEach
    void setUp() {
        question = new Question(NsUserTest.JAVAJIGI, "title", "contents");
        answer1 = new Answer(NsUserTest.JAVAJIGI, question, "Answers Contents1");
        answer2 = new Answer(NsUserTest.SANJIGI, question, "Answers Contents2");
    }

    @DisplayName("질문의 작성자와 삭제하려고 하는 사용자가 다른 경우 예외 발생")
    @Test
    void delete() {
        Assertions.assertThatThrownBy(() -> question.delete(NsUserTest.SANJIGI))
                .isExactlyInstanceOf(CannotDeleteException.class)
                .hasMessage("질문을 삭제할 권한이 없습니다.");
    }

    @DisplayName("다른 사람이 쓴 답변이 있는 경우 질문을 삭제할 수 없어 예외 발생")
    @Test
    void delete2() {
        question.addAnswer(answer2);

        Assertions.assertThatThrownBy(() -> question.delete(NsUserTest.JAVAJIGI))
                .isExactlyInstanceOf(CannotDeleteException.class)
                .hasMessage("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
    }

    @DisplayName("내가 작성한 질문에 내가 작성한 답변만 있는 경우 삭제 가능하다.")
    @Test
    void delete3() {
        question.addAnswer(answer1);
        question.delete(NsUserTest.JAVAJIGI);
        Assertions.assertThat(question.isDeleted()).isTrue();
    }

    @DisplayName("삭제된 질문은 삭제된 상태로 변경된다.")
    @Test
    void delete4() {
        Assertions.assertThat(question.isDeleted()).isFalse();
        question.delete(NsUserTest.JAVAJIGI);
        Assertions.assertThat(question.isDeleted()).isTrue();
    }

    @DisplayName("질문이 삭제된 경우 답변도 삭제된다.")
    @Test
    void delete5() {
        question.addAnswer(answer1);
        Assertions.assertThat(answer1.isDeleted()).isFalse();

        question.delete(NsUserTest.JAVAJIGI);

        Assertions.assertThat(question.isDeleted()).isTrue();
        question.getAnswers()
                .forEach(answer -> Assertions.assertThat(answer.isDeleted()).isTrue());
    }
}

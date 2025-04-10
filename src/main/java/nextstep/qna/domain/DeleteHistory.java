package nextstep.qna.domain;

import nextstep.users.domain.NsUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeleteHistory {
    private Long id;

    private ContentType contentType;

    private Long contentId;

    private NsUser deletedBy;

    private LocalDateTime createdDate = LocalDateTime.now();

    public DeleteHistory() {
    }

    public DeleteHistory(ContentType contentType, Long contentId, NsUser deletedBy, LocalDateTime createdDate) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
        this.createdDate = createdDate;
    }

    public static List<DeleteHistory> of(Question question) {
        validateQuestion(question);
        return Stream.concat(
                Stream.of(new DeleteHistory(ContentType.QUESTION, question.getId(), question.getWriter(), question.deletedDate())),
                question.getAnswers().stream()
                        .map(answer -> new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter(), question.deletedDate()))
        ).collect(Collectors.toUnmodifiableList());
    }

    private static void validateQuestion(Question question) {
        if (!question.isDeleted()) {
            throw new IllegalArgumentException("질문이 삭제되지 않았습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteHistory that = (DeleteHistory) o;
        return Objects.equals(id, that.id) &&
                contentType == that.contentType &&
                Objects.equals(contentId, that.contentId) &&
                Objects.equals(deletedBy, that.deletedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contentType, contentId, deletedBy);
    }

    @Override
    public String toString() {
        return "DeleteHistory [id=" + id + ", contentType=" + contentType + ", contentId=" + contentId + ", deletedBy="
                + deletedBy + ", createdDate=" + createdDate + "]";
    }
}

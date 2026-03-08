package com.wallet.domain.point

enum class PointType(
    val description: String,
    private val direction: Direction,
) {
    INIT("초기화", Direction.NONE),

    // ─── 적립 ──────────────────────────────────────
    QUESTION_REGISTER("질문 등록", Direction.DEPOSIT),
    ANSWER_REGISTER("답변 등록", Direction.DEPOSIT),
    ANSWER_ACCEPTED("답변 채택", Direction.DEPOSIT),
    FRIEND_INVITE("친구 초대", Direction.DEPOSIT),
    REVIEW_REGISTER("리뷰 등록", Direction.DEPOSIT),
    ADJUSTMENT_ADD("운영자 지급", Direction.DEPOSIT),

    // ─── 차감 ──────────────────────────────────────
    POINT_USE("포인트 사용", Direction.WITHDRAW),
    EXPIRATION("포인트 만료", Direction.WITHDRAW),
    ADJUSTMENT_SUB("운영자 차감", Direction.WITHDRAW),
    ;

    fun isDeposit() = direction == Direction.DEPOSIT

    fun isWithdraw() = direction == Direction.WITHDRAW

    private enum class Direction { NONE, DEPOSIT, WITHDRAW }
}

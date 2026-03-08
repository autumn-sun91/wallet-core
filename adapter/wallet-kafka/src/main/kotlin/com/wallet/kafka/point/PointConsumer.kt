package com.wallet.kafka.point

import com.wallet.application.point.port.inbound.GrantPointFromConsumeUseCase
import com.wallet.kafka.point.config.PointGrantMessage
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class PointConsumer(
    private val grantPointFromConsumeUseCase: GrantPointFromConsumeUseCase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${kafka.topic.point-grant}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(
        message: PointGrantMessage,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String?,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) timestamp: Long,
    ) {
        log.info(
            "포인트 메시지 수신 | topic={} partition={} offset={} key={} timestamp={} userId={} type={} amount={}",
            topic,
            partition,
            offset,
            key,
            timestamp,
            message.userId,
            message.type,
            message.amount,
        )

        grantPointFromConsumeUseCase.grant(
            GrantPointFromConsumeUseCase.ConsumePointCommand(
                userId = message.userId,
                amount = message.amount,
                type = message.type,
                referKey = message.referKey,
                idemKey = message.idemKey,
            ),
        )

        log.info(
            "포인트 처리 완료 | partition={} offset={} userId={} type={} amount={}",
            partition,
            offset,
            message.userId,
            message.type,
            message.amount,
        )
    }
}

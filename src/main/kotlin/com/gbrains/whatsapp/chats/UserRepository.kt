package com.gbrains.whatsapp.chats

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : R2dbcRepository<MessageEntity, Int>

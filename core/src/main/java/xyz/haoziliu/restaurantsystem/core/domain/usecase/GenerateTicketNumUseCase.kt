package xyz.haoziliu.restaurantsystem.core.domain.usecase

class GenerateTicketNumUseCase {
    operator fun invoke(): String {
        // MVP 策略：取当前时间戳后4位，或者 1000-9999 随机数
        // 优点：离线可用，不依赖服务器，冲突概率在小店可忽略
        return (1000..9999).random().toString()
    }
}

enum class UserLobbyState {
    OFFLINE, IDLE, SEARCHING, PLAYING
}

enum class GameAction {
    MOVE, PASS, FORFEIT
}

enum class UserColor {
    BLACK, WHITE
}

enum class StoneType {
    BLACK, WHITE, EMPTY
}

data class UserProfileDto(
    val userId: Long,
    val nickname: String,
    val score: Long,
    val isFriend: Boolean,
    val bio: String,
    val winsPerLosses: Float,
) {
    companion object {
        val INVALID = UserProfileDto(
            userId = -1L,
            nickname = "null",
            score = -1L,
            isFriend = false,
            bio = "null",
            winsPerLosses = -1.0f,
        )
    }
}

data class UserInviteDto(
    val userSenderId: Long,
    val userReceiverId: Long,
)

data class LeaderboardDto(
    val userId: Long,
    val userScore: Long,
)

enum class GameState {
    PLAYING, ONE_PASSED, SCORING, FINISHED
}

data class GameDto(
    val gameId: Long,
    val userWhiteId: Long,
    val userBlackId: Long,
    val state: GameState,
) {
    companion object {
        val INVALID = GameDto(
            gameId = -1L,
            userWhiteId = -1L,
            userBlackId = -1L,
            state = GameState.FINISHED,
        )
    }
}

data class GameActionDto(
    val userId: Long,
    val gameId: Long,
    val action: GameAction,
)

data class GameMessageDto(
    val gameId: Long,
    val authorId: Long,
    val content: String,
    val timestamp: String,
)

data class GameBoardDto(
    val cellX: Int,
    val cellY: Int,
    val cellType: UserColor?,
)

data class GameJournalPreviewDto(
    val gameId: Long,
    val timestamp: String,
)

data class GameJournalDto(
    val gameId: Long,
    val authorId: Long,
    val turnX: Int,
    val turnY: Int,
    val action: GameAction,
)

data class AuthResponseDto(
    val userId: Long,
    val token: String,
)

data class LoginDto(
    val nickname: String,
    val password: String,
)
data class RegisterDto(
    val nickname: String,
    val email: String,
    val password: String,
)

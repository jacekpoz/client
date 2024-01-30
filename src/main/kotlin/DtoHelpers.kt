import Globals.BASE_URL
import Globals.HTTP
import Globals.MOSHI
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

fun getLeaderboard(
    userId: Long,
    token: String,
): Result<List<UserProfileDto>> {
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/leaderboard/$userId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful)
            return Result.failure(IOException(response.message))

        val profileList = Types.newParameterizedType(List::class.java, UserProfileDto::class.java)

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter<List<UserProfileDto>>(profileList)
                .fromJson(body)
                ?: return Result.failure(Exception("Failed deserializing json: $body"))
        )
    }
}

fun getUserProfile(
    myId: Long,
    userId: Long,
    token: String,
): Result<UserProfileDto> {
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/user/profile?idAuthor=$myId&idAbout=$userId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        val body = response.body!!.string()

        val userProfileDto = MOSHI.adapter(UserProfileDto::class.java)
            .fromJson(body)
            ?: return Result.failure(Exception("Failed deserializing json: $body"))

        return Result.success(userProfileDto)
    }
}

enum class InviteType {
    FRIEND, GAME
}

fun sendInvite(
    invite: UserInviteDto,
    token: String,
    type: InviteType,
): Result<Unit> {
    val inviteDto = MOSHI.adapter(UserInviteDto::class.java)
        .toJson(invite)
    val pathBase = if (type == InviteType.FRIEND) "friend" else "game"
    val request = Request.Builder()
        .post(inviteDto.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .url("$BASE_URL/$pathBase/invite/send")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }
        return Result.success(Unit)
    }
}

fun acceptInvite(
    invite: UserInviteDto,
    token: String,
    type: InviteType,
): Result<UserInviteDto> {
    val inviteDto = MOSHI.adapter(UserInviteDto::class.java)
        .toJson(invite)
    val pathBase = if (type == InviteType.FRIEND) "friend" else "game"
    val request = Request.Builder()
        .post(inviteDto.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .url("$BASE_URL/$pathBase/invite/accept")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter(UserInviteDto::class.java)
                .fromJson(body)
                ?: return Result.failure(Exception("Failed deserializing json: $body"))
        )
    }
}

fun fetchInvites(
    myId: Long,
    token: String,
    type: InviteType,
): Result<List<UserInviteDto>> {
    val pathBase = if (type == InviteType.FRIEND) "friend" else "game"
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/$pathBase/invite/fetch/$myId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        val inviteList = Types.newParameterizedType(List::class.java, UserInviteDto::class.java)

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter<List<UserInviteDto>>(inviteList)
                .fromJson(body)
                ?: return Result.failure(Exception("Failed deserializing json: $body"))
        )
    }
}

fun getCurrentGame(
    myId: Long,
    token: String,
): Result<GameDto> {
    val request = Request.Builder()
        .get()
        .url("$BASE_URL/game/current/$myId")
        .header("Authorization", "Bearer $token")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter(GameDto::class.java)
                .fromJson(body)
                ?: return Result.failure(Exception("Failed deserializing json: $body"))
        )
    }
}

fun register(
    registerDto: RegisterDto,
): Result<AuthResponseDto> {
    val registerDtoJson = MOSHI.adapter(RegisterDto::class.java)
        .toJson(registerDto)

    val request = Request.Builder()
        .post(registerDtoJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .url("$BASE_URL/auth/register")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter(AuthResponseDto::class.java)
                .fromJson(body)
                ?: return Result.failure(Exception("Failed deserializing json: $body"))
        )
    }
}

fun login(
    loginDto: LoginDto,
): Result<AuthResponseDto> {
    val loginDtoJson = MOSHI.adapter(LoginDto::class.java)
        .toJson(loginDto)

    val request = Request.Builder()
        .post(loginDtoJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .url("$BASE_URL/auth/authenticate")
        .build()

    HTTP.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            return Result.failure(Exception(response.toString()))
        }

        val body = response.body!!.string()

        return Result.success(
            MOSHI.adapter(AuthResponseDto::class.java)
                .fromJson(body)
                ?: return Result.failure(Exception("Failed deserializing json: $body"))
        )
    }
}

fun sendAction(
    gameAction: GameJournalDto,
    token: String,
): Boolean {
    val actionDtoJson = MOSHI.adapter(GameJournalDto::class.java)
        .toJson(gameAction)

    val request = Request.Builder()
        .post(actionDtoJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
        .header("Authorization", "Bearer $token")
        .url("$BASE_URL/turn/send")
        .build()

    HTTP.newCall(request).execute().use { response ->
        return response.isSuccessful && response.code != 208
    }
}
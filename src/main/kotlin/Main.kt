import Globals.BASE_URL
import Globals.HTTP
import Globals.MOSHI
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
@Preview
fun App() {
    var loggedIn by remember { mutableStateOf(false) }
    var inGame by remember { mutableStateOf(false) }
    var accessToken by remember { mutableStateOf("") }
    var userProfile: UserProfileDto? = null
    var showError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var currentGame by remember { mutableStateOf(GameDto.INVALID) }

    val setError: (String?) -> Unit = {
        errorText = it ?: "Unknown error"
        showError = true
    }

    val afterLogin: (authDto: AuthResponseDto) -> Unit = afterLogin@{ authDto: AuthResponseDto ->
        accessToken = authDto.token

        val userProfileResult = getUserProfile(authDto.userId, authDto.userId, accessToken)

        if (userProfileResult.isFailure) {
            setError(userProfileResult.exceptionOrNull()!!.toString())
            return@afterLogin
        }

        userProfile = userProfileResult.getOrNull()!!

        loggedIn = true
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            if (!loggedIn) {
                continue
            }
            scope.launch scope@{
                val game = getCurrentGame(userProfile!!.userId, accessToken)
                if (game.isFailure) {
                    return@scope
                }
                currentGame = game.getOrNull()!!
                inGame = true
            }
        }
    }

    MaterialTheme {
        if (loggedIn) {
            if (inGame) {
                GameScreen(
                    myId = userProfile!!.userId,
                    game = currentGame,
                    token = accessToken,
                    // TODO implement this
                    //messages = listOf(
                    //    GameMessageDto(
                    //        gameId = 1L,
                    //        authorId = 1L,
                    //        content = "siema",
                    //        timestamp = "22:21:35",
                    //    ),
                    //    GameMessageDto(
                    //        gameId = 1L,
                    //        authorId = 2L,
                    //        content = "cześć",
                    //        timestamp = "22:21:50",
                    //    ),
                    //),
                    //onMessage = {

                    //},
                    onMove = onMove@{ gameJournalDto, stoneColor ->
                        val body = MOSHI.adapter(GameJournalDto::class.java)
                            .toJson(gameJournalDto)

                        val request = Request.Builder()
                            .post(body.toRequestBody("application/json; charset=utf-8".toMediaType()))
                            .url("$BASE_URL/game/turn/send")
                            .header("Authorization", "Bearer $accessToken")
                            .build()

                        val result = HTTP.newCall(request).execute().use newCall@{ response ->
                            if (!response.isSuccessful) {
                                return@newCall StoneType.EMPTY
                            }

                            return@newCall stoneColor
                        }

                        return@onMove result
                    },
                    onGameEnd = { winnerId ->
                        val body = MOSHI.adapter(UserProfileDto::class.java)
                            .toJson(userProfile)

                        val request = Request.Builder()
                            .post(body.toRequestBody("application/json; charset=utf-8".toMediaType()))
                            .url("$BASE_URL/game/${currentGame.gameId}/winner/$winnerId")
                            .header("Authorization", "Bearer $accessToken")
                            .build()

                        val result = HTTP.newCall(request).execute().use result@{ response ->
                            if (!response.isSuccessful) {
                                return@result Result.failure(Exception(response.toString()))
                            }

                            return@result Result.success(Unit)
                        }

                        if (result.isFailure) {
                            setError(result.exceptionOrNull()!!.toString())
                        }

                        inGame = false
                    }
                )
            } else {
                LobbyScreen(
                    currentProfile = userProfile!!,
                    showError = {
                        setError(it.toString())
                    },
                    token = accessToken,
                )
            }
        } else {
            LoginScreen(
                onLogin = onLogin@{ loginDto ->
                    val authDto = login(loginDto)

                    if (authDto.isFailure) {
                        setError(authDto.exceptionOrNull()!!.toString())
                        return@onLogin
                    }

                    afterLogin(authDto.getOrNull()!!)
                },
                onRegister = onRegister@{ registerDto: RegisterDto ->
                    val authDto = register(registerDto)

                    if (authDto.isFailure) {
                        setError(authDto.exceptionOrNull()!!.toString())
                        return@onRegister
                    }

                    afterLogin(authDto.getOrNull()!!)
                },
            )
        }
    }

    ErrorDialog(
        error = errorText,
        show = showError,
        onDismiss = { showError = false },
        onConfirm = { showError = false },
    )
}

fun main() = singleWindowApplication(
    title = "Go",
    state = WindowState(size = DpSize(800.dp, 600.dp))
) {
    App()
}

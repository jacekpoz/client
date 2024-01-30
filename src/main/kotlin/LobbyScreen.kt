import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LobbyScreen(
    currentProfile: UserProfileDto,
    showError: (Throwable) -> Unit,
    token: String,
) {
    var showCurrentProfile by remember { mutableStateOf(false) }
    var friendInvites by remember {
        mutableStateOf<List<UserInviteDto>>(emptyList())
    }
    var gameInvites by remember {
        mutableStateOf<List<UserInviteDto>>(emptyList())
    }
    var leaderboard by remember {
        mutableStateOf<List<UserProfileDto>>(emptyList())
    }

    LaunchedEffect(Unit) {
        while(true) {
            val friendInvitesResult = fetchInvites(
                myId = currentProfile.userId,
                token = token,
                type = InviteType.FRIEND,
            )
            if (friendInvitesResult.isSuccess) {
                friendInvites = friendInvitesResult.getOrDefault(emptyList())
            }

            val gameInvitesResult = fetchInvites(
                myId = currentProfile.userId,
                token = token,
                type = InviteType.GAME,
            )
            if (gameInvitesResult.isSuccess) {
                gameInvites = gameInvitesResult.getOrDefault(emptyList())
            }

            val leaderboardDto = getLeaderboard(currentProfile.userId, token)
            if (leaderboardDto.isSuccess) {
                leaderboard = leaderboardDto.getOrDefault(emptyList())
            }

            delay(5000)
        }
    }

    Row {
        Column {
            Button(
                onClick = {
                    showCurrentProfile = true
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(25.dp),
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.background,
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(40.dp),
                elevation = ButtonDefaults.elevation(
                    0.dp,
                    0.dp,
                    0.dp,
                    0.dp,
                    0.dp,
                ),
                shape = CircleShape,
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        )
        LazyColumn(
            horizontalAlignment = Alignment.End
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text("Nickname")
                    Text("Score")
                    Text("Wins per Losses")
                    Text("Game invite")
                    Text("Friend invite")
                }
            }
            items(leaderboard.size) { i ->
                val profile = leaderboard[i]
                val friendInvite = friendInvites.find {
                    (it.userSenderId == currentProfile.userId &&
                            it.userReceiverId == profile.userId) ||
                            (it.userSenderId == profile.userId &&
                                    it.userReceiverId == currentProfile.userId)
                }
                val gameInvite = gameInvites.find {
                    (it.userSenderId == currentProfile.userId &&
                            it.userReceiverId == profile.userId) ||
                            (it.userSenderId == profile.userId &&
                                    it.userReceiverId == currentProfile.userId)
                }

                UserProfileListable(
                    modifier = Modifier.fillMaxWidth(),
                    myId = currentProfile.userId,
                    profile = profile,
                    friendInvite = friendInvite,
                    gameInvite = gameInvite,
                    onFriendInvite = { accept ->
                        if (!accept) {
                            val result = sendInvite(
                                invite = UserInviteDto(
                                    userSenderId = currentProfile.userId,
                                    userReceiverId = profile.userId,
                                ),
                                token = token,
                                type = InviteType.FRIEND,
                            )
                            if (result.isFailure) {
                                showError(result.exceptionOrNull()!!)
                            }
                        } else {
                            val result = acceptInvite(
                                invite = UserInviteDto(
                                    userSenderId = profile.userId,
                                    userReceiverId = currentProfile.userId,
                                ),
                                token = token,
                                type = InviteType.FRIEND,
                            )
                            if (result.isFailure) {
                                showError(result.exceptionOrNull()!!)
                            }
                        }
                    },
                    onGameInvite = { accept ->
                        if (!accept) {
                            val result = sendInvite(
                                invite = UserInviteDto(
                                    userSenderId = currentProfile.userId,
                                    userReceiverId = profile.userId,
                                ),
                                token = token,
                                type = InviteType.GAME,
                            )
                            if (result.isFailure) {
                                showError(result.exceptionOrNull()!!)
                            }
                        } else {
                            val result = acceptInvite(
                                invite = UserInviteDto(
                                    userSenderId = profile.userId,
                                    userReceiverId = currentProfile.userId,
                                ),
                                token = token,
                                type = InviteType.GAME,
                            )
                            if (result.isFailure) {
                                showError(result.exceptionOrNull()!!)
                            }
                        }
                    },
                )
            }
        }
    }

    if (showCurrentProfile) {
        UserProfilePopup(
            profile = currentProfile,
            onDismissRequest = { showCurrentProfile = false },
        )
    }
}
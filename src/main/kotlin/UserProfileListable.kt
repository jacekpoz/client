import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileListable(
    modifier: Modifier = Modifier,
    myId: Long,
    profile: UserProfileDto,
    onFriendInvite: (Boolean) -> Unit = {},
    onGameInvite: (Boolean) -> Unit = {},
    friendInvite: UserInviteDto? = null,
    gameInvite: UserInviteDto? = null,
) {
    val isInvitedToFriendsByMe = if (friendInvite == null) false
        else friendInvite.userSenderId == myId &&
            friendInvite.userReceiverId == profile.userId

    val didInviteMeToFriends = if (friendInvite == null) false
        else friendInvite.userSenderId == profile.userId &&
            friendInvite.userReceiverId == myId

    val isInvitedToGameByMe = if (gameInvite == null) false
        else gameInvite.userSenderId == myId &&
            gameInvite.userReceiverId == profile.userId

    val didInviteMeToGame = if (gameInvite == null) false
        else gameInvite.userSenderId == profile.userId &&
            gameInvite.userReceiverId == myId

    val isMe = myId == profile.userId

    Row(
        modifier = modifier
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            text = profile.nickname,
            fontWeight = FontWeight.Bold,
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color.Gray,
        )
        Text(
            text = profile.score.toString(),
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color.Gray,
        )
        Text(
            text = profile.winsPerLosses.toString(),
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = Color.Gray,
        )
        Button(
            onClick = {
                if (isMe || isInvitedToGameByMe)
                    return@Button
                onGameInvite(didInviteMeToGame)
            },
        ) {
            Text(
                if (isMe) "This is you!"
                else if (didInviteMeToGame) "Accept game invite"
                else if (isInvitedToGameByMe) "Already invited"
                else "Invite to game")
        }
        Button(
            onClick = {
                if (isMe || isInvitedToFriendsByMe)
                    return@Button
                onFriendInvite(didInviteMeToFriends)
            },
        ) {
            Text(
                if (isMe) "This is you!"
                else if (didInviteMeToFriends) "Accept friend invite"
                else if (isInvitedToFriendsByMe) "Already invited"
                else "Invite friend")
        }
    }
}
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun UserProfilePopup(
    profile: UserProfileDto,
    onDismissRequest: () -> Unit,
) {
    Popup(
        focusable = true,
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(
                        text = profile.nickname,
                        textAlign = TextAlign.Center,
                    )
                }
                Divider()
                Text(
                    text = profile.bio,
                )
            }
        }
    }
}
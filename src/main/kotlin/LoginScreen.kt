import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogin: suspend (LoginDto) -> Unit,
    onRegister: suspend (RegisterDto) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val scope = rememberCoroutineScope()

        Column {
            var nickname by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            var isNicknameError by remember { mutableStateOf(false) }
            var isPasswordError by remember { mutableStateOf(false) }

            Text(
                text = "Login"
            )
            TextField(
                value = nickname,
                onValueChange = {
                    isNicknameError = false
                    if (it.length <= 64)
                        nickname = it
                },
                label = { Text("Username") },
                isError = isNicknameError,
            )
            TextField(
                value = password,
                onValueChange = {
                    isPasswordError = false
                    if (it.length <= 64)
                        password = it
                },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isError = isPasswordError,
            )
            Button(
                onClick = {
                    if (nickname.isBlank()) {
                        error = "Username can't be blank"
                        isNicknameError = true
                        return@Button
                    }
                    if (password.isBlank()) {
                        error = "Password can't be blank"
                        isPasswordError = true
                        return@Button
                    }

                    scope.launch {
                        onLogin(LoginDto(nickname, password))
                    }
                },
            ) {
                Text("Login")
            }
            Text(
                text = error,
                color = Color.Red,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            var nickname by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            var isNicknameError by remember { mutableStateOf(false) }
            var isEmailError by remember { mutableStateOf(false) }
            var isPasswordError by remember { mutableStateOf(false) }

            Text(
                text = "Register"
            )
            TextField(
                value = nickname,
                onValueChange = {
                    isNicknameError = false
                    if (it.length <= 64)
                        nickname = it
                },
                label = { Text("Username") },
            )
            TextField(
                value = email,
                onValueChange = {
                    isEmailError = false
                    if (it.length <= 64)
                        email = it
                },
                label = { Text("Email") },
            )
            TextField(
                value = password,
                onValueChange = {
                    isPasswordError = false
                    if (it.length <= 64)
                        password = it
                },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
            )
            Button(
                onClick = {
                    if (nickname.isBlank()) {
                        error = "Username can't be blank"
                        return@Button
                    }
                    if (email.isBlank()) {
                        error = "Email can't be blank"
                        return@Button
                    }
                    if (password.isBlank()) {
                        error = "Password can't be blank"
                        return@Button
                    }

                    scope.launch {
                        onRegister(RegisterDto(nickname, email, password))
                    }
                }
            ) {
                Text("Register")
            }
            Text(
                text = error,
                color = Color.Red,
            )
        }
    }
}
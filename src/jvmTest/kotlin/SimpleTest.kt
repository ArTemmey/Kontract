import io.ktor.http.*
import io.ktor.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import ru.impression.kontract.Ok
import ru.impression.kontract.method
import ru.impression.kontract.route

class SimpleTest {
    @Test
    fun run() {
        print(Json.decodeFromString<Int>("1"))
    }

    fun Route.myApi() {
        route<MyApi.User> {
            method(MyApi.User::get) {
                contract.userId!!.let {

                }
                status = HttpStatusCode.Unauthorized
                Ok(UserEntity(123, "name"))
            }
        }

        route<MyApi.Users> {
            method(MyApi.Users::post) {
                Ok(it)
            }
        }
    }
}
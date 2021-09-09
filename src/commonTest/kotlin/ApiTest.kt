import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import ru.impression.kontract.ApiContract
import ru.impression.kontract.ApiResult
import ru.impression.kontract.Ok
import ru.impression.kontract.request
import kotlin.test.Test


@Serializable
class UserEntity(val id: Int, val name: String)

@Serializable
class ProjectEntity(val id: Int, val name: String)

sealed class MyApi : ApiContract() {

    companion object {
        const val BASE_URL = "https://example.com/api"
    }

    class Users : MyApi() {
        override val path = "/users"

        fun post(userEntity: UserEntity) = post<UserEntity>(userEntity)
    }

    class User : MyApi() {
        override val path = "/users/{id}"

        var userId by pathParam<Int>("id")

        fun get() = get<UserEntity>()
    }
}


class ApiTest {

    @Test
    fun test() {
    }

    suspend fun testClient(client: HttpClient) {
        client.request(MyApi.BASE_URL) {
            MyApi.User().apply { userId = 123 }.get()
        }
    }
}

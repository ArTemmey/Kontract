import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.impression.kontract.ApiContract
import ru.impression.kontract.execute

private sealed class TestApi : ApiContract() {

    init {
        baseUrl = "https://api.erfgwergwrtg.com"
    }

    class Endpoint : TestApi() {
        override val path = "/test"

        var something by queryParam<String>("something")

        fun get() = get<String>()
    }
}

class ClientTest {

    val client = HttpClient {
        Logging {
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    @Test
    fun makeReq() {
        runBlocking {
            TestApi.Endpoint().apply { something = "aierjqorigqeigo" }.get().execute(client)
        }
    }
}
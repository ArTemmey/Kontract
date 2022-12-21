Framework for interaction between Ktor server and Ktor client.

### Usage example

Common code (API contract):
```kotlin
sealed class Api : ApiContract() {

    var accessToken by header(HttpHeaders.Authorization)

    class Sessions : Api() {
        override val path = "/sessions"

        fun post(body: SessionsPostIn) = post<domain.entity.Session>(body)
    }

    class Users : Api() {
        override val path = "/users"

        fun post(body: domain.entity.User) = post<domain.entity.User>(body)

        fun put(body: domain.entity.User) = put<Unit>(body)
    }

    class User : Api() {
        override val path = "/users/current"

        fun get() = get<domain.entity.User>()
    }
}
```

Server code:
```kotlin
method(Api.Users::post) { postUser(it) }

method(Api.Users::put) { put(it, koinGet<UserDao>()) }

method(Api.Sessions::post) { postSession(it) }
```

Client code:
```kotlin
Api.Sessions().post(SessionsPostIn(email, pass)).execute()
    .onSuccess { saveSession(it) }
```

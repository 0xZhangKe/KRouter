![](https://img.shields.io/maven-central/v/io.github.0xzhangke/krouter-runtime)

# KRouter2

Due to time constraints, the first version of KRouter was hastily developed and only barely functional. Recently, I had some free time to redesign and refine its usage, adding support for Kotlin Multiplatform and parameter injection. The implementation was also completely revamped, replacing ServiceLoader with KSP for collecting route information. Additionally, it has been published to the Maven Central Repository.

## How to Use

The usage remains as simple and straightforward as before. First, annotate the target class:

```kotlin
@Destination("screen/main")
class MainScreen(
    @RouteParam("id") val id: String,
    @RouteParam("name") val name: String,
): Screen
```

Then, use `KRouter` to retrieve the corresponding class:

```kotlin
val screen = KRouter.route<Screen>("screen/main?name=zhangke&id=123")
```

KRouter currently provides three annotations: `@Destination`, `@RouteUri`, and `@RouterParam`.

### @Destination

As the name suggests, the `@Destination` annotation marks the target class of a route, i.e., the destination, and takes a parameter as the route address.

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Destination(val route: String)
```

It can be used as follows:

```kotlin
@Destination("screen/profile/detail")
class ProfileDetailScreen: Screen
```

### @RouterParam

The `@RouterParam` annotation is used to mark route parameters. The fields marked by this annotation will be automatically injected with the corresponding route parameters. It also takes a parameter that represents the name of the query field in the route. KRouter will parse and assign values based on this field name.

```kotlin
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class RouteParam(val name: String)
```

Here’s an example of how to use it:

```kotlin
@Destination("screen/home/detail")
class HomeDetailScreen(
    @RouteParam("id") val id: String,
) : Screen {
}
```

In addition to constructor parameter injection, property field injection is also supported.

```kotlin
@Destination("screen/home/detail")
class HomeDetailScreen(
    @RouteParam("id") val id: String,
) : Screen {

    @RouteParam("title") var title: String? = null
}
```

Currently, parameter types are limited to basic types and `String`. For more complex types, you can convert the object to a JSON string and encode it into the route.

<aside>
💡

Please note that KSP cannot access default values for parameters and properties at the moment. Therefore, injected fields cannot have default values. This means that if your injected field has a default value, and the route does not include that parameter, the default value will be ignored.

</aside>

### @RouteUri

Parameters or properties injected with this annotation will be assigned the full route. Since `@RouterParam` can only be used for field injection, for more complex parsing scenarios, you can use `@RouteUri` to retrieve the full route.

### KRouterModule

`KRouterModule` is an interface used to implement specific routing capabilities. You can dynamically add custom modules through the `KRouter` class. By default, it will use the dynamically added modules first; if routing fails, it will fall back to KRouter's internal routing.

```kotlin
interface KRouterModule {
   fun route(uri: String): Any?
}
```

### Adding Dependencies

KRouter provides two KSP plugins:

- `krouter-collecting-compiler`: Collects route information, used in non-main modules.
- `krouter-reducing-compiler`: Aggregates route information from various modules, only used in the main project module (app module).

```kotlin
// Used in non-main modules
ksp("io.github.0xzhangke:krouter-collecting-compiler:$latest_version")
// Used in the main module
ksp("io.github.0xzhangke:krouter-reducing-compiler:$latest_version")
```

Additionally, there is an annotation module and a runtime module:

- `krouter-runtime`: The runtime module, providing `KRouter`, `KRouterModule`, and annotations.
- `krouter-annotation`: The annotation module, containing only the annotations.

```kotlin
// For modules that only need to use annotations
ksp("io.github.0xzhangke:krouter-runtime:$latest_version")
// For modules that need routing capabilities
ksp("io.github.0xzhangke:krouter-annotation:$latest_version")
```

## Implementation Details

First, the `collecting-compiler` plugin collects all route target class information in the module and generates a `KRouterModule` belonging to the current module. The generated class might look like this:

```kotlin
public class RouterCollection_1726153189290() : KRouterModule {
    override fun route(uri: String): Any? {
        val routerUri = com.zhangke.krouter.internal.KRouterUri.create(uri)
        return when (routerUri.baseUrl) {
            "screen/home/detail" -> {
                com.zhangke.krouter.sample.home.HomeDetailScreen(
                    id = routerUri.requireQuery("id"),
                )
            }
            "screen/home/landing" -> {
                com.zhangke.krouter.sample.home.HomeLandingScreen(
                    router = uri,
                )
            }
            else -> null
        }
    }
}
```

All modules that depend on the `collecting-compiler` plugin will generate a class like this.

Then, in the main project module (typically the app module), the `reducing-compiler` plugin is needed. This plugin generates a class with a fixed package name and class name, gathers all the classes generated by `collecting-compiler`, and adds them to the new class.

```kotlin
public class AutoReducingModule() : KRouterModule {

  private val moduleList: List<KRouterModule> = listOf<KRouterModule>(
          com.zhangke.krouter.generated.RouterCollection_1726153189283(),
          com.zhangke.krouter.generated.RouterCollection_1726153189290(),
          com.zhangke.krouter.generated.RouterCollection_1726153189284(),
          com.zhangke.krouter.generated.RouterCollection_1726153189709()
      )

  override fun route(uri: String): Any? = moduleList.firstNotNullOfOrNull { it.route(uri) }
}
```

At runtime, KRouter uses reflection to create this class, and the routing process is delegated to the implementation in this class. This completes the entire process of route collection and implementation.

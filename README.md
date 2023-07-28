# KRouter
Find interface implementation by uri string.

The main purpose is to open the compose page from other modules(e.g. [Voyager](https://voyager.adriel.cafe/navigation)).

# Usage
First, define a interface.
```kotlin
interface Screen
```
After that, define some implementation.
```kotlin
@Router("screen/home")
class HomeScreen: Screen

@Router("screen/profile")
class ProfileScreen: Screen

@Router("screen/setting")
class SettingScreen : Screen
```
This implementation can be distributed to any modules.

Now, you can route to any Screen by router.
```kotlin
val screen = KRouter.route<Screen>("scree/home/detail")
```

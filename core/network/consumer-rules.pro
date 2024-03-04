-dontwarn com.omar.musica.network.model.**

-keepclasseswithmembers class com.omar.musica.network.model.** { *; }

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
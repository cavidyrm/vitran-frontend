# VitranShop release ProGuard / R8 rules.
# Prefer narrow keeps — do not use blanket -keep class ** { *; }

-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod

# Kotlinx Serialization
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keep,includedescriptorclasses class **$$serializer { *; }
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Koin
-keep class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# Room 3 generated
-keep class * extends androidx.room3.RoomDatabase { *; }
-keep @androidx.room3.Entity class *
-dontwarn androidx.room3.paging.**

# Ktor / OkHttp engine bits
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.slf4j.**

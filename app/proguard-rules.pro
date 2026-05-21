# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.sonzaix.streaming.data.local.** { *; }
-keep class com.sonzaix.streaming.domain.model.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**

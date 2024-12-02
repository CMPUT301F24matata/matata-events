# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Keep all gRPC-related classes
-keep class io.grpc.** { *; }
# Keep Firebase Firestore classes
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firestore.v1.** { *; }
-keep class com.google.protobuf.** { *; }
-keep class com.google.android.gms.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
# Firebase SDK general rules
-keep class com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
# Protobuf Lite
-keep class com.google.protobuf.** { *; }
-keep class com.google.type.** { *; }
-dontwarn com.google.protobuf.**

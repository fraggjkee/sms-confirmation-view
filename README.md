# SmsConfirmationView
A custom Android's `View` implementing all the necessary UI for a typical "enter SMS code" flow.
<img src="images/demo.png?raw=true" width="400">

# Installation
**Step 1.** Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```
**Step 2.** Add the dependency
```gradle
dependencies {
    implementation "com.github.fraggjkee:sms-confirmation-view:1.0"
}
```

# Usage
Add `SmsConfirmationView` to your XML layout:
```xml
<com.fraggjkee.smsconfirmationview.SmsConfirmationView
    android:id="@+id/sms_code_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

...and then just listen for its updates in your `Activity` or `Fragment`:

```kotlin
val view: SmsConfirmationView = findViewById(R.id.sms_code_view)
view.onChangeListener = SmsConfirmationView.OnChangeListener { code, isComplete ->
   // TODO...           
}
```

# Customization
Here's the list of available XML attributes:
- `scv_codeLength`: expected confirmation code length. Default value = [4](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/java/com/fraggjkee/smsconfirmationview/SmsConfirmationView.kt#L186)
- `scv_symbolsSpacing`: gap between individual symbol subviews. Default value = [8dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L4)
- `scv_symbolWidth`: width of each individual symbol cell. Default value = [42dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L6)
- `scv_symbolHeight`: height of each individual symbol cell. Default value = [48dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L7)
- `scv_symbolTextColor`: text color used to draw text within symbol subviews. Default value = `?attr/colorOnSurface` or `Color.BLACK` if such attribute is not defined in your app's theme.
- `scv_symbolTextSize`: text size used within symbol subviews. Default value = [22sp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L8)
- `scv_symbolBackgroundColor`: filler color for symbol subviews. Default value = `?attr/colorSurface` or `Color.BLACK` is such attribute is not defined in your app's theme.
- `scv_symbolBorderColor`: color to use for symbol subview's stroke outline. Default value = `?attr/colorSurface` or `Color.BLACK` if such attribute is not defined in your app's theme.
- `scv_symbolBorderWidth`: thickness of the stroke used to draw symbol subview's border. Default value = [2dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L9)
- `scv_symbolBorderCornerRadius`: corner radius for symbol subview's border. Default value = [2dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L5)

All of these attributes can also be changed **programatically** (XML customization is the preferred way though), check out the list of available extensions [here](https://github.com/fraggjkee/SmsConfirmationView/blob/master/library/src/main/java/com/fraggjkee/smsconfirmationview/SmsConfirmationViewExt.kt).

License
----
Apache 2.0

# SmsConfirmationView [![](https://jitpack.io/v/fraggjkee/sms-confirmation-view.svg)](https://jitpack.io/#fraggjkee/sms-confirmation-view)
A custom Android's `View` implementing all the necessary UI for a typical "enter SMS / PIN code" flow. Can be used for verification of any digit-based codes (SMS verification, PIN verification, etc.).

Supports **automatic code retrieval from incoming SMS messages**. This feature is implemented using the [Consent API](https://developers.google.com/identity/sms-retriever/user-consent/overview).

Supports **copy-pasting from the clipboard** by showing a floating pop-up in response to a long press event (similarly to `EditText`). 

You may find my initial motivations & a little bit more detailed information in this [Medium post](https://medium.com/swlh/implementing-the-complete-sms-verification-flow-using-consent-api-in-android-ae0327f74658).

<img src="/images/screenshot2.png"  width="250">
<img src="/images/screenshot1.png"  width="250">

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
    implementation "com.github.fraggjkee:sms-confirmation-view:1.8.1"
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

You cal also get/set the code using the `enteredCode` property.

# DataBinding
This SMS verification view supports Android's DataBinding framework, including its two-way version. The list of available adapters can be found [here](https://github.com/fraggjkee/sms-confirmation-view/blob/master/library/src/main/java/com/fraggjkee/smsconfirmationview/BindingAdapters.kt).

# Customization
<img src="images/demo.png?raw=true" width="400">
Here's the list of available XML attributes:

- `scv_codeLength`: expected confirmation code length. Default value = [4](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/java/com/fraggjkee/smsconfirmationview/SmsConfirmationView.kt#L186)
- `scv_showCursor`: controls whether the blinking cursor should be displayed for an active symbol. Default value = `true`. Uses the color specified by `scv_symbolBorderActiveColor`.
- `scv_pasteEnabled`: enables or disables Copy/Paste support. Default value = `true`.
- `scv_symbolsSpacing`: gap between individual symbol subviews. Default value = [8dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L4)
- `scv_symbolWidth`: width of each individual symbol cell. Default value = [42dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L6)
- `scv_symbolHeight`: height of each individual symbol cell. Default value = [48dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L7)
- `scv_symbolTextColor`: text color used to draw text within symbol subviews. Default value = `?attr/colorOnSurface` or `Color.BLACK` if such attribute is not defined in your app's theme.
- `scv_symbolTextSize`: text size used within symbol subviews. Default value = [22sp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L8)
- `scv_symbolTextFont`: text font used within symbol subviews. Default value = theme default font
- `scv_symbolBackgroundColor`: filler color for symbol subviews. Default value = `?attr/colorSurface` or `Color.BLACK` if such attribute is not defined in your app's theme.
- `scv_symbolBorderColor`: color to use for symbol subview's stroke outline. Default value = `?attr/colorSurface` or `Color.BLACK` if such attribute is not defined in your app's theme.
- `scv_symbolBorderActiveColor`: color to use for symbol subview's stroke outline for an active symbol. Default value = `scv_symbolBorderColor`.
- `scv_symbolBorderWidth`: thickness of the stroke used to draw symbol subview's border. Default value = [2dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L9)
- `scv_symbolBorderCornerRadius`: corner radius for symbol subview's border. Default value = [2dp](https://github.com/fraggjkee/SmsConfirmationView/blob/fb2be87c0510a10a95b343f79380de72f6fe7742/library/src/main/res/values/dimens.xml#L5)

All of these attributes can also be changed **programmatically** (XML customization is the preferred way though), check out the list of available extensions [here](https://github.com/fraggjkee/SmsConfirmationView/blob/master/library/src/main/java/com/fraggjkee/smsconfirmationview/SmsConfirmationViewExt.kt).

# SMS Detection modes
- `app:scv_smsDetectionMode="disabled"`: Prevents the view from using SMS Consent API, i.e. this option simply disables automatic SMS detection.
- `app:scv_smsDetectionMode="auto"`: Default option. `SmsConfirmationView` will try to use SMS Consent API to detect incoming messages and extract confirmation codes out of the messages.
- `app:scv_smsDetectionMode="manual"`: Like `auto` but gives you more control when to actually start listening for incoming messages via `startListeningForIncomingMessages` method. Can be useful in some cases as SMS Consent API cannot be active for more than 5 minutes.

License
----
Apache 2.0
